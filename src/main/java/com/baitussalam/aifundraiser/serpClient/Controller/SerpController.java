package com.baitussalam.aifundraiser.serpClient.Controller;

import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import com.baitussalam.aifundraiser.serpClient.dto.SerpTrendResponseDto;
import com.baitussalam.aifundraiser.serpClient.service.SerpApiService;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

@RestController
@RequestMapping("api/public/v1/serp")
public class SerpController {

    private final SerpApiService serpApiService;

    public SerpController(SerpApiService serpApiService) {
        this.serpApiService = serpApiService;
    }

    @GetMapping("/google-trends/{keyWord}/{country}")
    public SerpTrendResponseDto getGoogleTrends(@PathVariable DonationTrendCategory keyWord , @PathVariable String country) {



        return serpApiService.getGoogleTrends(keyWord , country);
    }
}
