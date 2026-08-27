package com.baitussalam.aifundraiser.openWeatherClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponseDto {

    private MainDto main;

    private List<WeatherDto> weather;

    private WindDto wind;

    private String name;


    @Data
    public static class MainDto {

        private Double temp;

        @JsonProperty("feels_like")
        private Double feelsLike;

        private Integer humidity;

        private Integer pressure;
    }


    @Data
    public static class WeatherDto {

        private String main;

        private String description;
    }


    @Data
    public static class WindDto {

        private Double speed;
    }
}
