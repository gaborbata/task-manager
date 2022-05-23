package com.acme.taskmanager.dto;

import com.acme.taskmanager.type.TaskStatus;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.time.LocalDateTime;

/**
 * Class to represent a task info DTO.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TaskInfoDto {

    private final Long id;

    private final String name;

    private final String description;

    @JsonProperty("date_time")
    private final LocalDateTime dateTime;

    private final TaskStatus status;

    private final UserInfoDto user;

    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    public TaskInfoDto(@JsonProperty("id") Long id,
                       @JsonProperty("name") String name,
                       @JsonProperty("description") String description,
                       @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
                       @JsonProperty("date_time") LocalDateTime dateTime,
                       @JsonProperty("status") TaskStatus status,
                       @JsonProperty("user") UserInfoDto user) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.status = status;
        this.user = user;
    }

    public Long getId() {
        return id;
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

    public UserInfoDto getUser() {
        return user;
    }
}
