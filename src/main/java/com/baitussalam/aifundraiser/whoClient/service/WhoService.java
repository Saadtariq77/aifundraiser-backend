package com.baitussalam.aifundraiser.whoClient.service;

import com.baitussalam.aifundraiser.common.SignalType;
import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.whoClient.common.HealthIndicator;
import com.baitussalam.aifundraiser.whoClient.dto.HealthIndicatorDto;
import com.baitussalam.aifundraiser.whoClient.dto.WhoResponseDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.net.URI;

@Service
public class WhoService {

    @Value("${who.base.url}")
    private String BASE_URL;

    private final RestTemplate restTemplate;
    private final ExternalApiRawDataRepository externalApiRawDataRepository;

    public WhoService(RestTemplate restTemplate, ExternalApiRawDataRepository externalApiRawDataRepository) {
        this.restTemplate = restTemplate;
        this.externalApiRawDataRepository = externalApiRawDataRepository;
    }

    public WhoResponseDto<HealthIndicatorDto> getIndicator(HealthIndicator indicator , String country) {

        ObjectMapper objectMapper = new ObjectMapper();
        URI uri = UriComponentsBuilder
                .fromUriString(BASE_URL + "/" + indicator.getCode())
                .queryParam("$filter", "SpatialDim eq '" + country + "'")
                .build()
                .toUri();

        WhoResponseDto responseDto = restTemplate.getForObject(uri, WhoResponseDto.class);

        ExternalApiRawData externalApiRawData = ExternalApiRawData.builder()
                .source(Source.WHO.toString())
                .category(SignalType.HEALTH_SCORE.toString())
                .country(country)
                .requestUrl(uri.toString())
                .requestParameters(objectMapper.valueToTree(indicator))
                .responseData(objectMapper.valueToTree(responseDto))
                .build();
        externalApiRawDataRepository.save(externalApiRawData);
        return responseDto;

    }

}
