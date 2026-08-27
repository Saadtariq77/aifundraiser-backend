package com.baitussalam.aifundraiser.service;

import com.baitussalam.aifundraiser.common.ConfidenceLevel;
import com.baitussalam.aifundraiser.config.ScoringProperties;
import com.baitussalam.aifundraiser.entity.OpportunitySignal;
import com.baitussalam.aifundraiser.entity.OpportunityScore;
import com.baitussalam.aifundraiser.repository.OpportunitySignalRepository;
import com.baitussalam.aifundraiser.repository.OpportunityScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class OpportunityScoringService {

    private final OpportunitySignalRepository signalRepo;
    private final OpportunityScoreRepository scoreRepo;
    private final ScoringProperties scoringProperties;
    private final ObjectMapper objectMapper;

    @Transactional
    public OpportunityScore calculateFor(String country, String category) {
        List<OpportunitySignal> signals = signalRepo.findByCountryAndCategory(country, category);
        if (signals.isEmpty()) return null;

        Map<String, Double> weights = scoringProperties.getWeights();
        double weightedSum = 0, totalWeight = 0;
        Map<String, Double> breakdown = new HashMap<>();
        List<String> evidences = new ArrayList<>();

        for (OpportunitySignal s : signals) {
            Double w = weights.getOrDefault(s.getSignalType(), 0.0);
            if (w == 0) continue;
            weightedSum += s.getNormalizedScore() * w;
            totalWeight += w;
            breakdown.put(s.getSignalType(), s.getNormalizedScore());
            if (s.getEvidence() != null && s.getEvidence().has("summary"))
                evidences.add(s.getEvidence().get("summary").asText());
        }

        double finalScore = totalWeight == 0 ? 0 : weightedSum / totalWeight;
        finalScore = Math.round(finalScore * 10) / 10.0;

        // UPDATED: max 5 signals -> HIGH if 4+, MEDIUM if 3, LOW otherwise
        String confidence = signals.size() >= 4 ? ConfidenceLevel.HIGH.toString() : signals.size() >= 3 ? ConfidenceLevel.MEDIUM.toString() : ConfidenceLevel.LOW.toString();

        Map<String, Object> bundle = new HashMap<>();
        bundle.put("country", country);
        bundle.put("category", category);
        bundle.put("totalScore", finalScore);
        bundle.put("confidence", confidence);
        bundle.put("signals", breakdown);
        bundle.put("evidences", evidences);
        bundle.put("signalCount", signals.size());

        OpportunityScore score = scoreRepo.findByCountryAndCategory(country, category)
                .orElse(OpportunityScore.builder().country(country).category(category).build());

        score.setTotalScore(finalScore);
        score.setConfidence(confidence);
        score.setBreakdown(objectMapper.valueToTree(breakdown));
        score.setEvidenceBundle(objectMapper.valueToTree(bundle));
        score.setCalculatedAt(LocalDateTime.now());

        return scoreRepo.save(score);
    }
}