package com.baitussalam.aifundraiser.whoClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HealthIndicatorDto {

    private Integer id;

    private String spatialDim;

    private Integer timeDim;

    private Double numericValue;

    private String dim1;

}
