package com.acme.taskmanager.controller;

import com.acme.taskmanager.dto.TaskRequestDto;
import com.acme.taskmanager.dto.TaskInfoDto;
import com.acme.taskmanager.dto.TaskResponseDto;
import com.acme.taskmanager.service.TaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

/**
 * Controller for handling task related endpoints.
 */
@RestController
@RequestMapping("/api/user/{userId}/task")
public class TaskController {

    private final TaskService taskService;

    @Autowired
    public TaskController(TaskService taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<TaskResponseDto> createTask(@Valid @PathVariable Long userId, @Valid @RequestBody TaskRequestDto task) {
        return taskService.createTask(userId, task);
    }

    @PutMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateTask(@PathVariable Long userId, @PathVariable Long taskId, @RequestBody TaskRequestDto task) {
        return taskService.updateTask(userId, taskId, task);
    }

    @DeleteMapping("/{taskId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteTask(@PathVariable Long userId, @PathVariable Long taskId) {
        return taskService.deleteTask(userId, taskId);
    }

    @GetMapping("/{taskId}")
    public Mono<TaskInfoDto> getTaskInfo(@PathVariable Long userId, @PathVariable Long taskId) {
        return taskService.getTaskInfo(userId, taskId);
    }

    @GetMapping
    public Flux<TaskResponseDto> listAllTasksForAUser(@PathVariable Long userId) {
        return taskService.listAllTasksForAUser(userId);
    }
}
