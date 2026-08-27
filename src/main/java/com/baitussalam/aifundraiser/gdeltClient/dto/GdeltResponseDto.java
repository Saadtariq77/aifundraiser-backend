package com.baitussalam.aifundraiser.gdeltClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GdeltResponseDto {

    private List<ArticleDto> articles;


    @Data
    public static class ArticleDto {

        private String title;

        private String url;

        @JsonProperty("seendate")
        private String seenDate;

        private String domain;

        private String language;

        @JsonProperty("sourcecountry")
        private String sourceCountry;
    }
}
