package com.baitussalam.aifundraiser.openWeatherClient.controller;

import com.baitussalam.aifundraiser.openWeatherClient.dto.WeatherForecastResponseDto;
import com.baitussalam.aifundraiser.openWeatherClient.dto.WeatherResponseDto;
import com.baitussalam.aifundraiser.openWeatherClient.service.WeatherForecastService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.io.DataInput;

@Slf4j
@RestController
@RequestMapping("api/public/v1/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherForecastService weatherForecastService;
    private final com.baitussalam.aifundraiser.weatherClient.service.WeatherService weatherService;
    private final ObjectMapper objectMapper;


    @GetMapping("/forecast/{city}")
    public WeatherForecastResponseDto getForecast(@PathVariable String city){
        return weatherForecastService.getWeather(city);
    }

    @GetMapping("/{city}")
    public WeatherResponseDto getWeather(@PathVariable String city) {
        JsonNode node = weatherService.getWeatherByCity(city); // NOT getWeather(city,"weather")
        try {
            log.info("Raw node: {}", node.toPrettyString());
            return objectMapper.treeToValue(node, WeatherResponseDto.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to map weather json to DTO", e);
        }
    }
}
