package com.hng.telexAgent.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;


public final class A2ADto {

    public record A2ARequest(
            String jsonrpc,
            String method,
            String id,
            Params params
    ) {}

    public record Params(
            Message message
    ) {}

    public record Message(
            String role,
            List<Part> parts,
            String messageId
    ) {}


    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Part(
            String kind,
            String text,
            List<Part> data
    ) {}

    public record A2AResponse(
            String jsonrpc,
            String id,
            Result result
    ) {}

    public record Result(
            String id,
            String contextId,
            Status status,
            List<Artifact> artifacts,
            List<Message> history
    ) {}

    public record Status(
            String state,
            Message message
    ) {}

    public record Artifact(
            String artifactId,
            String name, // "Interview Questions"
            List<Part> parts
    ) {}

}