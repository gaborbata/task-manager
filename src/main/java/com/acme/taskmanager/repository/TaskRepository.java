package com.acme.taskmanager.repository;

import com.acme.taskmanager.entity.TaskEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * CRUD repository for tasks.
 */
@Repository
public interface TaskRepository extends ReactiveCrudRepository<TaskEntity, Long> {

    Flux<TaskEntity> findAllByUserId(Long userId);

    Mono<TaskEntity> findByIdAndUserId(Long taskId, Long userId);
}
