package ru.practicum.shareit.common.crud.default_implementations;

import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.InternalLogicException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.crud.CrudDao;
import ru.practicum.shareit.common.crud.CrudService;
import ru.practicum.shareit.common.IdentificableObject;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Slf4j
public class CrudServiceImpl<T extends IdentificableObject> implements CrudService<T> {
    protected CrudDao<T> objectDao;
    protected UserDao userDao;
    protected String type = "Some_type";

    @Override
    public T save(long userId, T object) {
        checkUserExisting(userId);
        object = objectDao.save(object);
        log.info("Сохранен новый объект '{}'. Присвоен идентификатор '{}'", type, object.getId());
        return object;
    }

    @Override
    public List<T> getAll(long userId) {
        List<T> objects;
        if (userId == 0) {
            objects = objectDao.getAll();
            log.info("Запрошен список всех сохраненных объектов '{}'. Количество сохраненных объектов - {}",
                    type, objects.size());
        } else {
            checkUserExisting(userId);
            objects = objectDao.getAllEntitiesOfUserById(userId);
            log.info("Запрошен список всех сохраненных объектов '{}', принадлежащих пользователю с id'{}'. " +
                            "Количество сохраненных объектов - {}", type, userId, objects.size());
        }
        return objects;
    }

    @Override
    public T getById(long userId, long id) {
        checkUserExisting(userId);
        T object = objectDao.getById(id);
        log.info("Запрошен объект '{}' с идентификатором '{}'", type, id);
        return object;
    }

    @Override
    public int getQuantity() {
        return objectDao.getQuantity();
    }

    @Override
    public T update(long userId, long objectId, T object) {
        checkUserExisting(userId);
        object = objectDao.update(objectId, object);
        log.info("Обновлены данные объекта '{}' с идентификатором '{}'", type, objectId);
        return object;
    }

    @Override
    public void deleteAll(long userId) {
        if (userId == 0) {
            int quantity = getQuantity();
            objectDao.deleteAll();
            if (getQuantity() != 0) {
                throw new InternalLogicException(String.format("Произошла ощибка при удалении всех объектов '%s'. Если " +
                        "Вы видите это сообщение, пожалуйста, обратитесь к разработчикам", type));
            }
            log.info("Удалены все объекты '{}' из хранилища. Количество объектов, которое было в хранилище " +
                    "до удаления: {}", type, quantity);
        } else {
            checkUserExisting(userId);
            List<T> objects = objectDao.deleteAllEntitiesOfUserById(userId);
            log.info("Удалены все объекты '{}' из хранилища, принадлежащие пользователю с id'{}'. Количество " +
                    "объектов, которое было удалено: {}", type, userId, objects.size());
        }
    }

    @Override
    public T deleteById(long userId, long id) {
        checkUserExisting(userId);
        T object = objectDao.deleteById(id);
        log.info("Удален объект '{}' с идентификатором '{}'", type, id);
        return object;
    }

    protected void checkUserExisting(long userId) {
        if (userId == 0) {
            throw new BadRequestHeaderException("Не указан идентификатор пользователя-хозяина вещи в заголовке " +
                    "Http-запроса");
        }
        try {
            userDao.getById(userId);
        } catch (ObjectNotFoundException exception) {
            throw new ObjectNotFoundException(String.format("Указанный в заголовке Http-запроса идентификатор " +
                    "пользователя-хозяина id'%d' не соответствует ни одному сохраненному пользователю", userId));
        }
    }

}