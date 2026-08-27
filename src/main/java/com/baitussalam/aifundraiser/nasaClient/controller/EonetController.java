package com.baitussalam.aifundraiser.nasaClient.controller;

import com.baitussalam.aifundraiser.nasaClient.common.DisasterCategory;
import com.baitussalam.aifundraiser.nasaClient.dto.EonetResponseDto;
import com.baitussalam.aifundraiser.nasaClient.service.EonetService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/public/v1/eonet")
@RequiredArgsConstructor
public class EonetController {

    private final EonetService service;

    @GetMapping("/{category}")
    public EonetResponseDto events(@PathVariable DisasterCategory category) {

        return service.getEvents(category, 30);
    }
}
