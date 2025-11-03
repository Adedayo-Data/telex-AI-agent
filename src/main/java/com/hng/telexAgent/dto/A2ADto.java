package com.hng.telexAgent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

// This container file holds the REAL A2A JSON-RPC 2.0 contract

public final class A2ADto {

    // --- 1. A2A REQUEST (What Telex.im sends TO us) ---

    // This is the main request object
    public record A2ARequest(
            String jsonrpc, // Will be "2.0"
            String method,  // Will be "message/send"
            String id,      // A unique request ID
            Params params
    ) {}

    // This is the "params" object
    public record Params(
            Message message
    ) {}

    // This is the "message" object
    public record Message(
            String role, // "user"
            List<Part> parts,
            String messageId
    ) {}

    // This is the "part" object
    // Inside public final class A2ADto
// ...

    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(
            String kind,
            String text,
            List<Part> data  // <-- ADD THIS LINE
    ) {}


    // --- 2. A2A RESPONSE (What we send BACK to Telex.im) ---

    // This is the main response object
    public record A2AResponse(
            String jsonrpc, // We must reply with "2.0"
            String id,      // We must send back the SAME request ID
            Result result
    ) {}

    // This is the "result" object
    public record Result(
            String id, // A new, unique task ID
            String contextId, // Can be the same as the task ID
            Status status,
            List<Artifact> artifacts,
            List<Message> history // Can be empty
    ) {}

    // This is the "status" object
    public record Status(
            String state, // "input-required" or "completed"
            Message message
    ) {}

    // This is the "artifact" object (our AI's answer)
    public record Artifact(
            String artifactId,
            String name, // "Interview Questions"
            List<Part> parts
    ) {}

    // We can reuse the Message and Part records from the request
}