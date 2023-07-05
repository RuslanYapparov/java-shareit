package ru.practicum.shareit.user;

import org.mapstruct.Mapper;
import ru.practicum.shareit.common.ObjectMapper;
import ru.practicum.shareit.user.dao.UserEntity;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

@Mapper(componentModel = "spring")
public interface UserMapper extends ObjectMapper<UserEntity, User, UserRestCommand, UserRestView> {

}