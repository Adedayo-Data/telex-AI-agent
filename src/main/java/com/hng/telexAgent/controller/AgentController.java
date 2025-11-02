package com.hng.telexAgent.controller;

import com.hng.telexAgent.dto.A2ADto;
import com.hng.telexAgent.services.GeminiService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/agent") // This is our base URL
@RequiredArgsConstructor
public class AgentController {

    private final GeminiService geminiService;

    // This is the main endpoint Telex.im will call
    @PostMapping("/message")
    public ResponseEntity<A2ADto.A2AProtocolResponse> handleMessage(
            @RequestBody A2ADto.A2AProtocolRequest request) {

        // 1. Extract the job role from the A2A request
        String jobRole = request.message().content();
        System.out.println("JobRole is: " + jobRole);

        // 2. Call our "brain" (the service we already built)
        String interviewQuestions = geminiService.generateAIQuestions(jobRole);

        // 3. Build the A2A-formatted response
        A2ADto.Response responsePayload = new A2ADto.Response(
                "text", // Type is text
                interviewQuestions // The content is our AI-generated questions
        );

        A2ADto.A2AProtocolResponse a2aResponse = new A2ADto.A2AProtocolResponse(
                request.agentId(), // Send back the same agentId
                "success",       // Set status to success
                responsePayload  // Attach the payload
        );

        return ResponseEntity.ok(a2aResponse);
    }

    // This is the "Health Check" endpoint from the project brief
    @GetMapping("/health")
    public ResponseEntity<Map<String, String>> healthCheck() {
        return ResponseEntity.ok(Map.of("status", "ok", "message", "Agent is running"));
    }
}