package com.baitussalam.aifundraiser.whoClient.dto;

import lombok.Data;

import java.util.List;

@Data
public class WhoResponseDto<T> {

    private List<T> value;
}
