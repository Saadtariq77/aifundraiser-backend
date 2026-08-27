package com.baitussalam.aifundraiser.controller;

import com.baitussalam.aifundraiser.dto.AiDirectDecisionDto;
import com.baitussalam.aifundraiser.service.AiDirectDecisionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/public/v1/ai-decision")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:5173")
public class AiDecisionController {

    private final AiDirectDecisionService directService;

    @PostMapping("/direct")
    public AiDirectDecisionDto decide(@RequestParam(defaultValue = "ALL") String country){
        return directService.decide(country);
    }
}