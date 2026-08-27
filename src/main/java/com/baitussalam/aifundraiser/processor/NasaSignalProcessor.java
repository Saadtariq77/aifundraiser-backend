package com.baitussalam.aifundraiser.processor;

import com.baitussalam.aifundraiser.common.SignalType;
import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.entity.OpportunitySignal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class NasaSignalProcessor implements SignalProcessor {

    private final ObjectMapper objectMapper;

    @Override public boolean supports(ExternalApiRawData raw) { return Source.NASA.toString().equals(raw.getSource()); }

    @Override
    public OpportunitySignal process(ExternalApiRawData raw) {
        JsonNode data = raw.getResponseData();
        int count = 0;
        if (data.has("events") && data.get("events").isArray()) count = data.get("events").size();
        else if (data.isArray()) count = data.size();

        double normalized = switch (count) {
            case 0 -> 0;
            case 1 -> 60;
            case 2 -> 80;
            default -> 100; // 3+ disasters = 100
        };

        Map<String,Object> ev = new HashMap<>();
        ev.put("disasterCount", count);
        ev.put("summary", String.format("NASA EONET: %d open disasters in last 30 days for %s", count, raw.getCategory()));

        return OpportunitySignal.builder()
                .country(raw.getCountry()).category(raw.getCategory())
                .signalType(SignalType.DISASTER_COUNT.toString()).source(Source.NASA.toString())
                .rawValue((double) count).normalizedScore(normalized)
                .evidence(objectMapper.valueToTree(ev)).calculatedAt(LocalDateTime.now()).build();
    }
}