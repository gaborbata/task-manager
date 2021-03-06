package com.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;

/**
 * Class to represent a user request DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserRequestDto {

    @NotNull
    private final String username;

    private final String firstName;

    private final String lastName;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserRequestDto(@JsonProperty("username") String username,
                          @JsonProperty("first_name") String firstName,
                          @JsonProperty("last_name") String lastName) {
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }
}
