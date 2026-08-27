package com.baitussalam.aifundraiser.nasaClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class EonetResponseDto {

    private String title;

    private List<EventDto> events;

    @Data
    public static class EventDto {

        private String id;

        private String title;

        private String description;

        private String link;

        private List<CategoryDto> categories;

        private List<GeometryDto> geometry;

    }

    @Data
    public static class CategoryDto {

        private String id;

        private String title;

    }

    @Data
    public static class GeometryDto {

        private String date;

        private List<Double> coordinates;

    }
}
