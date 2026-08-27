package com.baitussalam.aifundraiser.gdeltClient.controller;

import com.baitussalam.aifundraiser.gdeltClient.dto.GdeltResponseDto;
import com.baitussalam.aifundraiser.gdeltClient.service.GdeltService;
import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/public/v1/gdelt")
@RequiredArgsConstructor
public class GdeltController {


    private final GdeltService gdeltService;


    @GetMapping("/news/{category}/{country}")
    public GdeltResponseDto getNews(
            @PathVariable DonationTrendCategory category,
            @PathVariable String country){

        return gdeltService.searchNews(category, country);
    }
}
