package com.acme.taskmanager.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Class to represent a user info DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserInfoDto {
    private final Long id;
    private final String username;

    @JsonProperty("first_name")
    private final String firstName;

    @JsonProperty("last_name")
    private final String lastName;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public UserInfoDto(@JsonProperty("id") Long id,
                       @JsonProperty("username") String username,
                       @JsonProperty("first_name") String firstName,
                       @JsonProperty("last_name") String lastName) {
        this.id = id;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    public Long getId() {
        return id;
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
