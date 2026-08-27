package com.baitussalam.aifundraiser.nasaClient.service;

import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.nasaClient.common.DisasterCategory;
import com.baitussalam.aifundraiser.nasaClient.dto.EonetResponseDto;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EonetService {

    private final RestTemplate restTemplate;
    private final ExternalApiRawDataRepository externalApiRawDataRepository;
    private final ObjectMapper objectMapper; // INJECT don't do new ObjectMapper()

    @Value("${nasa.eonet.base.url}")
    private String baseUrl;

    public EonetResponseDto getEvents(DisasterCategory category, Integer days) {

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("status", "open")
                .queryParam("days", days)
                .queryParam("category", category.getValue())
                // REMOVE .queryParam("format", "json") -> THIS CAUSES RSS
                .toUriString();

        log.info("EONET calling url: {}", url);

        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        headers.set("User-Agent", "NOIP-POC");
        HttpEntity<Void> entity = new HttpEntity<>(headers);

        // 1. Get as String first, not as DTO
        ResponseEntity<String> response = restTemplate.exchange(url,
                HttpMethod.GET,
                entity,
                String.class);

        String body = response.getBody();
        log.info("EONET raw response: {}", body.substring(0, Math.min(500, body.length())));

        // 2. Fail fast if NASA still returned XML
        if (body != null && body.trim().startsWith("<")) {
            log.error("EONET returned XML instead of JSON. Check category value: {}", category.getValue());
            throw new RuntimeException("EONET returned RSS XML. URL: " + url);
        }

        try {
            EonetResponseDto dto = objectMapper.readValue(body, EonetResponseDto.class);

            ExternalApiRawData externalApiRawData = ExternalApiRawData.builder()
                    .source(Source.NASA.toString())
                    .category(category.toString()) // use DisasterCategory, not SignalType
                    .country("GLOBAL") // Title is not country, use GLOBAL
                    .requestUrl(url)
                    .requestParameters(objectMapper.valueToTree(category))
                    .responseData(objectMapper.readTree(body)) // save raw JSON directly
                    .build();
            externalApiRawDataRepository.save(externalApiRawData);

            return dto;
        } catch (Exception e) {
            log.error("Failed to parse EONET JSON", e);
            throw new RuntimeException(e);
        }
    }
}