package com.baitussalam.aifundraiser.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiDirectDecisionDto {

    private String recommendedCountry;

    private String recommendedCategory;

    private String campaignTitle;

    private String launchWindow;

    private String confidence;

    private String reasoning;

    private List<String> evidence;

    private String rawAiResponse;
}