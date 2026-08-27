package com.baitussalam.aifundraiser.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvidenceBundleDto {

    private String country;

    private String category;

    private Double totalScore;

    private String confidence;

    private Map<String, Double> breakdown;

    private List<String> evidences;
}