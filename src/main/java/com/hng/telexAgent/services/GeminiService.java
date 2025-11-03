package com.hng.telexAgent.services;

import com.hng.telexAgent.dto.GeminiDto;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GeminiService {

    private final RestTemplate restTemplate;

    @Value("${GEMINI-API-KEY}")
    private String apiKey;

    private final String geminiApiUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";


    public String generateAIQuestions(String role) {

        System.out.println("Api key: " +apiKey);

        String prompt = "Generate nine interview questions for a " + role +
                ". Organize them into three categories with markdown headings and emojis: " +
                "🛠️ **Technical Questions** (4 questions), " +
                "🤝 **Behavioral Questions** (4 questions), and " +
                "🧠 **Situational Questions** (4 questions)." +
                "Format the entire response as clean Markdown." + "Add simple job jokes if possible";

        GeminiDto.Part part = new GeminiDto.Part(prompt);
        GeminiDto.Content content = new GeminiDto.Content(List.of(part));
        GeminiDto.GeminiRequest geminiRequest = new GeminiDto.GeminiRequest(List.of(content));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<GeminiDto.GeminiRequest> requestEntity = new HttpEntity<>(geminiRequest, headers);

        try {

            GeminiDto.GeminiResponse response = restTemplate.postForObject(
                    geminiApiUrl + apiKey,
                    requestEntity,
                    GeminiDto.GeminiResponse.class
            );

            if (response != null) {
                return response.extractText();
            } else {
                return "Error: Received an empty response from API.";
            }

        } catch (Exception e) {

            System.err.println("Gemini API Error: " + e.getMessage());

            return "Error: Could not connect to the AI service. Please try again later.";
        }
    }
}