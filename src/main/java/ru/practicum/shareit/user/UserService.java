package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.*;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {
    private final UserDao userDao;

    public User save(User user) {
        checkUserEmail(user);
        user = userDao.save(user);
        log.info("Сохранен новый пользователь. Присвоен идентификатор '{}'", user.getId());
        return user;
    }

    public List<User> getAll() {
        List<User> users = userDao.getAll();
        log.info("Запрошен список всех сохраненных пользователей. " +
                "Количество сохраненных пользователей - {}", users.size());
        return users;
    }

    public User getById(long id) {
        User user = userDao.getById(id);
        log.info("Запрошен пользователь с идентификатором '{}'", id);
        return user;
    }

    public int getQuantity() {
        return userDao.getQuantity();
    }

    public User update(long userId, User user) {
        User savedUser = userDao.getById(userId);

        if (!savedUser.getEmail().equals(user.getEmail())) {
            checkUserEmail(user);
        }
        savedUser = userDao.update(userId, user);
        log.info("Обновлены данные пользователя с id'{}'", userId);
        return savedUser;
    }

    public void deleteAll() {
        int quantity = getQuantity();
        userDao.deleteAll();
        if (getQuantity() != 0) {
            throw new InternalLogicException("Произошла ощибка при удалении всех пользователей. Если " +
                        "Вы видите это сообщение, пожалуйста, обратитесь к разработчикам");
            }
        log.info("Удалены данные всех пользователей из хранилища. Количество объектов, которое было в хранилище " +
                    "до удаления: {}", quantity);
    }

    public User deleteById(long id) {
        User user = userDao.deleteById(id);
        log.info("Удалены данные пользователя с идентификатором '{}'", id);
        return user;
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