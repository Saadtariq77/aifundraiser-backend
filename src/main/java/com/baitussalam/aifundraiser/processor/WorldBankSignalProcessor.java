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
public class WorldBankSignalProcessor implements SignalProcessor {

    private final ObjectMapper objectMapper;

    @Override public boolean supports(ExternalApiRawData raw) { return Source.WORLD_BANK.toString().equals(raw.getSource()); }

    @Override
    public OpportunitySignal process(ExternalApiRawData raw) {
        // WorldBank returns [metadata, [ {value: 4.2} ] ]
        JsonNode data = raw.getResponseData();
        double povertyRate = 0;
        if (data.isArray() && data.size() > 1 && data.get(1).isArray() && data.get(1).size() > 0) {
            for (JsonNode n : data.get(1)) if (!n.path("value").isNull()) { povertyRate = n.path("value").asDouble(0); break; }
        }
        // poverty 0-5% = 30, 5-15% = 60, 15%+ = 90 (higher poverty = higher need)
        double normalized = povertyRate >= 15 ? 90 : povertyRate >= 5 ? 60 : povertyRate > 0 ? 30 : 50; // 50 if no data

        Map<String,Object> ev = new HashMap<>();
        ev.put("povertyRate", povertyRate);
        ev.put("summary", String.format("World Bank: Poverty headcount %.1f%% at $2.15/day -> need score %.0f/100", povertyRate, normalized));

        return OpportunitySignal.builder()
                .country(raw.getCountry()).category(raw.getCategory())
                .signalType(SignalType.POVERTY_RATE.toString()).source(Source.WORLD_BANK.toString())
                .rawValue(povertyRate).normalizedScore(normalized)
                .evidence(objectMapper.valueToTree(ev)).calculatedAt(LocalDateTime.now()).build();
    }
}