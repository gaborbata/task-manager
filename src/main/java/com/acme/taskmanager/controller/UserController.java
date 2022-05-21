package com.acme.taskmanager.controller;

import com.acme.taskmanager.dto.UserRequestDto;
import com.acme.taskmanager.dto.UserInfoDto;
import com.acme.taskmanager.dto.UserResponseDto;
import com.acme.taskmanager.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
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
 * Controller for handling user related endpoints.
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<UserResponseDto> createUser(@Valid @RequestBody UserRequestDto user) {
        return userService.createUser(user);
    }

    @PutMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> updateUser(@PathVariable Long userId, @RequestBody UserRequestDto user) {
        return userService.updateUser(userId, user);
    }

    @GetMapping("/{userId}")
    public Mono<UserInfoDto> getUserInfo(@PathVariable Long userId) {
        return userService.getUserInfo(userId);
    }

    @GetMapping
    public Flux<UserResponseDto> listAllUsers() {
        return userService.listAllUsers();
    }
}
