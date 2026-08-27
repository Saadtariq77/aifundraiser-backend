package com.baitussalam.aifundraiser.worldBankClient.service;

import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorldBankService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ExternalApiRawDataRepository rawRepo;

    @Value("${world.bank.base.url}")
    private String baseUrl;

    public JsonNode getPovertyRate(String countryCode, String category) {
        // countryCode ISO2: PK, TR, ZA  |  indicator: SI.POV.DDAY = $2.15 poverty
        String url = UriComponentsBuilder.fromUriString(baseUrl + "/country/" + countryCode + "/indicator/SI.POV.DDAY")
                .queryParam("format", "json")
                .queryParam("date", "2020:2024")
                .queryParam("per_page", 5)
                .toUriString();
        String country = switch (countryCode) {
            case "PK" -> "pakistan";
            case "SY" -> "syria";
            case "PS" -> "palestine";
            default -> "unknown";
        };

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);

        try {
            JsonNode json = objectMapper.readTree(resp.getBody());
            ExternalApiRawData raw = ExternalApiRawData.builder()
                    .source(Source.WORLD_BANK.toString())
                    .country(country)
                    .category(category)
                    .requestUrl(url)
                    .requestParameters(objectMapper.valueToTree(Map.of("indicator", "SI.POV.DDAY")))
                    .responseData(json)
                    .build();
            rawRepo.save(raw);
            return json;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}