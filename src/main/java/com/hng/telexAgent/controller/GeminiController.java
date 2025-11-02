package com.hng.telexAgent.controller;

import com.hng.telexAgent.services.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
public class GeminiController {

    @Autowired
    private GeminiService geminiService;

    @PostMapping("/ask")
    public ResponseEntity<String> fetchFromAI(@RequestBody Map<String, String> request){

        String role = request.get("role");
        String response = geminiService.generateAIQuestions(role);

        return ResponseEntity.status(HttpStatus.OK).body(response);
    }
}
