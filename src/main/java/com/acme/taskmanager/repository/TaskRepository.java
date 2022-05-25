package com.acme.taskmanager.repository;

import com.acme.taskmanager.entity.TaskEntity;
import com.acme.taskmanager.type.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger LOGGER = LoggerFactory.getLogger(TaskRepository.class);

    private static final String USER_ID = "user_id";
    private static final String STATUS = "status";
    private static final String DATE_TIME = "date_time";

    @Autowired
    public TaskRepository(R2dbcEntityTemplate r2dbcEntityTemplate) {
        super(r2dbcEntityTemplate);
    }

    @Override
    public Class<TaskEntity> getEntityClass() {
        return TaskEntity.class;
    }

    public Flux<TaskEntity> findAllByUserId(Long userId) {
        return r2dbcEntityTemplate.select(query(where(USER_ID).is(userId)), getEntityClass());
    }

    public Mono<TaskEntity> findByIdAndUserId(Long taskId, Long userId) {
        return r2dbcEntityTemplate.selectOne(query(where(identifier).is(taskId)
                .and(USER_ID).is(userId)), getEntityClass());
    }

    public Mono<Boolean> existsByIdAndUserId(Long taskId, Long userId) {
        return r2dbcEntityTemplate.exists(query(where(identifier).is(taskId)
                .and(USER_ID).is(userId)), getEntityClass());
    }

    public Mono<Integer> updatePendingTasksBeforeDateTime(LocalDateTime expirationDateTime, TaskStatus status, Integer limit) {
        return r2dbcEntityTemplate.select(query(where(STATUS).is(TaskStatus.PENDING)
                        .and(DATE_TIME).lessThanOrEquals(expirationDateTime))
                        .limit(limit), getEntityClass())
                .doOnNext(task -> LOGGER.info("Expired pending task id={} and name={}", task.getId(), task.getName()))
                .map(TaskEntity::getId)
                .collectList()
                .flatMap(ids -> r2dbcEntityTemplate.update(getEntityClass())
                        .matching(query(where(identifier).in(ids)))
                        .apply(update(STATUS, status))
                );
    }
}
