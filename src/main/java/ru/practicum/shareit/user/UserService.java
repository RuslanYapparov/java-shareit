package ru.practicum.shareit.user;

import org.springframework.stereotype.Service;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.common.default_implementations.CrudServiceImpl;
import ru.practicum.shareit.user.dao.UserDao;

@Service
public class UserService extends CrudServiceImpl<User> {

    public UserService(UserDao userDao) {
        this.objectDao = userDao;
        this.userDao = userDao;
        this.type = "user";
    }

    @Override
    public User save(User user) throws ObjectAlreadyExistsException, BadRequestBodyException {
        this.checkUserEmail(user);
        return super.save(user);
    }

    @Override
    public User update(long userId, User user) {
        User savedUser = userDao.getById(userId);

        if (savedUser.getEmail().equals(user.getEmail())) {
            return super.update(userId, user);
        }
        this.checkUserEmail(user);
        return super.update(userId, user);
    }

    private void checkUserEmail(User user)  {
        String userEmail = user.getEmail();
        if (userEmail.equals(ShareItConstants.NOT_ASSIGNED)) {
            return;
        }
        if (userDao.isEmailDuplicate(userEmail)) {
            throw new ObjectAlreadyExistsException(String.format("Пользователь с адресом электронной почты '%s' " +
                    "уже сохранен", userEmail));
        }
        String[] emailElements = userEmail.split("@");
        if (!emailElements[1].contains(".")) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить пользователя '%s'. Причина - " +
                    "неправильный формат адреса электронной почты '%s': отсутствует точка в домене", user.getName(),
                    user.getEmail()));
        }
    }

}
