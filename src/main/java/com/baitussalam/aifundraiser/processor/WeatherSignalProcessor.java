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
public class WeatherSignalProcessor implements SignalProcessor {

    private final ObjectMapper objectMapper;

    @Override public boolean supports(ExternalApiRawData raw) { return Source.WEATHER.toString().equals(raw.getSource()); }

    @Override
    public OpportunitySignal process(ExternalApiRawData raw) {
        JsonNode data = raw.getResponseData();
        double maxWind = 0, maxRain = 0;
        boolean hasStorm = false;

        // OpenWeather forecast has "list": [ {wind:{speed}, rain:{"3h":5}, weather:[{id:202}]} ]
        JsonNode list = data.path("list");
        if (list.isArray()) {
            for (JsonNode item : list) {
                maxWind = Math.max(maxWind, item.path("wind").path("speed").asDouble(0));
                maxRain = Math.max(maxRain, item.path("rain").path("3h").asDouble(0));
                maxRain = Math.max(maxRain, item.path("snow").path("3h").asDouble(0));
                // weather id 2xx = thunderstorm, 5xx = rain, 6xx = snow
                for (JsonNode w : item.path("weather")) {
                    int id = w.path("id").asInt(800);
                    if (id < 600) hasStorm = true;
                }
            }
        } else {
            // fallback for /weather (single object)
            maxWind = data.path("wind").path("speed").asDouble(0);
            maxRain = data.path("rain").path("1h").asDouble(0);
        }

        double normalized = 10;
        if (hasStorm && (maxRain > 10 || maxWind > 15)) normalized = 100;
        else if (maxRain > 5 || maxWind > 10) normalized = 65;
        else if (maxRain > 0.5) normalized = 30;

        Map<String,Object> ev = new HashMap<>();
        ev.put("maxRain3h", maxRain);
        ev.put("maxWind", maxWind);
        ev.put("hasStorm", hasStorm);
        ev.put("summary", String.format("Weather 5d forecast: max rain %.1fmm/3h, max wind %.1f m/s, storm=%s -> risk %.0f/100", maxRain, maxWind, hasStorm, normalized));

        return OpportunitySignal.builder()
                .country(raw.getCountry()).category(raw.getCategory())
                .signalType(SignalType.WEATHER_RISK.toString()).source(Source.WEATHER.toString())
                .rawValue(maxRain).normalizedScore(normalized)
                .evidence(objectMapper.valueToTree(ev)).calculatedAt(LocalDateTime.now()).build();
    }
}