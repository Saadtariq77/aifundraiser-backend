package com.baitussalam.aifundraiser.serpClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TrendValueDto {

    private String query;

    private String value;

    @JsonProperty("extracted_value")
    private Integer extractedValue;
}
