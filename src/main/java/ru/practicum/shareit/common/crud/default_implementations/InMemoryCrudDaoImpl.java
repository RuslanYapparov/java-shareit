package ru.practicum.shareit.common.crud.default_implementations;

import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.crud.CrudDao;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCrudDaoImpl<T> implements CrudDao<T> {
    protected Map<Long, T> dataMap;
    protected long idCounter;
    protected String type;

    public InMemoryCrudDaoImpl() {
        this.dataMap = new HashMap<>();
        this.type = "Some_type";
    }

    @Override
    public T save(T object) {
        long objectId = ++idCounter;
        if (dataMap.containsValue(object)) {
            throw new ObjectAlreadyExistsException(String.format("Объект '%s' уже был сохранен ранее", type));
        }
        dataMap.put(objectId, object);
        return dataMap.get(objectId);
    }

    @Override
    public List<T> getAll() {
        return List.copyOf(dataMap.values());
    }

    @Override
    public T getById(long id) {
        if (!dataMap.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Объект '%s' с идентификатором '%d' не найден " +
                    "в базе данных приложения", type, id));
        }
        return dataMap.get(id);
    }

    @Override
    public int getQuantity() {
        return dataMap.size();
    }

    @Override
    public T update(long objectId, T object) {
        if (!dataMap.containsKey(objectId)) {
            throw new ObjectNotFoundException(String.format("Обновление объекта '%s' с идентификатором '%d' " +
                    "невозможно, так как он не был сохранен в базе данных приложения", type, objectId));
        }
        dataMap.put(objectId, object);
        return dataMap.get(objectId);
    }

    @Override
    public void deleteAll() {
        dataMap.clear();
    }

    public T deleteById(long id) {
        if (!dataMap.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Удаление объекта '%s' с идентификатором '%d' " +
                    "невозможно, так как он не был сохранен в базе данных приложения", type, id));
        }
        return dataMap.remove(id);
    }

    @Override
    public List<T> getAllEntitiesOfUserById(long userId) {                   // Метод нужно будет переопределить для
        return null;                                                                 // Всех Dao-классов сущностей
    }

    @Override
    public List<T> deleteAllEntitiesOfUserById(long userId) {
        List<T> entities = getAllEntitiesOfUserById(userId);
        entities.forEach(dataMap::remove);
        return entities;
    }

}