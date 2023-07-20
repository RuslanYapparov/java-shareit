package ru.practicum.shareit_server.user.dao;

import java.time.LocalDateTime;

public interface UserShort {

    String getName();

    String getEmail();

    LocalDateTime getCreated();

}