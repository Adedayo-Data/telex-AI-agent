package com.hng.telexAgent.dto;

// This container file holds the contract for the A2A Protocol

public final class A2ADto {

    // --- 1. A2A REQUEST (What Telex.im sends TO us) ---

    public record A2AProtocolRequest(
            String agentId,
            String userId,
            Message message
            // We can ignore 'context' for now
    ) {}

    public record Message(
            String type, // e.g., "text"
            String content // e.g., "Backend Developer"
    ) {}


    // --- 2. A2A RESPONSE (What we send BACK to Telex.im) ---

    public record A2AProtocolResponse(
            String agentId,
            String status, // e.g., "success" or "error"
            Response response
    ) {}

    public record Response(
            String type, // e.g., "text"
            String content // The actual interview questions
    ) {}
}