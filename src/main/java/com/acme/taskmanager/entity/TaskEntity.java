package com.acme.taskmanager.entity;

import com.acme.taskmanager.type.TaskStatus;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

/**
 * Class to represent a task entity.
 */
@Table("task")
public class TaskEntity {

    @Id
    private final Long id;

    @Column("user_id")
    private final Long userId;

    private final String name;

    private final String description;

    @Column("date_time")
    private final LocalDateTime dateTime;

    private final TaskStatus status;

    public TaskEntity(Long id, Long userId, String name, String description, LocalDateTime dateTime, TaskStatus status) {
        this.id = id;
        this.userId = userId;
        this.name = name;
        this.description = description;
        this.dateTime = dateTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public Long getUserId() {
        return userId;
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

    public static class Builder {
        private Long id;
        private Long userId;
        private String name;
        private String description;
        private LocalDateTime dateTime;
        private TaskStatus status;

        public Builder setId(Long id) {
            this.id = id;
            return this;
        }

        public Builder setUserId(Long userId) {
            this.userId = userId;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setDateTime(LocalDateTime dateTime) {
            this.dateTime = dateTime;
            return this;
        }

        public Builder setStatus(TaskStatus status) {
            this.status = status;
            return this;
        }

        public TaskEntity build() {
            return new TaskEntity(id, userId, name, description, dateTime, status);
        }
    }
}
