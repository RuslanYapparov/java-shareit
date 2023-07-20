package ru.practicum.shareit_gateway.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit_gateway.user.dto.UserRestCommand;
import ru.practicum.shareit_gateway.util.EndpointObjectsValidator;

import javax.validation.Valid;
import javax.validation.constraints.Positive;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
@Slf4j
public class UserController {
    private final UserClient userClient;

    @PostMapping
    public ResponseEntity<Object> save(@Valid @RequestBody UserRestCommand userRestCommand) {
        log.debug("Gateway received ru.practicum.shareit.request to create new ru.practicum.shareit.user, ru.practicum.shareit.user={}", userRestCommand);
        EndpointObjectsValidator.checkUserEmail(userRestCommand.getEmail());
        return userClient.save(userRestCommand);
    }

    @GetMapping
    public ResponseEntity<Object> getAll() {
        log.debug("Gateway received ru.practicum.shareit.request to get all users");
        return userClient.getAll();
    }

    @GetMapping("{user_id}")
    public ResponseEntity<Object> getById(@Positive @PathVariable(value = "user_id") long userId) {
        log.debug("Gateway received ru.practicum.shareit.request to get ru.practicum.shareit.user by id, userId={}", userId);
        return userClient.getById(userId);
    }

    @PatchMapping("{user_id}")
    public ResponseEntity<Object> update(@Positive @PathVariable(value = "user_id") long userId,
                               @RequestBody UserRestCommand userRestCommand) {
        log.debug("Gateway received ru.practicum.shareit.request to update ru.practicum.shareit.user, userId={}, updatedUser={}", userId, userRestCommand);
        return userClient.update(userId, userRestCommand);
    }

    @DeleteMapping
    public ResponseEntity<Object> deleteAll() {
        log.debug("Gateway received ru.practicum.shareit.request to delete all users");
        return userClient.deleteAll();
    }

    @DeleteMapping("{user_id}")
    public ResponseEntity<Object> deleteById(@Positive @PathVariable(value = "user_id") long userId) {
        log.debug("Gateway received ru.practicum.shareit.request to delete ru.practicum.shareit.user by id, userId={}", userId);
        return userClient.deleteById(userId);
    }

}