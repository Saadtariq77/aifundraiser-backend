package com.baitussalam.aifundraiser.weatherClient.service;

import com.baitussalam.aifundraiser.common.Country;
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
public class WeatherService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;
    private final ExternalApiRawDataRepository rawRepo;

    @Value("${open.weather.base.url}")
    private String baseUrl; // https://api.openweathermap.org/data/2.5/weather

    @Value("${open.weather.forecast.url}")
    private String forecastUrl; // https://api.openweathermap.org/data/2.5/forecast

    @Value("${open.weather.api.key}")
    private String apiKey;

    // DELETE THIS
// private final Map<String, double[]> COORDS = Map.of(...)

    // ADD THIS
    private double[] getCoords(String country){
        return Country.fromName(country)
                .map(c -> new double[]{c.getLat(), c.getLon()})
                .orElse(new double[]{30.37, 69.34});
    }

    // FOR SCORING - uses forecast + lat/lon (for OpportunityScoringEngine)
    public JsonNode getWeather(String country, String category) {
        double[] latlon = getCoords(country);
        String url = UriComponentsBuilder.fromUriString(forecastUrl)
                .queryParam("lat", latlon[0])
                .queryParam("lon", latlon[1])
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            JsonNode json = objectMapper.readTree(resp.getBody());
            rawRepo.save(ExternalApiRawData.builder()
                    .source(Source.WEATHER.toString()).country(country).category(category)
                    .requestUrl(url).requestParameters(objectMapper.valueToTree(Map.of("lat", latlon[0], "lon", latlon[1])))
                    .responseData(json).build());
            return json;
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    // FOR CONTROLLER - uses current weather + city name
    public JsonNode getWeatherByCity(String city) {
        String url = UriComponentsBuilder.fromUriString(baseUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();
        log.info("Weather current calling: {}", url);
        HttpHeaders headers = new HttpHeaders();
        headers.setAccept(List.of(MediaType.APPLICATION_JSON));
        ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.GET, new HttpEntity<>(headers), String.class);
        try {
            JsonNode json = objectMapper.readTree(resp.getBody());
            log.info("Weather current response: {}", json.toPrettyString().substring(0, Math.min(800, json.toPrettyString().length())));
            return json;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}