package com.baitussalam.aifundraiser.serpClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoogleTrendRequestDto {

    private String query;
    private String geo = "PK";
    private String region = "COUNTRY";

}
