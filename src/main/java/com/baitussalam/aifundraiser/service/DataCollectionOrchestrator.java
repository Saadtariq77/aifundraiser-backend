package com.baitussalam.aifundraiser.service;

import com.baitussalam.aifundraiser.gdeltClient.service.GdeltService;
import com.baitussalam.aifundraiser.googleNewsClient.service.GoogleNewsService;
import com.baitussalam.aifundraiser.nasaClient.common.DisasterCategory;
import com.baitussalam.aifundraiser.nasaClient.service.EonetService;
import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import com.baitussalam.aifundraiser.serpClient.service.SerpApiService;
import com.baitussalam.aifundraiser.worldBankClient.service.WorldBankService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class DataCollectionOrchestrator {

    private final SerpApiService serpService;
    private final GoogleNewsService  googleNewsService;
    private final EonetService eonetService;
    private final com.baitussalam.aifundraiser.weatherClient.service.WeatherService weatherService;
    private final WorldBankService worldBankService;

    public void collectFor(String country, String countryCode, DonationTrendCategory category) {
        log.info("Collecting for {}-{} category {}", country, countryCode, category);
        try {
            serpService.getGoogleTrends(category , country);
        }
        catch (Exception e){
            log.warn("SERP failed {}", e.getMessage());
        }
        try {
            googleNewsService.searchNews(category, country);
        }
        catch (Exception e){
            log.warn("GOOGLE_NEWS failed {}", e.getMessage());
        }
        try {
            eonetService.getEvents(DisasterCategory.FLOODS, 30);
            eonetService.getEvents(DisasterCategory.LANDSLIDES , 30);
        }
        catch (Exception e){ log.warn("NASA failed {}", e.getMessage());
        }
        try {
            weatherService.getWeather(country, category.toString());
        }
        catch (Exception e){
            log.warn("WEATHER failed {}", e.getMessage());
        }
        try {
            worldBankService.getPovertyRate(countryCode, category.toString());
        } catch (Exception e){
            log.warn("WORLDBANK failed {}", e.getMessage());
        }
    }
}