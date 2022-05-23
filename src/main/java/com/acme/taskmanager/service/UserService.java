package com.acme.taskmanager.service;

import com.acme.taskmanager.dto.UserRequestDto;
import com.acme.taskmanager.dto.UserInfoDto;
import com.acme.taskmanager.dto.UserResponseDto;
import com.acme.taskmanager.entity.UserEntity;
import com.acme.taskmanager.exception.EntityNotFoundException;
import com.acme.taskmanager.mapper.UserMapper;
import com.acme.taskmanager.repository.EntityUpdater;
import com.acme.taskmanager.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service for managing users.
 */
@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final EntityUpdater<UserEntity, Long> entityUpdater;

    @Autowired
    public UserService(UserRepository userRepository, UserMapper userMapper, EntityUpdater<UserEntity, Long> entityUpdater) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.entityUpdater = entityUpdater;
    }

    public Mono<UserResponseDto> createUser(UserRequestDto user) {
        return userRepository.save(userMapper.toEntity(user))
                .doOnError(error -> LOGGER.error("Could not create user", error))
                .map(userMapper::toResponseDto);
    }

    public Mono<Void> updateUser(Long userId, UserRequestDto user) {
        return userRepository.existsById(userId)
                .flatMap(exists -> exists ? Mono.just(exists) : Mono.empty())
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .flatMap(exists -> entityUpdater.updateNonNull(userId, userMapper.toEntity(user)))
                .doOnError(error -> LOGGER.error("Could not update user with userId=" + userId, error))
                .flatMap(resultCount -> resultCount > 0 ? Mono.empty() : Mono.error(new EntityNotFoundException("Could not update entity")));
    }

    public Flux<UserResponseDto> listAllUsers() {
        return userRepository.findAll()
                .doOnError(error -> LOGGER.error("Could not list users", error))
                .map(userMapper::toResponseDto);
    }

    public Mono<UserInfoDto> getUserInfo(Long userId) {
        return userRepository.findById(userId)
                .doOnError(error -> LOGGER.error("Could not find user with userId=" + userId, error))
                .switchIfEmpty(Mono.error(new EntityNotFoundException("user entity does not exists")))
                .map(userMapper::toInfoDto);
    }
}
