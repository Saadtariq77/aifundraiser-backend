package com.baitussalam.aifundraiser.usgsClient.service;

import com.baitussalam.aifundraiser.common.SignalType;
import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.usgsClient.dto.UsgsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.time.LocalDate;

@Service
@RequiredArgsConstructor
public class UsgsService {

    private final RestTemplate restTemplate;
    private final ExternalApiRawDataRepository  externalApiRawDataRepository;

    @Value("${usgs.earthquake.base.url}")
    private String baseUrl;

    public UsgsResponseDto getEarthquakes(
            Double latitude,
            Double longitude,
            Integer radiusKm,
            Double minMagnitude,
            LocalDate startDate,
            LocalDate endDate
    ) {

        ObjectMapper objectMapper = new ObjectMapper();

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("format", "geojson")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("maxradiuskm", radiusKm)
                .queryParam("minmagnitude", minMagnitude)
                .queryParam("starttime", startDate)
                .queryParam("endtime", endDate)
                .toUriString();

        UsgsResponseDto responseDto = restTemplate.getForObject(url, UsgsResponseDto.class);

        ExternalApiRawData externalApiRawData = ExternalApiRawData.builder()
                .source(Source.USGS.toString())
                .category(SignalType.DISASTER_COUNT.toString())
                .country("Global")
                .requestUrl(url)
                .requestParameters(objectMapper.valueToTree(responseDto.getMetadata() != null ? responseDto.getMetadata() : null))
                .responseData(objectMapper.valueToTree(responseDto))
                .build();
        externalApiRawDataRepository.save(externalApiRawData);
        return responseDto;

    }
}
