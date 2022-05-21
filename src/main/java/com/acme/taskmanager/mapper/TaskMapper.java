package com.acme.taskmanager.mapper;

import com.acme.taskmanager.dto.TaskRequestDto;
import com.acme.taskmanager.dto.TaskInfoDto;
import com.acme.taskmanager.dto.TaskResponseDto;
import com.acme.taskmanager.entity.TaskEntity;
import com.acme.taskmanager.entity.UserEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper to convert task DTOs to entities and vice-versa.
 */
@Mapper(componentModel = "spring", uses = UserMapper.class)
public interface TaskMapper {

    TaskEntity toEntity(TaskRequestDto task);

    TaskEntity.Builder toBuilder(TaskRequestDto taskDto);

    TaskResponseDto toReponseDto(TaskEntity taskEntity);

    @Mapping(source = "taskEntity.id", target = "id")
    @Mapping(source = "userEntity", target = "user")
    TaskInfoDto toInfoDto(TaskEntity taskEntity, UserEntity userEntity);
}
