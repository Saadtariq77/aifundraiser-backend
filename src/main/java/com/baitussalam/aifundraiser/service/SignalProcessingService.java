package com.baitussalam.aifundraiser.service;

import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.entity.OpportunitySignal;
import com.baitussalam.aifundraiser.processor.SignalProcessor;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.repository.OpportunitySignalRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SignalProcessingService {

    private final ExternalApiRawDataRepository rawRepo;
    private final OpportunitySignalRepository signalRepo;
    private final List<SignalProcessor> processors;
    private final OpportunityScoringService scoringService;

    @Transactional
    public void processUnprocessed() {
        List<ExternalApiRawData> raws = rawRepo.findByProcessedFalse();
        log.info("Found {} unprocessed raw records", raws.size());
        for (ExternalApiRawData raw : raws) {
            try {
                SignalProcessor processor = processors.stream().filter(p -> p.supports(raw)).findFirst().orElse(null);
                if (processor == null) { log.warn("No processor for source {}", raw.getSource()); continue; }
                OpportunitySignal signal = processor.process(raw);
                if (signal == null) continue;

                signalRepo.findByCountryAndCategoryAndSignalType(signal.getCountry(), signal.getCategory(), signal.getSignalType())
                        .ifPresentOrElse(existing -> {
                            existing.setRawValue(signal.getRawValue());
                            existing.setNormalizedScore(signal.getNormalizedScore());
                            existing.setEvidence(signal.getEvidence());
                            existing.setCalculatedAt(signal.getCalculatedAt());
                            signalRepo.save(existing);
                        }, () -> signalRepo.save(signal));

                raw.setProcessed(true);
                rawRepo.save(raw);

                scoringService.calculateFor(signal.getCountry(), signal.getCategory());
            } catch (Exception e) {
                log.error("Failed to process raw id {}", raw.getId(), e);
            }
        }
    }
}