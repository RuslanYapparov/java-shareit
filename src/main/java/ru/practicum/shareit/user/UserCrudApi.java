package ru.practicum.shareit.user;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

import java.util.List;

public interface UserCrudApi {

    UserRestView save(@Valid UserRestCommand userRestCommand);

    List<UserRestView> getAll();

    UserRestView getById(@Positive long userId);

    UserRestView update(@Positive long userId, UserRestCommand userRestCommand);

    void deleteAll();

    UserRestView deleteById(@Positive long userId);

}