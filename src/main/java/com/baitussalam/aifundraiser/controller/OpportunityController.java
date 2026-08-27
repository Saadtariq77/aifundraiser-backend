package com.baitussalam.aifundraiser.controller;

import com.baitussalam.aifundraiser.common.Country;
import com.baitussalam.aifundraiser.entity.OpportunityScore;
import com.baitussalam.aifundraiser.repository.OpportunityScoreRepository;
import com.baitussalam.aifundraiser.serpClient.common.DonationTrendCategory;
import com.baitussalam.aifundraiser.service.AiExplanationService;
import com.baitussalam.aifundraiser.service.DataCollectionOrchestrator;
import com.baitussalam.aifundraiser.service.SignalProcessingService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/public/v1/opportunities")
@RequiredArgsConstructor
public class OpportunityController {

    private final OpportunityScoreRepository scoreRepo;
    private final AiExplanationService aiService;
    private final SignalProcessingService processingService;
    private final DataCollectionOrchestrator orchestrator;

    @PostMapping("/refresh")
    public ResponseEntity<Map<String,String>> refresh() {
        processingService.processUnprocessed();
        return ResponseEntity.ok(Map.of("status","processed"));
    }

    @GetMapping("/top")
    public List<OpportunityScore> top(@RequestParam(defaultValue = "5") int limit) {
        return scoreRepo.findAllByOrderByTotalScoreDesc().stream().limit(limit).toList();
    }

    @GetMapping("/{id}/explain")
    public ResponseEntity<Map<String,String>> explain(@PathVariable Long id) {
        String explanation = aiService.explain(id);
        return ResponseEntity.ok(Map.of("explanation", explanation));
    }

    @GetMapping("/options")
    public Map<String,Object> getOptions(){
        List<Map<String,String>> countries = Arrays.stream(Country.values())
                .map(c -> Map.of("name", c.getName(), "code", c.getCode(), "lat", String.valueOf(c.getLat()), "lon", String.valueOf(c.getLon())))
                .toList();
        List<String> categories = Arrays.stream(DonationTrendCategory.values()).map(Enum::name).toList();
        return Map.of("countries", countries, "categories", categories);
    }

    @PostMapping("/collect")
    public Map<String,String> collect(@RequestParam String country, @RequestParam String countryCode, @RequestParam DonationTrendCategory category){
        orchestrator.collectFor(country, countryCode, category);
        return Map.of("status","collected "+country+" "+category);
    }

    @PostMapping("/collect/batch")
    public ResponseEntity<Map<String,String>> collectBatch(@RequestBody BatchRequest req){
        for(BatchItem item : req.getItems()){
            try{ orchestrator.collectFor(item.getCountry(), item.getCountryCode(), DonationTrendCategory.valueOf(item.getCategory())); }catch(Exception e){}
        }
        return ResponseEntity.ok(Map.of("status","batch collected "+req.getItems().size()+" combos"));
    }

    @Data
    public static class BatchRequest{
        private List<BatchItem> items;
    }
    @Data
    public static class BatchItem{
        private String country; private String countryCode; private String category;
    }


    @GetMapping("/filter")
    public Page<OpportunityScore> filter(
            @RequestParam(required = false) String country,
            @RequestParam(required = false) String category,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "6") int size) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "totalScore"));
        boolean hasCountry = country != null && !country.isBlank() && !country.equals("ALL");
        boolean hasCategory = category != null && !category.isBlank() && !category.equals("ALL");

        if (hasCountry && hasCategory) return scoreRepo.findByCountryIgnoreCaseAndCategoryIgnoreCase(country, category, pageable);
        if (hasCountry) return scoreRepo.findByCountryIgnoreCase(country, pageable);
        if (hasCategory) return scoreRepo.findByCategoryIgnoreCase(category, pageable);
        return scoreRepo.findAll(pageable);
    }
}
