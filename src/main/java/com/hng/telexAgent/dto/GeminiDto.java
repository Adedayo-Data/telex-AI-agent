package com.hng.telexAgent.dto;

import java.util.List;

// THIS is the public class that matches the file name.
// It acts as a container for all our other DTO records.
public final class GeminiDto {

    // A private constructor so this container class can't be instantiated
    private GeminiDto() {}

    // --- 1. REQUEST CONTRACT ---
    // We make these 'public record' so we can access them from our service
    public record GeminiRequest(List<Content> contents) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}


    // --- 2. RESPONSE CONTRACT ---
    public record GeminiResponse(List<Candidate> candidates) {

        // This helper method is perfectly fine here
        public String extractText() {
            if (this.candidates != null && !this.candidates.isEmpty()) {
                Candidate firstCandidate = this.candidates.get(0);
                if (firstCandidate.content() != null &&
                        firstCandidate.content().parts() != null &&
                        !firstCandidate.content().parts().isEmpty()) {
                    return firstCandidate.content().parts().get(0).text();
                }
            }
            return "Sorry, I couldn't get a valid response from the AI.";
        }
    }

    public record Candidate(Content content) {}

    // Note: 'Content' and 'Part' are used by both the request and response
}