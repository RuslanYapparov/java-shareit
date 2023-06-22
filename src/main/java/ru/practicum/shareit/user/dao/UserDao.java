package ru.practicum.shareit.user.dao;

import ru.practicum.shareit.common.crud.CrudDao;
import ru.practicum.shareit.user.User;

public interface UserDao extends CrudDao<User> {

    boolean isEmailDuplicate(String email);

}