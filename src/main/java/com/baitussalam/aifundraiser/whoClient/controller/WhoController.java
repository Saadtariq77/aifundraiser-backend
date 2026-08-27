package com.baitussalam.aifundraiser.whoClient.controller;

import com.baitussalam.aifundraiser.nasaClient.common.DisasterCategory;
import com.baitussalam.aifundraiser.whoClient.common.HealthIndicator;
import com.baitussalam.aifundraiser.whoClient.service.WhoService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/public/v1/who")
@RequiredArgsConstructor
public class WhoController {


    private final WhoService service;


    @GetMapping("/{indicator}/{country}")
    public Object getHealthData(
            @PathVariable HealthIndicator indicator,
            @PathVariable String country){

        return service.getIndicator(indicator, country);

    }

}
