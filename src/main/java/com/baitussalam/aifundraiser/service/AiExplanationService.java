package com.baitussalam.aifundraiser.service;

import com.baitussalam.aifundraiser.entity.OpportunityScore;
import com.baitussalam.aifundraiser.geminiClient.service.GeminiService;
import com.baitussalam.aifundraiser.repository.OpportunityScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiExplanationService {

    private final GeminiService geminiService;
    private final OpportunityScoreRepository scoreRepo;

    public String explain(Long scoreId) {
        OpportunityScore score = scoreRepo.findById(scoreId)
                .orElseThrow(() -> new RuntimeException("OpportunityScore not found: " + scoreId));
        return explain(score);
    }

    public String explain(OpportunityScore score) {
        String evidenceJson = score.getEvidenceBundle() != null ? score.getEvidenceBundle().toString() : "{}";

        String systemPrompt = """
                You are NOIP assistant for Baitussalam.
                DO NOT calculate scores. Score is pre-calculated.
                Return response in MARKDOWN with this exact structure:
                ### 🎯 Recommended Campaign Title
                One line title
                ### 📅 Launch Window
                NOW / Next 2 Weeks / Next Month + 1 line why
                ### 💡 Why This Score (4 bullets defining each evidence)
                - bullet 1 with evidence number
                - bullet 2
                - bullet 3
                - bullet 4
                ### ⚠️ Risks & Confidence
                1 line
                Keep under 180 words. Use only provided evidence. No hallucinations.
                """;

        String userPrompt = String.format("""
                Explain why category '%s' in country '%s' has opportunity score %.1f with confidence %s.
                
                Evidence Bundle (deterministic scores 0-100):
                %s
                
                Tasks:
                1. Suggest a Campaign Title
                2. Suggest Launch Window (NOW / Next 2 Weeks / Next Month)
                3. Give 3 bullet points reasoning with evidence
                """, score.getCategory(), score.getCountry(), score.getTotalScore(), score.getConfidence(), evidenceJson);

        try {
            return geminiService.generateContent(systemPrompt, userPrompt);
        } catch (Exception e) {
            log.error("AI explanation failed for score {}", score.getId(), e);
            return "AI explanation unavailable. Raw evidence: " + evidenceJson;
        }
    }
}