package com.acme.taskmanager.repository;

import com.acme.taskmanager.entity.TaskEntity;
import com.acme.taskmanager.type.TaskStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.springframework.data.relational.core.query.Query.query;
import static org.springframework.data.relational.core.query.Criteria.where;
import static org.springframework.data.relational.core.query.Update.update;

/**
 * CRUD repository for tasks.
 */
@Repository
public class TaskRepository extends CriteriaBasedRepository<TaskEntity, Long> {

    @Autowired
    public TaskRepository(R2dbcEntityTemplate r2dbcEntityTemplate) {
        super(r2dbcEntityTemplate);
    }

    @Override
    public Class<TaskEntity> getEntityClass() {
        return TaskEntity.class;
    }

    public Flux<TaskEntity> findAllByUserId(Long userId) {
        return r2dbcEntityTemplate.select(query(where("user_id").is(userId)), getEntityClass());
    }

    public Mono<TaskEntity> findByIdAndUserId(Long taskId, Long userId) {
        return r2dbcEntityTemplate.selectOne(query(where(identifier).is(taskId)
                .and("user_id").is(userId)), getEntityClass());
    }

    public Mono<Boolean> existsByIdAndUserId(Long taskId, Long userId) {
        return r2dbcEntityTemplate.exists(query(where(identifier).is(taskId)
                .and("user_id").is(userId)), getEntityClass());
    }

    public Mono<Integer> updatePendingTasksBeforeDateTime(LocalDateTime expirationDateTime, TaskStatus status) {
        return r2dbcEntityTemplate.update(getEntityClass())
                .matching(query(where("status").is(TaskStatus.PENDING)
                        .and("date_time").lessThanOrEquals(expirationDateTime)))
                .apply(update("status", status));
    }
}
