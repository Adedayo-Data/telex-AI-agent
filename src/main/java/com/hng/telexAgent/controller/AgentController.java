package com.hng.telexAgent.controller;

import com.hng.telexAgent.dto.A2ADto;
import com.hng.telexAgent.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final GeminiService geminiService;

    @PostMapping("/message")
    public ResponseEntity<?> handleMessage(
            @RequestBody A2ADto.A2ARequest request) {

        // --- 1. VALIDATE THE REQUEST ---
        if (!"message/send".equals(request.method())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid method"));
        }

        try {
            // --- 2. EXTRACT THE USER'S PROMPT ---
            // Find the first "text" part in the message
            String userPrompt = request.params().message().parts().stream()
                    .filter(part -> "text".equals(part.kind()))
                    .findFirst()
                    .map(A2ADto.Part::text)
                    .orElse("");

            if (userPrompt.trim().isEmpty()) {
                return ResponseEntity.ok(
                        createErrorResponse(request.id(), "Please provide a job role.")
                );
            }

            // --- 3. CALL OUR "BRAIN" (This part is unchanged!) ---
            String aiResponseText = geminiService.generateAIQuestions(userPrompt);

            // --- 4. BUILD THE FORMAL A2A RESPONSE ---
            String taskId = "task-" + UUID.randomUUID();
            String artifactId = "artifact-" + UUID.randomUUID();

            // Create the text part for our AI's response
            A2ADto.Part aiPart = new A2ADto.Part("text", aiResponseText);

            // Create the "Artifact" (the main content)
            A2ADto.Artifact artifact = new A2ADto.Artifact(
                    artifactId,
                    "Interview Questions",
                    List.of(aiPart)
            );

            // Create the "Message" for the status
            A2ADto.Message statusMessage = new A2ADto.Message(
                    "agent",
                    List.of(aiPart),
                    "msg-" + UUID.randomUUID()
            );

            // Create the "Status"
            A2ADto.Status status = new A2ADto.Status(
                    "input-required", // This tells Telex the agent is done and waiting for more input
                    statusMessage
            );

            // Create the final "Result"
            A2ADto.Result result = new A2ADto.Result(
                    taskId,
                    taskId, // Use task ID as context ID for simplicity
                    status,
                    List.of(artifact),
                    List.of() // Empty history
            );

            // Create the final JSON-RPC Response
            A2ADto.A2AResponse a2aResponse = new A2ADto.A2AResponse(
                    "2.0",      // Must be "2.0"
                    request.id(), // Must be the same ID from the request
                    result
            );

            return ResponseEntity.ok(a2aResponse);

        } catch (Exception e) {
            System.err.println("!!! UNEXPECTED ERROR: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(
                    createErrorResponse(request.id(), "Sorry, I encountered an internal error.")
            );
        }
    }

    // --- Helper to build an Error Response ---
    private A2ADto.A2AResponse createErrorResponse(String requestId, String errorMessage) {
        String taskId = "task-" + UUID.randomUUID();

        A2ADto.Part errorPart = new A2ADto.Part("text", errorMessage);

        A2ADto.Message errorMessageObj = new A2ADto.Message(
                "agent",
                List.of(errorPart),
                "msg-" + UUID.randomUUID()
        );

        A2ADto.Status errorStatus = new A2ADto.Status("failed", errorMessageObj);

        A2ADto.Result errorResult = new A2ADto.Result(
                taskId,
                taskId,
                errorStatus,
                List.of(),
                List.of()
        );

        return new A2ADto.A2AResponse("2.0", requestId, errorResult);
    }

    // Health check stays the same
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Agent is running"));
    }
}