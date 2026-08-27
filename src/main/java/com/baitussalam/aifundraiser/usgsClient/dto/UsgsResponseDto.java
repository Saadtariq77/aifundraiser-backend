package com.baitussalam.aifundraiser.usgsClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UsgsResponseDto {

    private Metadata metadata;

    private List<Feature> features;

    @Data
    public static class Metadata {
        private Integer count;
    }

    @Data
    public static class Feature {

        private String id;

        private Properties properties;

        private Geometry geometry;
    }

    @Data
    public static class Properties {

        private Double mag;

        private String place;

        private Long time;

        private String url;
    }

    @Data
    public static class Geometry {

        private List<Double> coordinates;
    }
}