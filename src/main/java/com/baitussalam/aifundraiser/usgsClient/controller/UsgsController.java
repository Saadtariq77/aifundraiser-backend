package com.baitussalam.aifundraiser.usgsClient.controller;

import com.baitussalam.aifundraiser.usgsClient.dto.UsgsResponseDto;
import com.baitussalam.aifundraiser.usgsClient.service.UsgsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/api/public/v1/usgs")
@RequiredArgsConstructor
public class UsgsController {

    private final UsgsService service;

    @GetMapping("/earthquakes")
    public UsgsResponseDto earthquakes() {

        return service.getEarthquakes(
                30.3753,
                69.3451,
                1000,
                4.5,
                LocalDate.now().minusDays(30),
                LocalDate.now()
        );
    }
}
