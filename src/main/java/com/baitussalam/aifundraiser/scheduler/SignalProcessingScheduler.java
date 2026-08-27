package com.baitussalam.aifundraiser.scheduler;

import com.baitussalam.aifundraiser.service.SignalProcessingService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SignalProcessingScheduler {
    private final SignalProcessingService processingService;

    @Scheduled(fixedDelay = 3600000) // every hour
    public void run() {
        log.info("Running scheduled signal processing");
        processingService.processUnprocessed();
    }
}