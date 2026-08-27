package com.baitussalam.aifundraiser.service;

import com.baitussalam.aifundraiser.entity.ExternalApiRawData;
import com.baitussalam.aifundraiser.entity.OpportunityScore;
import com.baitussalam.aifundraiser.geminiClient.service.GeminiService;
import com.baitussalam.aifundraiser.dto.AiDirectDecisionDto;
import com.baitussalam.aifundraiser.repository.ExternalApiRawDataRepository;
import com.baitussalam.aifundraiser.repository.OpportunityScoreRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiDirectDecisionService {

    private final ExternalApiRawDataRepository rawRepo;
    private final GeminiService geminiService;
    private final ObjectMapper objectMapper;
    private final OpportunityScoreRepository opportunityScoreRepository;

    public AiDirectDecisionDto decide(String country) {
        // 1. Get latest 10 raw rows for this country OR all if country=ALL
        List<OpportunityScore> raws;
        if (country == null || country.equalsIgnoreCase("ALL")) {
            raws = opportunityScoreRepository.findAll().stream()
                    .sorted((a, b) -> b.getCalculatedAt().compareTo(a.getCalculatedAt()))
                    .limit(10).toList();
        } else {
            raws = opportunityScoreRepository.findAllByCountryIgnoreCaseOrderByCalculatedAtDesc(country);
        }
            if (raws.isEmpty()) throw new RuntimeException("No raw data for " + country + " - run /collect first");

            // 2. Build compact raw bundle - TRUNCATE to avoid token limit
            StringBuilder bundle = new StringBuilder();
            for (OpportunityScore r : raws) {
                String rawStr = r.getEvidenceBundle().toString();
                // truncate huge timeline - keep only 800 chars per source
                if (rawStr.length() > 1500) rawStr = rawStr.substring(0, 1500) + "...[truncated]";
                bundle.append("\n--- Confidence: ").append(r.getConfidence())
                        .append(" | Country: ").append(r.getCountry())
                        .append(" | Category: ").append(r.getCategory())
                        .append("\nRAW_JSON: ").append(rawStr).append("\n");
            }

            // 3. System + User Prompt - Force JSON
            String systemPrompt = """
                    You are NOIP AI Decision Maker for Baitussalam NGO.
                    You are given RAW JSON from 5 APIs: SERP Google Trends, Google News, NASA EONET, OpenWeather, WorldBank.
                    Your job is to DECIDE which fundraising campaign to launch.
                    You MUST return ONLY valid JSON, no markdown, no explanation outside JSON.
                    JSON Schema:
                    {
                      "recommendedCountry": "pakistan | syria | palestine",
                      "recommendedCategory": "NATURAL_DISASTER | HEALTH | EDUCATION | FOOD | etc",
                      "campaignTitle": "string",
                      "launchWindow": "NOW | NEXT_2_WEEKS | NEXT_MONTH",
                      "confidence": "HIGH | MEDIUM | LOW",
                      "reasoning": "4 bullet points why, reference raw numbers from each evidence",
                      "evidence": ["1 line per evidence from raw"]
                    }
                    Be concise. Use only numbers from RAW_JSON. Do not invent and for the serp API see the trends from the latest dates not the old ones.
                    """;

            String userPrompt = """
                    RAW DATA BUNDLE (10 latest records):
                    %s
                    
                    Task: Analyze all raw data, compare countries/categories, pick ONE best campaign to launch now.
                    Consider: search spike + news count + disaster count + weather risk + poverty.
                    Return JSON only.
                    """.formatted(bundle.toString());

            log.info("Direct AI prompt size: {} chars", userPrompt.length());
            String aiJson = geminiService.generateContent(systemPrompt, userPrompt);
            log.info("Direct AI response: {}", aiJson);

            // 4. Clean markdown fences if Gemini returns ```json
            String clean = aiJson.trim()
                    .replace("```json", "").replace("```", "").trim();
            // extract first { to last }
            int start = clean.indexOf("{");
            int end = clean.lastIndexOf("}");
            if (start != -1 && end != -1) clean = clean.substring(start, end + 1);

            try {
                AiDirectDecisionDto dto = objectMapper.readValue(clean, AiDirectDecisionDto.class);
                dto.setRawAiResponse(aiJson);
                return dto;
            } catch (Exception e) {
                log.error("Failed to parse AI JSON", e);
                AiDirectDecisionDto fallback = new AiDirectDecisionDto();
                fallback.setReasoning(aiJson);
                fallback.setRawAiResponse(aiJson);
                fallback.setConfidence("MEDIUM");
                return fallback;
            }
        }
    }