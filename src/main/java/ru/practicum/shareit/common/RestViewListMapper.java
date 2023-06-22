package ru.practicum.shareit.common;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import ru.practicum.shareit.item.Item;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemRestView;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserRestView;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class RestViewListMapper {
    private final UserMapper userMapper;
    private final ItemMapper itemMapper;

    public List<UserRestView> mapListOfUsers(List<User> users) {
        return users.stream()
                .map(userMapper::toRestView)
                .collect(Collectors.toList());
    }

    public List<ItemRestView> mapListOfItems(List<Item> items) {
        return items.stream()
                .map(itemMapper::toRestView)
                .collect(Collectors.toList());
    }

}