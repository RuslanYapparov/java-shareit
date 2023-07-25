package ru.practicum.shareit_server.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;

    @PostMapping
    public UserRestView save(@RequestBody UserRestCommand userRestCommand) {
        return userService.save(userRestCommand);
    }

    @GetMapping
    public List<UserRestView> getAll() {
        return userService.getAll();
    }

    @GetMapping("{user_id}")
    public UserRestView getById(@PathVariable(value = "user_id") long userId) {
        return userService.getById(userId);
    }

    @PatchMapping("{user_id}")
    public UserRestView update(@PathVariable(value = "user_id") long userId,
                               @RequestBody UserRestCommand userRestCommand) {
        return userService.update(userId, userRestCommand);
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteAll();
    }

    @DeleteMapping("{user_id}")
    public UserRestView deleteById(@PathVariable(value = "user_id") long userId) {
        return userService.deleteById(userId);
    }

}