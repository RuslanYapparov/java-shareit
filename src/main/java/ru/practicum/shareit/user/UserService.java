package ru.practicum.shareit.user;

import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

import java.util.List;

public interface UserService {

    UserRestView save(UserRestCommand userRestCommand);

    List<UserRestView> getAll();

    UserRestView getById(long userId);

    UserRestView update(long userId, UserRestCommand userRestCommand);

    void deleteAll();

    UserRestView deleteById(long userId);

}