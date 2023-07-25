package ru.practicum.shareit_server.user;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.shareit_server.common.ObjectMapper;
import ru.practicum.shareit_server.user.dao.UserEntity;
import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

@Mapper(componentModel = "spring")
public interface UserMapper extends ObjectMapper<UserEntity, User, UserRestCommand, UserRestView> {

    @Override
    @Mapping(target = "avatarUri", expression = "java(java.net.URI.create(userEntity.getAvatarUri()))")
    User fromDbEntity(UserEntity userEntity);

    @Override
    @Mapping(target = "avatarUri", expression = "java(user.getAvatarUri().getPath())")
    UserEntity toDbEntity(User user);


}