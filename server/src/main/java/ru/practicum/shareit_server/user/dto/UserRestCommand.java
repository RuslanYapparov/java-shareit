package ru.practicum.shareit_server.user.dto;

import lombok.*;

@Value
@Builder(toBuilder = true)
public class UserRestCommand {
    String name;
    String email;

}