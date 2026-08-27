package com.baitussalam.aifundraiser.geminiClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GeminiRequestDto {

    private List<ContentDto> contents;

    @JsonProperty("systemInstruction")
    private ContentDto systemInstruction;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ContentDto {
        private List<PartDto> parts;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PartDto {
        private String text;
    }
}