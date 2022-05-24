package com.acme.taskmanager.controller;

import com.acme.taskmanager.dto.ErrorResponseDto;
import com.acme.taskmanager.dto.TaskInfoDto;
import com.acme.taskmanager.dto.TaskRequestDto;
import com.acme.taskmanager.dto.TaskResponseDto;
import com.acme.taskmanager.entity.TaskEntity;
import com.acme.taskmanager.entity.UserEntity;
import com.acme.taskmanager.exception.ResponseEntityExceptionMapper;
import com.acme.taskmanager.repository.TaskRepository;
import com.acme.taskmanager.repository.UserRepository;
import com.acme.taskmanager.service.TaskService;
import com.acme.taskmanager.type.TaskStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.QueryTimeoutException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit test for {@link TaskController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class TaskControllerTest {
    private static final Long USER_ID = 1L;
    private static final Long TASK_ID = 2L;
    private static final LocalDateTime DATE_TIME = LocalDateTime.of(2022, 1, 30, 10, 20, 30);
    private static final TaskStatus STATUS = TaskStatus.PENDING;
    private static final String USERNAME = "homer";
    private static final String FIRST_NAME = "Homer";
    private static final String LAST_NAME = "Simpson";
    private static final String TASK_NAME = "donut";
    private static final String INVALID_TASK_NAME = null;
    private static final String DESCRIPTION = "buy donuts";
    private static final TaskRequestDto VALID_TASK_REQUEST = new TaskRequestDto(TASK_NAME, DESCRIPTION, DATE_TIME, STATUS);
    private static final TaskRequestDto INVALID_TASK_REQUEST = new TaskRequestDto(INVALID_TASK_NAME, DESCRIPTION, DATE_TIME, STATUS);
    private static final UserEntity USER_ENTITY = new UserEntity(USER_ID, USERNAME, FIRST_NAME, LAST_NAME);
    private static final TaskEntity TASK_ENTITY = new TaskEntity.Builder()
            .setId(TASK_ID)
            .setName(TASK_NAME)
            .setDescription(DESCRIPTION)
            .setUserId(USER_ID)
            .setDateTime(DATE_TIME)
            .setStatus(STATUS)
            .build();

    private WebTestClient webTestClient;

    @Autowired
    private TaskService taskService;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private TaskRepository taskRepository;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToController(new TaskController(taskService))
                .controllerAdvice(ResponseEntityExceptionMapper.class)
                .build();
    }

    @Test
    void shouldCreateTaskForValidRequest() {
        when(taskRepository.save(any(TaskEntity.class))).thenReturn(Mono.just(TASK_ENTITY));
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/api/user/{userId}/task", USER_ID)
                .bodyValue(VALID_TASK_REQUEST)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(TaskResponseDto.class)
                .value(allOf(
                        hasProperty("id", is(equalTo(TASK_ID))),
                        hasProperty("name", is(equalTo(TASK_NAME)))
                ));

        verify(taskRepository).save(isA(TaskEntity.class));
    }

    @Test
    void shouldNotCreateTaskForInvalidRequest() {
        webTestClient.post()
                .uri("/api/user/{userId}/task", USER_ID)
                .bodyValue(INVALID_TASK_REQUEST)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class);

        verify(taskRepository, never()).save(isA(TaskEntity.class));
    }

    @Test
    void shouldCreateTaskForNonExistingUser() {
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(false));

        webTestClient.post()
                .uri("/api/user/{userId}/task", USER_ID)
                .bodyValue(VALID_TASK_REQUEST)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(taskRepository, never()).save(isA(TaskEntity.class));
    }

    @Test
    void shouldNotCreateTaskWithDataAccessException() {
        when(taskRepository.save(any(TaskEntity.class))).thenThrow(new QueryTimeoutException("intentionally thrown for testing purposes"));
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(true));

        webTestClient.post()
                .uri("/api/user/{userId}/task", USER_ID)
                .bodyValue(VALID_TASK_REQUEST)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponseDto.class);
    }

    @Test
    void shouldUpdateTaskForValidRequest() {
        when(taskRepository.existsByIdAndUserId(USER_ID, TASK_ID)).thenReturn(Mono.just(true));
        when(taskRepository.updateNonNull(eq(TASK_ID), isA(TaskEntity.class))).thenReturn(Mono.just(1));

        webTestClient.put()
                .uri("/api/user/{userId}/task/{taskId}", USER_ID, TASK_ID)
                .bodyValue(VALID_TASK_REQUEST)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        verify(taskRepository).updateNonNull(eq(TASK_ID), isA(TaskEntity.class));
    }

    @Test
    void shouldNotUpdateTaskForNonExistingEntity() {
        when(taskRepository.existsByIdAndUserId(USER_ID, TASK_ID)).thenReturn(Mono.just(false));

        webTestClient.put()
                .uri("/api/user/{userId}", USER_ID)
                .bodyValue(VALID_TASK_REQUEST)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(taskRepository, never()).updateNonNull(anyLong(), isA(TaskEntity.class));
    }

    @Test
    void shouldDeleteExistingTask() {
        when(taskRepository.deleteById(TASK_ID)).thenReturn(Mono.empty());
        when(taskRepository.existsByIdAndUserId(USER_ID, TASK_ID)).thenReturn(Mono.just(true));

        webTestClient.delete()
                .uri("/api/user/{userId}/task/{taskId}", USER_ID, TASK_ID)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        verify(taskRepository).deleteById(TASK_ID);
    }

    @Test
    void shouldNotDeleteNotExistingTask() {
        when(taskRepository.deleteById(TASK_ID)).thenReturn(Mono.empty());
        when(taskRepository.existsByIdAndUserId(USER_ID, TASK_ID)).thenReturn(Mono.just(false));

        webTestClient.delete()
                .uri("/api/user/{userId}/task/{taskId}", USER_ID, TASK_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(taskRepository, never()).deleteById(TASK_ID);
    }

    @Test
    void shouldGetTaskInfo() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(USER_ENTITY));
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Mono.just(TASK_ENTITY));

        webTestClient.get()
                .uri("/api/user/{userId}/task/{taskId}", USER_ID, TASK_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(TaskInfoDto.class)
                .value(allOf(
                        hasProperty("id", is(equalTo(TASK_ID))),
                        hasProperty("name", is(equalTo(TASK_NAME))),
                        hasProperty("description", is(equalTo(DESCRIPTION))),
                        hasProperty("dateTime", is(equalTo(DATE_TIME))),
                        hasProperty("status", is(equalTo(STATUS))),
                        hasProperty("user", allOf(
                                hasProperty("id", is(equalTo(USER_ID))),
                                hasProperty("username", is(equalTo(USERNAME))),
                                hasProperty("firstName", is(equalTo(FIRST_NAME))),
                                hasProperty("lastName", is(equalTo(LAST_NAME)))
                        ))));

        verify(userRepository).findById(USER_ID);
        verify(taskRepository).findByIdAndUserId(TASK_ID, USER_ID);
    }

    @Test
    void shouldNotGetTaskInfoForNonExistingUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/user/{userId}/task/{taskId}", USER_ID, TASK_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(userRepository).findById(USER_ID);
        verify(taskRepository, never()).findByIdAndUserId(TASK_ID, USER_ID);
    }

    @Test
    void shouldNotGetTaskInfoForNonExistingTask() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(USER_ENTITY));
        when(taskRepository.findByIdAndUserId(TASK_ID, USER_ID)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/user/{userId}/task/{taskId}", USER_ID, TASK_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(userRepository).findById(USER_ID);
        verify(taskRepository).findByIdAndUserId(TASK_ID, USER_ID);
    }

    @Test
    void shouldListAllTasksForExistingUser() {
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(true));
        when(taskRepository.findAllByUserId(USER_ID)).thenReturn(Flux.just(TASK_ENTITY));

        webTestClient.get()
                .uri("/api/user/{userId}/task", USER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskResponseDto.class)
                .value(hasItems(allOf(
                        hasProperty("id", is(equalTo(TASK_ID))),
                        hasProperty("name", is(equalTo(TASK_NAME)))
                )));

        verify(userRepository).existsById(USER_ID);
        verify(taskRepository).findAllByUserId(USER_ID);
    }

    @Test
    void shouldReturnWithEmptyListForNoResults() {
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(true));
        when(taskRepository.findAllByUserId(USER_ID)).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/user/{userId}/task", USER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(TaskResponseDto.class)
                .value(empty());

        verify(userRepository).existsById(USER_ID);
        verify(taskRepository).findAllByUserId(USER_ID);
    }

    @Test
    void shouldNotListTasksForNonExistingUser() {
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(false));

        webTestClient.get()
                .uri("/api/user/{userId}/task", USER_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBodyList(ErrorResponseDto.class);

        verify(userRepository).existsById(USER_ID);
        verify(taskRepository, never()).findAllByUserId(USER_ID);
    }
}
