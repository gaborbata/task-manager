package com.acme.taskmanager.service;

import com.acme.taskmanager.dto.TaskRequestDto;
import com.acme.taskmanager.dto.TaskInfoDto;
import com.acme.taskmanager.dto.TaskResponseDto;
import com.acme.taskmanager.entity.TaskEntity;
import com.acme.taskmanager.exception.EntityNotFoundException;
import com.acme.taskmanager.mapper.TaskMapper;
import com.acme.taskmanager.repository.TaskRepository;
import com.acme.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.core.query.Update;
import org.springframework.data.relational.core.sql.SqlIdentifier;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service for managing tasks.
 */
@Service
public class TaskService {
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskService.class);

    private final TaskRepository taskRepository;
    private final UserRepository userRepository;
    private final TaskMapper taskMapper;
    private final R2dbcEntityTemplate r2dbcEntityTemplate;

    @Autowired
    public TaskService(TaskRepository taskRepository, UserRepository userRepository, TaskMapper taskMapper, R2dbcEntityTemplate r2dbcEntityTemplate) {
        this.taskRepository = taskRepository;
        this.userRepository = userRepository;
        this.taskMapper = taskMapper;
        this.r2dbcEntityTemplate = r2dbcEntityTemplate;
    }

    public Mono<TaskResponseDto> createTask(Long userId, TaskRequestDto task) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .flatMap(existingUser -> taskRepository.save(taskMapper.toBuilder(task).setUserId(userId).build()))
                .doOnError(error -> LOGGER.error("Could not create task with userId=" + userId, error))
                .map(taskMapper::toReponseDto);
    }

    public Mono<Void> updateTask(Long userId, Long taskId, TaskRequestDto task) {
        return taskRepository.findByIdAndUserId(userId, taskId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("entity does not exists")))
                .flatMap(existingTask -> {
                    var entity = taskMapper.toEntity(task);
                    var outboundRow = r2dbcEntityTemplate.getDataAccessStrategy().getOutboundRow(entity);
                    Map<SqlIdentifier, Object> assignments = outboundRow.entrySet().stream()
                            .filter(entry -> entry.getValue().hasValue())
                            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
                    return r2dbcEntityTemplate.update(TaskEntity.class)
                            .matching(Query.query(Criteria.where("id").is(taskId)))
                            .apply(Update.from(assignments));
                })
                .doOnError(error -> LOGGER.error("Could not update task with userId=" + userId + " and taskId=" + taskId, error))
                .flatMap(resultCount -> resultCount > 0 ? Mono.empty() : Mono.error(new EntityNotFoundException("Could not update entity")));
    }

    public Mono<Void> deleteTask(Long userId, Long taskId) {
        return taskRepository.findByIdAndUserId(userId, taskId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .flatMap(existingTask -> taskRepository.deleteById(taskId))
                .doOnError(error -> LOGGER.error("Could not delete task with userId=" + userId + " and taskId=" + taskId, error));
    }

    public Mono<TaskInfoDto> getTaskInfo(Long userId, Long taskId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("entity does not exists")))
                .flatMap(existingUser -> taskRepository.findByIdAndUserId(taskId, userId)
                        .map(task -> taskMapper.toInfoDto(task, existingUser)))
                .doOnError(error -> LOGGER.error("Could not get task info with userId=" + userId + " and taskId=" + taskId, error));
    }

    public Flux<TaskResponseDto> listAllTasksForAUser(Long userId) {
        return userRepository.findById(userId)
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .flatMapMany(existingUser -> taskRepository.findAllByUserId(userId))
                .doOnError(error -> LOGGER.error("Could not get tasks with userId=" + userId, error))
                .map(taskMapper::toReponseDto);
    }
}
