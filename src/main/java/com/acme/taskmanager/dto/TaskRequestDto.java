package com.acme.taskmanager.dto;

import com.acme.taskmanager.type.TaskStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

/**
 * Class to represent a task request DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskRequestDto {

    @NotNull
    private final String name;
    private final String description;
    private final LocalDateTime dateTime;
    private final TaskStatus status;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TaskRequestDto(@JsonProperty("name") String name,
                          @JsonProperty("description") String description,
                          @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                          @JsonProperty("date_time") LocalDateTime dateTime,
                          @JsonProperty("status") TaskStatus status) {
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public TaskStatus getStatus() {
        return status;
    }
}
