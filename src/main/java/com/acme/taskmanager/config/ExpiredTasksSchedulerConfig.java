package com.acme.taskmanager.config;

import com.acme.taskmanager.repository.TaskRepository;
import com.acme.taskmanager.type.TaskStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.LocalDateTime;

/**
 * Config for a scheduled job to check all tasks in the Database - those that have a status of "PENDING"
 * and who date_time has passed.
 */
@Configuration
@EnableScheduling
@ConditionalOnProperty(value = "expired-task-scheduler.enabled", havingValue = "true", matchIfMissing = true)
public class ExpiredTasksSchedulerConfig {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExpiredTasksSchedulerConfig.class);

    private final TaskRepository taskRepository;
    private final Integer expirationInDays;

    @Autowired
    public ExpiredTasksSchedulerConfig(TaskRepository taskRepository,
                                       @Value("${expired-task-scheduler.expiration-in-days}") Integer expirationInDays) {
        this.taskRepository = taskRepository;
        this.expirationInDays = expirationInDays;
        LOGGER.info("Expired task scheduler initialized with expirationInDays={}", expirationInDays);
    }

    @Scheduled(fixedDelayString = "${expired-task-scheduler.delay-in-ms}", initialDelayString = "${expired-task-scheduler.delay-in-ms}")
    public void scheduleExpiredTaskUpdates() {
        LocalDateTime expirationDateTime = LocalDateTime.now().minusDays(expirationInDays);
        taskRepository.updatePendingTasksBeforeDateTime(expirationDateTime, TaskStatus.DONE)
                .subscribe(
                        count -> LOGGER.info("Updated {} expired task(s)", count),
                        error -> LOGGER.error("Could not update expired tasks", error)
                );
    }
}
