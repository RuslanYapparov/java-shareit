package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;

import ru.practicum.shareit.common.RestViewListMapper;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

import java.util.List;

@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping(path = "/users")
public class UserController {
    private final UserService userService;
    private final UserMapper userMapper;
    private final RestViewListMapper restViewListMapper;

    @PostMapping
    public UserRestView save(@RequestBody @Valid UserRestCommand userRestCommand) {
        User user = userMapper.fromRestCommand(userRestCommand);
        user = userService.save(user);
        return userMapper.toRestView(user);
    }

    @GetMapping
    public List<UserRestView> getAll() {
        return restViewListMapper.mapListOfUsers(userService.getAll());
    }

    @GetMapping("{user_id}")
    public UserRestView getById(@PathVariable(value = "user_id") @Positive long id) {
        return userMapper.toRestView(userService.getById(id));
    }

    @PatchMapping("{user_id}")
    public UserRestView update(@PathVariable(value = "user_id") @Positive long userId,
                               @RequestBody UserRestCommand userRestCommand) {
        User user = userMapper.fromRestCommand(userRestCommand);
        user = userService.update(userId, user);
        return userMapper.toRestView(user);
    }

    @DeleteMapping
    public void deleteAll() {
        userService.deleteAll();
    }

    @DeleteMapping("{user_id}")
    public UserRestView deleteById(@PathVariable(value = "user_id") @Positive long id) {
        return userMapper.toRestView(userService.deleteById(id));
    }

}