package com.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a user response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserResponseDto {

    private final Long id;
    private final String username;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserResponseDto(@JsonProperty("id") Long id, @JsonProperty("username") String username) {
        this.id = id;
        this.username = username;
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }
}
