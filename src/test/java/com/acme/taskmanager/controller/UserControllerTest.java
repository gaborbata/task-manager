package com.acme.taskmanager.controller;

import com.acme.taskmanager.dto.ErrorResponseDto;
import com.acme.taskmanager.dto.UserInfoDto;
import com.acme.taskmanager.dto.UserRequestDto;
import com.acme.taskmanager.dto.UserResponseDto;
import com.acme.taskmanager.entity.UserEntity;
import com.acme.taskmanager.exception.ResponseEntityExceptionMapper;
import com.acme.taskmanager.repository.UserRepository;
import com.acme.taskmanager.service.UserService;
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
 * Unit test for {@link UserController}.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@ActiveProfiles("test")
class UserControllerTest {
    private static final Long USER_ID = 1L;
    private static final String INVALID_USERNAME = null;
    private static final String USERNAME = "homer";
    private static final String FIRST_NAME = "Homer";
    private static final String LAST_NAME = "Simpson";
    private static final UserRequestDto VALID_USER_REQUEST = new UserRequestDto(USERNAME, FIRST_NAME, LAST_NAME);
    private static final UserRequestDto INVALID_USER_REQUEST = new UserRequestDto(INVALID_USERNAME, FIRST_NAME, LAST_NAME);
    private static final UserEntity USER_ENTITY = new UserEntity(USER_ID, USERNAME, FIRST_NAME, LAST_NAME);

    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @MockBean
    private UserRepository userRepository;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToController(new UserController(userService))
                .controllerAdvice(ResponseEntityExceptionMapper.class)
                .build();
    }

    @Test
    void shouldCreateUserForValidRequest() {
        when(userRepository.save(any(UserEntity.class))).thenReturn(Mono.just(USER_ENTITY));

        webTestClient.post()
                .uri("/api/user")
                .bodyValue(VALID_USER_REQUEST)
                .exchange()
                .expectStatus().isCreated()
                .expectBody(UserResponseDto.class)
                .value(allOf(
                        hasProperty("id", is(equalTo(USER_ID))),
                        hasProperty("username", is(equalTo(USERNAME)))
                ));

        verify(userRepository).save(isA(UserEntity.class));
    }

    @Test
    void shouldNotCreateUserForInvalidRequest() {
        webTestClient.post()
                .uri("/api/user")
                .bodyValue(INVALID_USER_REQUEST)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ErrorResponseDto.class);

        verify(userRepository, never()).save(isA(UserEntity.class));
    }

    @Test
    void shouldNotCreateUserWithDataAccessException() {
        when(userRepository.save(any(UserEntity.class))).thenThrow(new QueryTimeoutException("intentionally thrown for testing purposes"));

        webTestClient.post()
                .uri("/api/user")
                .bodyValue(VALID_USER_REQUEST)
                .exchange()
                .expectStatus().is5xxServerError()
                .expectBody(ErrorResponseDto.class);
    }

    @Test
    void shouldUpdateUserForValidRequest() {
        when(userRepository.existsById(USER_ID)).thenReturn(Mono.just(true));
        when(userRepository.updateNonNull(eq(USER_ID), isA(UserEntity.class))).thenReturn(Mono.just(1));

        webTestClient.put()
                .uri("/api/user/{userId}", USER_ID)
                .bodyValue(VALID_USER_REQUEST)
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(Void.class);

        verify(userRepository).updateNonNull(eq(USER_ID), isA(UserEntity.class));
    }

    @Test
    void shouldNotUpdateUserForNonExistingUser() {
        when(userRepository.existsById(anyLong())).thenReturn(Mono.just(false));

        webTestClient.put()
                .uri("/api/user/{userId}", USER_ID)
                .bodyValue(VALID_USER_REQUEST)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(userRepository, never()).updateNonNull(anyLong(), isA(UserEntity.class));
    }

    @Test
    void shouldGetUserInfo() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(USER_ENTITY));

        webTestClient.get()
                .uri("/api/user/{userId}", USER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserInfoDto.class)
                .value(allOf(
                        hasProperty("id", is(equalTo(USER_ID))),
                        hasProperty("username", is(equalTo(USERNAME))),
                        hasProperty("firstName", is(equalTo(FIRST_NAME))),
                        hasProperty("lastName", is(equalTo(LAST_NAME)))
                ));

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldNotGetUserInfoForNonExistingUser() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.empty());

        webTestClient.get()
                .uri("/api/user/{userId}", USER_ID)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ErrorResponseDto.class);

        verify(userRepository).findById(USER_ID);
    }

    @Test
    void shouldListAllUsers() {
        when(userRepository.findAll()).thenReturn(Flux.just(USER_ENTITY));

        webTestClient.get()
                .uri("/api/user")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .value(hasItems(allOf(
                        hasProperty("id", is(equalTo(USER_ID))),
                        hasProperty("username", is(equalTo(USERNAME)))
                )));

        verify(userRepository).findAll();
    }

    @Test
    void shouldReturnWithEmptyListForNoResults() {
        when(userRepository.findAll()).thenReturn(Flux.empty());

        webTestClient.get()
                .uri("/api/user")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(UserResponseDto.class)
                .value(empty());

        verify(userRepository).findAll();
    }
}
