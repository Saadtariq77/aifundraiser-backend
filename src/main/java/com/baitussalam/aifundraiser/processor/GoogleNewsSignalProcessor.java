package com.baitussalam.aifundraiser.processor;

import com.baitussalam.aifundraiser.common.SignalType;
import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.entity.OpportunitySignal;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class GoogleNewsSignalProcessor implements SignalProcessor {
    private final ObjectMapper objectMapper;
    @Override public boolean supports(ExternalApiRawData raw) { return Source.GOOGLE_NEWS.toString().equals(raw.getSource()); }
    @Override
    public OpportunitySignal process(ExternalApiRawData raw) {
        int count = raw.getResponseData().path("count").asInt(0);
        double normalized = switch (count) {
            case 0 -> 0;
            case 1,2 -> 30;
            case 3,4,5 -> 65;
            default -> 85;
        };
        if(count>=5) normalized = Math.min(100, normalized+10);
        Map<String,Object> ev = new HashMap<>();
        ev.put("articleCount", count);
        ev.put("summary", String.format("Google News: Found %d news articles for %s in %s", count, raw.getCategory(), raw.getCountry()));
        return OpportunitySignal.builder()
                .country(raw.getCountry()).category(raw.getCategory())
                .signalType(SignalType.NEWS_MENTIONS.toString()).source(Source.GOOGLE_NEWS.toString())
                .rawValue((double)count).normalizedScore(normalized)
                .evidence(objectMapper.valueToTree(ev)).calculatedAt(LocalDateTime.now()).build();
    }
}