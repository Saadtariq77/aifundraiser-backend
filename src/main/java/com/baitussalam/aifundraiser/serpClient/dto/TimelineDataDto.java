package com.baitussalam.aifundraiser.serpClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TimelineDataDto {

    private String date;

    private List<TrendValueDto> values;
}
