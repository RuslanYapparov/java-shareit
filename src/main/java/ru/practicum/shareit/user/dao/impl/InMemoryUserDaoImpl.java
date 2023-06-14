package ru.practicum.shareit.user.dao.impl;

import org.springframework.stereotype.Repository;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.default_implementations.InMemoryCrudDaoImpl;
import ru.practicum.shareit.user.User;
import ru.practicum.shareit.user.dao.UserDao;

@Repository
public class InMemoryUserDaoImpl extends InMemoryCrudDaoImpl<User> implements UserDao {

    public InMemoryUserDaoImpl() {
        super();
        this.type = "user";
    }

    @Override
    public User update(long userId, User user) throws ObjectNotFoundException {
        String userName = user.getName();
        String userEmail = user.getEmail();
        User savedUser = this.getById(userId);

        savedUser = savedUser.toBuilder()
                .name(userName.equals(ShareItConstants.NOT_ASSIGNED) ? savedUser.getName() : userName)
                .email(userEmail.equals(ShareItConstants.NOT_ASSIGNED) ? savedUser.getEmail() : userEmail)
                .build();
        savedUser.setId(userId);
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