package com.baitussalam.aifundraiser.serpClient.service;

import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import com.baitussalam.aifundraiser.serpClient.dto.SerpTrendResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;


@Service
public class SerpApiService {

private final RestTemplate restTemplateConfig;
private final ExternalApiRawDataRepository externalApiRawDataRepository;


    @Value("${serp.api.key}")
    private String SERP_API_KEY;

    @Value("${serp.base.url}")
    private String SERP_BASE_URL;

    public SerpApiService(RestTemplate restTemplateConfig, ExternalApiRawDataRepository externalApiRawDataRepository) {
        this.restTemplateConfig = restTemplateConfig;
        this.externalApiRawDataRepository = externalApiRawDataRepository;
    }

    public SerpTrendResponseDto getGoogleTrends(DonationTrendCategory keyWord , String country) {

        String query = String.join(",", keyWord.getKeywords());
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            String url = UriComponentsBuilder
                    .fromUriString(SERP_BASE_URL)
                    .queryParam("engine", "google_trends")
                    .queryParam("q", query)
                    .queryParam("geo", "PK")
                    .queryParam("data_type", "TIMESERIES")
                    .queryParam("api_key", SERP_API_KEY)
                    .toUriString();

            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(java.util.List.of(MediaType.APPLICATION_JSON));

            ResponseEntity<String> response = restTemplateConfig.getForEntity(url, String.class);

            SerpTrendResponseDto responseDto = objectMapper.readValue(response.getBody(), SerpTrendResponseDto.class);

            ExternalApiRawData externalApiRawData = ExternalApiRawData.builder()
                    .country(country)
                    .category(keyWord.toString())
                    .source(Source.SERP.toString())
                    .requestUrl(url)
                    .requestParameters(objectMapper.valueToTree(query))
                    .responseData(objectMapper.valueToTree(responseDto))
                    .build();

            externalApiRawDataRepository.save(externalApiRawData);

            return responseDto;

        } catch (Exception e) {
            throw new RuntimeException("Failed to fetch Google Trends data", e);
        }

    }
}
