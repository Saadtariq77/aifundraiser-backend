package com.baitussalam.aifundraiser.openWeatherClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class WeatherForecastResponseDto {

    private List<ForecastItemDto> list;

    private CityDto city;


    @Data
    public static class ForecastItemDto {

        private Long dt;

        private String dt_txt;

        private MainDto main;

        private List<WeatherDto> weather;

        private Double pop;

        private RainDto rain;
    }


    @Data
    public static class MainDto {

        private Double temp;

        private Integer humidity;

        private Double feels_like;
    }


    @Data
    public static class WeatherDto {

        private String main;

        private String description;
    }


    @Data
    public static class RainDto {

        @JsonProperty("3h")
        private Double threeHourRain;
    }


    @Data
    public static class CityDto {

        private String name;
    }
}
