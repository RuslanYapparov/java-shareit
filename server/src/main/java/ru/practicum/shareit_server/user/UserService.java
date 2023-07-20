package ru.practicum.shareit_server.user;

import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

import java.util.List;

public interface UserService {

    UserRestView save(UserRestCommand userRestCommand);

    List<UserRestView> getAll();

    UserRestView getById(long userId);

    UserRestView update(long userId, UserRestCommand userRestCommand);

    void deleteAll();

    UserRestView deleteById(long userId);

}