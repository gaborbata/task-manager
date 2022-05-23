package com.acme.taskmanager.service;

import com.acme.taskmanager.dto.TaskRequestDto;
import com.acme.taskmanager.dto.TaskInfoDto;
import com.acme.taskmanager.dto.TaskResponseDto;
import com.acme.taskmanager.entity.TaskEntity;
import com.acme.taskmanager.exception.EntityNotFoundException;
import com.acme.taskmanager.mapper.TaskMapper;
import com.acme.taskmanager.repository.EntityUpdater;
import com.acme.taskmanager.repository.TaskRepository;
import com.acme.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing tasks.
 */
@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final EntityUpdater<TaskEntity, Long> entityUpdater;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper, EntityUpdater<TaskEntity, Long> entityUpdater) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.entityUpdater = entityUpdater;
    }

    public Mono<TaskResponseDto> createTask(Long userId, TaskRequestDto task) {
        return userRepository.existsById(userId)
                .flatMap(exists -> exists ? Mono.just(exists) : Mono.empty())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .flatMap(exists -> taskRepository.save(taskMapper.toBuilder(task).setUserId(userId).build()))
                .doOnError(error -> LOGGER.error("Could not create task with userId=" + userId, error))
                .map(taskMapper::toReponseDto);
    }

    public Mono<Void> updateTask(Long userId, Long taskId, TaskRequestDto task) {
        return taskRepository.existsByIdAndUserId(userId, taskId)
                .flatMap(exists -> exists ? Mono.just(exists) : Mono.empty())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("entity does not exists")))
                .flatMap(exists -> entityUpdater.updateNonNull(taskId, taskMapper.toEntity(task)))
                .doOnError(error -> LOGGER.error("Could not update task with userId=" + userId + " and taskId=" + taskId, error))
                .flatMap(resultCount -> resultCount > 0 ? Mono.empty() : Mono.error(new EntityNotFoundException("Could not update entity")));
    }

    public Mono<Void> deleteTask(Long userId, Long taskId) {
        return taskRepository.existsByIdAndUserId(userId, taskId)
                .flatMap(exists -> exists ? Mono.just(exists) : Mono.empty())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("entity does not exists")))
                .flatMap(exists -> taskRepository.deleteById(taskId))
                .doOnError(error -> LOGGER.error("Could not delete task with userId=" + userId + " and taskId=" + taskId, error));
    }

    public Mono<TaskInfoDto> getTaskInfo(Long userId, Long taskId) {
        return userRepository.findById(userId)
                .flatMap(existingUser -> taskRepository.findByIdAndUserId(taskId, userId)
                        .map(task -> taskMapper.toInfoDto(task, existingUser)))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("entity does not exists")))
                .doOnError(error -> LOGGER.error("Could not get task info with userId=" + userId + " and taskId=" + taskId, error));
    }

    public Flux<TaskResponseDto> listAllTasksForAUser(Long userId) {
        return userRepository.findById(userId)
                .flatMapMany(existingUser -> taskRepository.findAllByUserId(userId))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .doOnError(error -> LOGGER.error("Could not get tasks with userId=" + userId, error))
                .map(taskMapper::toReponseDto);
    }
}
