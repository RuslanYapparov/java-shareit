package ru.practicum.shareit_gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import ru.practicum.shareit_gateway.exception.BadRequestBodyException;
import ru.practicum.shareit_gateway.user.dto.UserRestCommand;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserRestCommand userRestCommand) {
        log.debug("Gateway received request to create new user, user={}", userRestCommand);
        checkUserEmail(userRestCommand.getEmail());
        return userClient.save(userRestCommand);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.debug("Gateway received request to get all users");
        return userClient.getAll();
    }

    @GetMapping("{user_id}")
    public ResponseEntity<Object> getById(@Positive @PathVariable(value = "user_id") long userId) {
        log.debug("Gateway received request to get user by id, userId={}", userId);
        return userClient.getById(userId);
    }

    @PatchMapping("{user_id}")
    public ResponseEntity<Object> update(@Positive @PathVariable(value = "user_id") long userId,
                               @RequestBody UserRestCommand userRestCommand) {
        log.debug("Gateway received request to update user, userId={}, updatedUser={}", userId, userRestCommand);
        return userClient.update(userId, userRestCommand);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAll() {
        log.debug("Gateway received request to delete all users");
        return userClient.deleteAll();
    }

    @DeleteMapping("{user_id}")
    public ResponseEntity<Object> deleteById(@Positive @PathVariable(value = "user_id") long userId) {
        log.debug("Gateway received request to delete user by id, userId={}", userId);
        return userClient.deleteById(userId);
    }

    private void checkUserEmail(String email) {
        String[] emailElements = email.split("@");
        if (!emailElements[1].contains(".")) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя. Причина - " +
                    "неправильный формат адреса электронной почты '%s': отсутствует точка в домене", email));
        }
    }

}