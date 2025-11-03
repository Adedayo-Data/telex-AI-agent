package com.hng.telexAgent.controller;

import com.hng.telexAgent.dto.A2ADto;
import com.hng.telexAgent.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/agent")
@RequiredArgsConstructor
public class AgentController {

    private final GeminiService geminiService;

    @PostMapping("/message")
    public ResponseEntity<?> handleMessage(
            @RequestBody A2ADto.A2ARequest request) {

        if (!"message/send".equals(request.method())) {
            return ResponseEntity.badRequest().body(Map.of("error", "Invalid method"));
        }

        try {
            // --- 2. EXTRACT THE USER'S PROMPT ---
            // We must recursively search for all text parts and get the LAST one.
            List<String> allTextParts = new ArrayList<>();
            if (request.params().message().parts() != null) {
                for (A2ADto.Part part : request.params().message().parts()) {
                    findTextRecursively(part, allTextParts);
                }
            }

            String userPrompt = allTextParts.isEmpty() ? "" : allTextParts.get(allTextParts.size() - 1);

            if (userPrompt.trim().isEmpty()) {
                return ResponseEntity.ok(
                        createErrorResponse(request.id(), "Please provide a job role.")
                );
            }

            String aiResponseText = geminiService.generateAIQuestions(userPrompt);

            String taskId = "task-" + UUID.randomUUID();
            String artifactId = "artifact-" + UUID.randomUUID();

            A2ADto.Part aiPart = new A2ADto.Part("text", aiResponseText, null);

            A2ADto.Artifact artifact = new A2ADto.Artifact(
                    artifactId,
                    "Interview Questions",
                    List.of(aiPart)
            );

            A2ADto.Message statusMessage = new A2ADto.Message(
                    "agent",
                    List.of(aiPart),
                    "msg-" + UUID.randomUUID()
            );

            A2ADto.Status status = new A2ADto.Status(
                    "input-required",
                    statusMessage
            );

            A2ADto.Result result = new A2ADto.Result(
                    taskId,
                    taskId,
                    status,
                    List.of(artifact),
                    List.of()
            );

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

    private A2ADto.A2AResponse createErrorResponse(String requestId, String errorMessage) {
        String taskId = "task-" + UUID.randomUUID();

        A2ADto.Part errorPart = new A2ADto.Part("text", errorMessage, null);

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


    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Agent is running"));
    }

    /**
     * Recursively searches the A2A parts structure to find all plain-text parts.
     */
    private void findTextRecursively(A2ADto.Part part, List<String> textParts) {
        if ("text".equals(part.kind()) && part.text() != null && !part.text().isBlank()) {
            if (!part.text().startsWith("<p>")) {
                textParts.add(part.text());
            }
        }

        if (part.data() != null) {
            for (A2ADto.Part nestedPart : part.data()) {
                findTextRecursively(nestedPart, textParts);
            }
        }
    }
}