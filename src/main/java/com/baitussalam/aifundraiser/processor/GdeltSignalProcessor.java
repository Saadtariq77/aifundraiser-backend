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
public class GdeltSignalProcessor implements SignalProcessor {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supports(ExternalApiRawData rawData) {
        return Source.GDELT.toString().equals(rawData.getSource());
    }

    @Override
    public OpportunitySignal process(ExternalApiRawData rawData) {
        try {
            JsonNode articles = rawData.getResponseData().path("articles");
            int count = articles.isArray() ? articles.size() : 0;
            double normalized;
            if (count == 0) normalized = 0;
            else if (count <= 2) normalized = 30;
            else if (count <= 5) normalized = 65;
            else normalized = 85;
            // bonus if many articles very recent
            if (count >= 5) normalized = Math.min(100, normalized + 10);

            List<String> titles = new ArrayList<>();
            for (JsonNode a : articles) titles.add(a.path("title").asText(""));

            Map<String, Object> ev = new HashMap<>();
            ev.put("articleCount", count);
            ev.put("titles", titles.stream().limit(3).toList());
            ev.put("summary", String.format("Found %d recent news articles for %s in %s", count, rawData.getCategory(), rawData.getCountry()));

            return OpportunitySignal.builder()
                    .country(rawData.getCountry()).category(rawData.getCategory())
                    .signalType(SignalType.NEWS_MENTIONS.toString()).source(Source.GDELT.toString())
                    .rawValue((double) count).normalizedScore(normalized)
                    .evidence(objectMapper.valueToTree(ev)).calculatedAt(LocalDateTime.now()).build();
        } catch (Exception e) {
            log.error("GDELT processing failed", e);
            return null;
        }
    }
}