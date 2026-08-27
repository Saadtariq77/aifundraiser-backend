package com.baitussalam.aifundraiser.openWeatherClient.service;


import com.baitussalam.aifundraiser.common.SignalType;
import com.baitussalam.aifundraiser.common.Source;
import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.openWeatherClient.dto.WeatherForecastResponseDto;
import com.baitussalam.aifundraiser.openWeatherClient.dto.WeatherResponseDto;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import tools.jackson.databind.ObjectMapper;


@Service
public class WeatherForecastService {


    private final RestTemplate restTemplate;
    private final ExternalApiRawDataRepository externalApiRawDataRepository;

    @Value("${open.weather.api.key}")
    private String apiKey;

    @Value("${open.weather.forecast.url}")
    private String baseUrl;

    public WeatherForecastService(RestTemplate restTemplate, ExternalApiRawDataRepository externalApiRawDataRepository) {
        this.restTemplate = restTemplate;
        this.externalApiRawDataRepository = externalApiRawDataRepository;
    }


    public WeatherForecastResponseDto getWeather(String city) {

        ObjectMapper objectMapper = new ObjectMapper();
        String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .queryParam("q", city)
                .queryParam("appid", apiKey)
                .queryParam("units", "metric")
                .toUriString();


        WeatherForecastResponseDto responseDto = restTemplate.getForObject(url, WeatherForecastResponseDto.class);

        ExternalApiRawData externalApiRawData = ExternalApiRawData.builder()
                .source(Source.WEATHER.toString())
                .country(city)
                .category(SignalType.WEATHER_RISK.toString())
                .requestUrl(url)
                .requestParameters(objectMapper.valueToTree(city))
                .responseData(objectMapper.valueToTree(responseDto))
                .build();
        externalApiRawDataRepository.save(externalApiRawData);
        return responseDto;
    }
}

