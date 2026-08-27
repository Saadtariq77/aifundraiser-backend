package com.baitussalam.aifundraiser.serpClient.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerpTrendResponseDto {

    @JsonProperty("interest_over_time")
    private InterestOverTimeDto interestOverTime;


    @Data
    public static class InterestOverTimeDto {

        @JsonProperty("timeline_data")
        private List<TimelineDataDto> timelineData;
    }
}
