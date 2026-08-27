package com.baitussalam.aifundraiser.worldBankClient.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorldBankResponseDto {


    private List<Object> metadata;

    private List<DataDto> data;


    @Data
    public static class DataDto {

        private String date;

        private Double value;


        private CountryDto country;

    }


    @Data
    public static class CountryDto {

        private String value;

    }
}
