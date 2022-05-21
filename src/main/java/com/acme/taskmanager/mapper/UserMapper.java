package com.acme.taskmanager.mapper;

import com.acme.taskmanager.dto.UserInfoDto;
import com.acme.taskmanager.dto.UserRequestDto;
import com.acme.taskmanager.dto.UserResponseDto;
import com.acme.taskmanager.entity.UserEntity;
import org.mapstruct.Mapper;

/**
 * Mapper to convert user DTOs to entities and vice-versa.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    UserEntity toEntity(UserRequestDto user);

    UserResponseDto toResponseDto(UserEntity userEntity);

    UserInfoDto toInfoDto(UserEntity userEntity);
}
