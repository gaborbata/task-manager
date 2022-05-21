package com.acme.taskmanager.dto;

import com.acme.taskmanager.type.TaskStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Class to represent a task response DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskResponseDto {

    private final Long id;
    private final String name;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TaskResponseDto(@JsonProperty("id") Long id,
                           @JsonProperty("name") String name) {
        this.id = id;
        this.name = name;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }
}
