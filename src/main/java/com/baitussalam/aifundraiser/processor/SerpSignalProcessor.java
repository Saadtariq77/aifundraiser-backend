package com.baitussalam.aifundraiser.processor;

import com.baitussalam.aifundraiser.common.SignalType;
import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.entity.OpportunitySignal;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class SerpSignalProcessor implements SignalProcessor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(ExternalApiRawData rawData) {
        return Source.SERP.toString().equals(rawData.getSource());
    }

    @Override
    public OpportunitySignal process(ExternalApiRawData rawData) {
        try {
            JsonNode response = rawData.getResponseData();
            JsonNode timeline = response.path("interest_over_time").path("timeline_data");
            if (!timeline.isArray() || timeline.size() == 0) {
                return buildSignal(rawData, 0, 0, "No timeline data");
            }
            int n = timeline.size();
            int startLast4 = Math.max(0, n - 4);
            int startPrev4 = Math.max(0, n - 8);

            double last4Avg = calculateAvg(timeline, startLast4, n);
            double prev4Avg = calculateAvg(timeline, startPrev4, startLast4);
            double max = findMax(timeline);

            double intensity = last4Avg; // already 0-100
            double velocity = Math.max(0, last4Avg - prev4Avg); // 0-100

            double normalized = (intensity * 0.6) + (velocity * 2.0 * 0.4); // velocity weighted more
            if (max == 100) normalized = Math.min(100, normalized + 20); // spike bonus
            normalized = Math.min(100, Math.max(0, normalized));

            String trend = velocity > 10 ? "RISING" : velocity > 3 ? "SLIGHT_RISE" : "STABLE";
            Map<String, Object> evMap = new HashMap<>();
            evMap.put("last4WeeksAvg", Math.round(last4Avg * 10) / 10.0);
            evMap.put("prev4WeeksAvg", Math.round(prev4Avg * 10) / 10.0);
            evMap.put("maxSpike", max);
            evMap.put("trend", trend);
            evMap.put("summary", String.format("Search interest for '%s' avg %.1f last 4 weeks vs %.1f previous, max spike %.0f. Trend: %s", rawData.getCategory(), last4Avg, prev4Avg, max, trend));

            return OpportunitySignal.builder()
                    .country(rawData.getCountry())
                    .category(rawData.getCategory())
                    .signalType(SignalType.SEARCH_INTEREST.toString())
                    .source(Source.SERP.toString())
                    .rawValue(last4Avg)
                    .normalizedScore(normalized)
                    .evidence(objectMapper.valueToTree(evMap))
                    .calculatedAt(LocalDateTime.now())
                    .build();
        } catch (Exception e) {
            log.error("SERP processing failed id={}", rawData.getId(), e);
            return buildSignal(rawData, 0, 0, "Error: " + e.getMessage());
        }
    }

    private double calculateAvg(JsonNode timeline, int start, int end) {
        if (start >= end) return 0;
        double sum = 0; int count = 0;
        for (int i = start; i < end; i++) {
            JsonNode values = timeline.get(i).path("values");
            double weekMax = 0;
            for (JsonNode v : values) weekMax = Math.max(weekMax, v.path("extracted_value").asDouble(0));
            sum += weekMax; count++;
        }
        return count == 0 ? 0 : sum / count;
    }

    private double findMax(JsonNode timeline) {
        double max = 0;
        for (JsonNode t : timeline) for (JsonNode v : t.path("values")) max = Math.max(max, v.path("extracted_value").asDouble(0));
        return max;
    }

    private OpportunitySignal buildSignal(ExternalApiRawData raw, double rawVal, double norm, String msg) {
        Map<String,Object> m = Map.of("summary", msg);
        return OpportunitySignal.builder().country(raw.getCountry()).category(raw.getCategory())
                .signalType(SignalType.SEARCH_INTEREST.toString()).source(Source.SERP.toString())
                .rawValue(rawVal).normalizedScore(norm).evidence(new ObjectMapper().valueToTree(m))
                .calculatedAt(LocalDateTime.now()).build();
    }
}