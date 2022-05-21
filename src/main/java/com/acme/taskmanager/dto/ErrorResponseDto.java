package com.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Optional;

public class ErrorResponseDto {
    private final String message;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public ErrorResponseDto(@JsonProperty("message") String message) {
        this.message = Optional.ofNullable(message).orElse("unknown");
    }

    public String getMessage() {
        return message;
    }
}
