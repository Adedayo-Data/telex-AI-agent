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
// This Lombok annotation automatically injects 'final' fields
@RequiredArgsConstructor
public class GeminiService {

    // 1. INJECT OUR TOOLS

    // This injects the RestTemplate bean we created in AppConfig
    private final RestTemplate restTemplate;

    // This injects the API key from application.properties
    @Value("${GEMINI-API-KEY}")
    private String apiKey;

    // The Gemini API endpoint. We use gemini-1.5-flash-latest,
    // a great free-tier model.
    private final String geminiApiUrl =
            "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash-lite:generateContent?key=";


    public String generateAIQuestions(String role) {

        System.out.println("Api key: " +apiKey);

        // 2. BUILD THE PROMPT
        // We add the formatting instructions from our plan.
        String prompt = "Generate twelve interview questions for a " + role +
                ". Organize them into three categories with markdown headings and emojis: " +
                "🛠️ **Technical Questions** (4 questions), " +
                "🤝 **Behavioral Questions** (4 questions), and " +
                "🧠 **Situational Questions** (4 questions)." +
                "Format the entire response as clean Markdown.";

        // 3. BUILD THE REQUEST BODY (using our DTOs)
        // This converts our Java objects into the perfect JSON structure.
        GeminiDto.Part part = new GeminiDto.Part(prompt);
        GeminiDto.Content content = new GeminiDto.Content(List.of(part));
        GeminiDto.GeminiRequest geminiRequest = new GeminiDto.GeminiRequest(List.of(content));

        // 4. SET THE HEADERS
        // We must tell Google we are sending JSON.
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // 5. CREATE THE HTTP REQUEST
        // This combines the body (geminiRequest) and headers
        HttpEntity<GeminiDto.GeminiRequest> requestEntity = new HttpEntity<>(geminiRequest, headers);

        // 6. MAKE THE API CALL!
        try {
            // This is the magic line.
            // It sends our request, and automatically converts
            // the JSON response back into our GeminiResponse object.
            GeminiDto.GeminiResponse response = restTemplate.postForObject(
                    geminiApiUrl + apiKey, // The full URL + key
                    requestEntity,        // Our request body + headers
                    GeminiDto.GeminiResponse.class  // The class to convert the response into
            );

            // 7. EXTRACT AND RETURN THE TEXT
            // We use the helper method we created in our DTO!
            if (response != null) {
                return response.extractText();
            } else {
                return "Error: Received an empty response from API.";
            }

        } catch (Exception e) {
            // This is our Phase 3 error handling.
            // Log the real error for debugging
            System.err.println("Gemini API Error: " + e.getMessage());
            // Return a safe, user-friendly message
            return "Error: Could not connect to the AI service. Please try again later.";
        }
    }
}