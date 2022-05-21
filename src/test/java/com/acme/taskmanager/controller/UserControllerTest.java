package com.acme.taskmanager.controller;

import com.acme.taskmanager.dto.ErrorResponseDto;
import com.acme.taskmanager.dto.UserInfoDto;
import com.acme.taskmanager.dto.UserRequestDto;
import com.acme.taskmanager.dto.UserResponseDto;
import com.acme.taskmanager.entity.UserEntity;
import com.acme.taskmanager.repository.UserRepository;
import com.acme.taskmanager.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.hasProperty;
import static org.hamcrest.Matchers.is;
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
    private static final UserRequestDto VALID_USER_REQUEST = new UserRequestDto("homer", "Homer", "Simpson");
    private static final UserRequestDto INVALID_USER_REQUEST = new UserRequestDto(null, "Homer", "Simpson");
    private static final UserEntity USER_ENTITY = new UserEntity(USER_ID, "homer", "Homer", "Simpson");
    private static final UserResponseDto USER_RESPONSE = new UserResponseDto(USER_ID, "homer");
    private static final UserInfoDto USER_INFO = new UserInfoDto(USER_ID, "homer", "Homer", "Simpson");

    private WebTestClient webTestClient;

    @Autowired
    private UserService userService;

    @MockBean
    UserRepository userRepository;

    @BeforeEach
    public void setup() {
        webTestClient = WebTestClient.bindToController(new UserController(userService)).build();
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
                        hasProperty("id", is(equalTo(USER_RESPONSE.getId()))),
                        hasProperty("username", is(equalTo(USER_RESPONSE.getUsername())))
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
    void shouldGetUserInfo() {
        when(userRepository.findById(USER_ID)).thenReturn(Mono.just(USER_ENTITY));

        webTestClient.get()
                .uri("/api/user/{userId}", USER_ID)
                .exchange()
                .expectStatus().isOk()
                .expectBody(UserInfoDto.class)
                .value(allOf(
                        hasProperty("id", is(equalTo(USER_INFO.getId()))),
                        hasProperty("username", is(equalTo(USER_INFO.getUsername()))),
                        hasProperty("firstName", is(equalTo(USER_INFO.getFirstName()))),
                        hasProperty("lastName", is(equalTo(USER_INFO.getLastName())))
                ));

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
                        hasProperty("id", is(equalTo(USER_RESPONSE.getId()))),
                        hasProperty("username", is(equalTo(USER_RESPONSE.getUsername())))
                )));

        verify(userRepository).findAll();
    }
}
