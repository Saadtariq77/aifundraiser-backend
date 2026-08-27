package com.baitussalam.aifundraiser.gdeltClient.service;

import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.gdeltClient.dto.GdeltResponseDto;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;

import java.util.Arrays;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class GdeltService {


    private final RestTemplate restTemplate;

    private final ExternalApiRawDataRepository  externalApiRawDataRepository;

    ObjectMapper objectMapper = new ObjectMapper();


    @Value("${gdelt.base.url}")
    private String baseUrl;



    public GdeltResponseDto searchNews(DonationTrendCategory category, String country) {

        String query = buildQuery(category, country);

        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("query", query)
                .queryParam("mode", "artlist")
                .queryParam("format", "json")
                .queryParam("maxrecords", 3)
                .toUriString();


        GdeltResponseDto responseDto = restTemplate.getForObject(url, GdeltResponseDto.class);

        ExternalApiRawData externalApiRawData = new ExternalApiRawData();
        externalApiRawData.setSource(Source.GDELT.toString());
        externalApiRawData.setCountry(country);
        externalApiRawData.setCategory(category.toString());
        externalApiRawData.setRequestUrl(url);
        externalApiRawData.setRequestParameters(objectMapper.valueToTree(query));
        externalApiRawData.setResponseData(objectMapper.valueToTree(responseDto));
        externalApiRawDataRepository.save(externalApiRawData);

        return responseDto;
    }



    private String buildQuery(
            DonationTrendCategory category, String country) {

        String keywords = Arrays.stream(category.getKeywords())
                .map(keyword -> "\"" + keyword + "\"")
                .collect(Collectors.joining(" OR "));

        return "(" + keywords + ") AND " + country;
    }

}
