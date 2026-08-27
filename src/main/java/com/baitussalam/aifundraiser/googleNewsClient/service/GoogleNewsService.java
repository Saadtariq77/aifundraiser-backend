package com.baitussalam.aifundraiser.googleNewsClient.service;

import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ObjectNode;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class GoogleNewsService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ExternalApiRawDataRepository rawRepo;

    @Value("${googlenews.base.url}")
    private String baseUrl;

    @Value("${serp.api.key}")
    private String SERP_API_KEY;

    public ObjectNode searchNews(DonationTrendCategory category, String country) {
        String query = category.toString().replace("_"," ") + " " + country;
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", query)
                .queryParam("hl", "en-US")
                .queryParam("gl", "US")
                .queryParam("ceid", "US:en")
                .queryParam("api_key", SERP_API_KEY)
                .toUriString();

        log.info("GoogleNews calling: {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_XML, MediaType.TEXT_XML, MediaType.ALL));
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        String body = resp.getBody() == null ? "" : resp.getBody();

        int count = body.split("<item>").length - 1;
        count = Math.max(0, count);

        ObjectNode json = objectMapper.createObjectNode();
        json.put("query", query);
        json.put("count", count);
        json.put("rawSnippet", body.substring(0, Math.min(800, body.length())));

        ExternalApiRawData raw = ExternalApiRawData.builder()
                .source(Source.GOOGLE_NEWS.toString())
                .country(country)
                .category(category.toString())
                .requestUrl(url)
                .requestParameters(objectMapper.valueToTree(Map.of("query", query)))
                .responseData(json)
                .build();
        rawRepo.save(raw);
        return json;
    }
}