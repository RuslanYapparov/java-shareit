package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.common.CrudDao;
import ru.practicum.shareit.user.User;

public interface UserDao extends CrudDao<User> {

    boolean isEmailDuplicate(String email);

}