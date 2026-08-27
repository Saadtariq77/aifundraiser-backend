package com.baitussalam.aifundraiser.geminiClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GeminiResponseDto {

    private List<CandidateDto> candidates;

    @Data
    public static class CandidateDto {
        private ContentDto content;
        private String finishReason;
    }

    @Data
    public static class ContentDto {
        private List<PartDto> parts;
        private String role;
    }

    @Data
    public static class PartDto {
        private String text;
    }

    public String getFirstText() {
        if (candidates == null || candidates.isEmpty()) return null;
        CandidateDto candidate = candidates.get(0);
        if (candidate.getContent() == null || candidate.getContent().getParts() == null || candidate.getContent().getParts().isEmpty()) return null;
        return candidate.getContent().getParts().get(0).getText();
    }
}