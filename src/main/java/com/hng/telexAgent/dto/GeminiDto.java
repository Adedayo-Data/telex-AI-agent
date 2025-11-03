package com.hng.telexAgent.dto;

import java.util.List;

public final class GeminiDto {

    private GeminiDto() {}

    public record GeminiRequest(List<Content> contents) {}
    public record Content(List<Part> parts) {}
    public record Part(String text) {}

    public record GeminiResponse(List<Candidate> candidates) {

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