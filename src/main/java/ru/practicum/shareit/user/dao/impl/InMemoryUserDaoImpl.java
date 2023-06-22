package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Repository;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.common.crud.default_implementations.InMemoryCrudDaoImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

@Repository
public class InMemoryUserDaoImpl extends InMemoryCrudDaoImpl<User> implements UserDao {

    public InMemoryUserDaoImpl() {
        super();
        this.type = "user";
    }

    @Override
    public User update(long userId, User user) {
        String userName = user.getName();
        String userEmail = user.getEmail();
        User savedUser = getById(userId);

        savedUser = savedUser.toBuilder()
                .name(ShareItConstants.NOT_ASSIGNED.equals(userName) ? savedUser.getName() : userName)
                .email(ShareItConstants.NOT_ASSIGNED.equals(userEmail) ? savedUser.getEmail() : userEmail)
                .build();
        dataMap.put(userId, savedUser);
        return dataMap.get(userId);
    }

    @Override
    public boolean isEmailDuplicate(String email) {
        return dataMap.values().stream()
                .map(User::getEmail)
                .anyMatch(userEmail -> userEmail.equals(email));
    }

}