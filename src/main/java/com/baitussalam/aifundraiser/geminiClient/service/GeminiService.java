package com.baitussalam.aifundraiser.geminiClient.service;

import com.baitussalam.aifundraiser.geminiClient.dto.GeminiRequestDto;
import com.baitussalam.aifundraiser.geminiClient.dto.GeminiResponseDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${gemini.base-url}")
    private String baseUrl;

    @Value("${gemini.api-key}")
    private String apiKey;

    @Value("${gemini.model}")
    private String model;

    public String generateContent(String systemPrompt, String userPrompt) {
        String url = String.format("%s/v1beta/models/%s:generateContent", baseUrl, model);

        GeminiRequestDto requestDto = GeminiRequestDto.builder()
                .systemInstruction(GeminiRequestDto.ContentDto.builder()
                        .parts(List.of(GeminiRequestDto.PartDto.builder().text(systemPrompt).build()))
                        .build())
                .contents(List.of(
                        GeminiRequestDto.ContentDto.builder()
                                .parts(List.of(GeminiRequestDto.PartDto.builder().text(userPrompt).build()))
                                .build()
                ))
                .build();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("X-goog-api-key", apiKey);

        HttpEntity<GeminiRequestDto> entity = new HttpEntity<>(requestDto, headers);

        try {
            ResponseEntity<GeminiResponseDto> response = restTemplate.exchange(url, HttpMethod.POST, entity, GeminiResponseDto.class);
            if (response.getBody() != null && response.getBody().getFirstText() != null) {
                return response.getBody().getFirstText();
            }
            log.error("Empty Gemini response: {}", response.getBody());
            return "AI response unavailable";
        } catch (Exception e) {
            log.error("Gemini API call failed", e);
            throw new RuntimeException("Gemini API error: " + e.getMessage(), e);
        }
    }

    // simple overload - same as your curl example
    public String generateContent(String prompt) {
        return generateContent("You are a helpful assistant.", prompt);
    }
}