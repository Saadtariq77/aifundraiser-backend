package com.baitussalam.aifundraiser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tools.jackson.databind.JsonNode;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OpportunityScoreDto {

    private Long id;

    private String country;

    private String category;

    private Double totalScore;

    private String confidence;

    private JsonNode breakdown;

    private JsonNode evidenceBundle;

    private String suggestedTitle;

    private String launchWindow;
}