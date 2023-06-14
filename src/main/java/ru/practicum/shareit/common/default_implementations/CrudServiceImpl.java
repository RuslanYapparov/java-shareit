package ru.practicum.shareit.common.default_implementations;

import lombok.extern.slf4j.Slf4j;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.InternalLogicException;
import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.CrudDao;
import ru.practicum.shareit.common.CrudService;
import ru.practicum.shareit.common.IdentificableObject;
import ru.practicum.shareit.user.dao.UserDao;

import java.util.List;

@Slf4j
public class CrudServiceImpl<T extends IdentificableObject> implements CrudService<T> {
    protected CrudDao<T> objectDao;
    protected UserDao userDao;
    protected String type = "Some_type";

    @Override
    public T save(T object) throws ObjectAlreadyExistsException {
        object = objectDao.save(object);
        log.info(String.format("Сохранен новый объект '%s'. Присвоен идентификатор '%d'.", type, object.getId()));
        return object;
    }

    @Override
    public List<T> getAll() {
        List<T> objectList = objectDao.getAll();
        log.info(String.format("Запрошен список всех сохраненных объектов '%s'. Количество сохраненных объектов - %d",
                type, this.getQuantity()));
        return objectList;
    }

    @Override
    public T getById(long id) throws ObjectNotFoundException {
        T object = objectDao.getById(id);
        log.info(String.format("Запрошен объект '%s' с идентификатором '%d'", type, id));
        return object;
    }

    @Override
    public int getQuantity() {
        return objectDao.getQuantity();
    }

    @Override
    public T update(long objectId, T object) throws ObjectNotFoundException {
        object = objectDao.update(objectId, object);
        log.info(String.format("Обновлены данные объекта '%s' с идентификатором '%d'.", type, objectId));
        return object;
    }

    @Override
    public void deleteAll() throws InternalLogicException {
        int quantity = this.getQuantity();
        objectDao.deleteAll();
        if (this.getQuantity() != 0) {
            throw new InternalLogicException("Произошла ощибка при удалении всех объектов '%s'. Если Вы видите это " +
                    "сообщение, пожалуйста, обратитесь к разработчикам");
        }
        log.info(String.format("Удалены все объекты '%s' из хранилища. Количество объектов, которое было " +
                "в хранилище до удаления: %d", type, quantity));
    }

    @Override
    public T deleteById(long id) throws ObjectNotFoundException {
        T object = objectDao.deleteById(id);
        log.info(String.format("Удален объект '%s' с идентификатором '%d'", type, id));
        return object;
    }

    @Override
    public void checkUserExisting(long userId) throws ObjectNotFoundException, BadRequestHeaderException {
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