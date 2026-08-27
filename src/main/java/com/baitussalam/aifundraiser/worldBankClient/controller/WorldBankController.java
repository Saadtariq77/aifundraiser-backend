package com.baitussalam.aifundraiser.worldBankClient.controller;

import com.baitussalam.aifundraiser.worldBankClient.WorldBankIndicator;
import com.baitussalam.aifundraiser.worldBankClient.service.WorldBankService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/world-bank")
@RequiredArgsConstructor
public class WorldBankController {


    private final WorldBankService service;


    @GetMapping("/{country}/{indicator}")
    public Object getData(
            @PathVariable String country,
            @PathVariable WorldBankIndicator indicator){

        return service.getPovertyRate(country, indicator.toString());
    }

}
