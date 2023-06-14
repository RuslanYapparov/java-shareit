package ru.practicum.shareit.common.default_implementations;

import ru.practicum.shareit.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.common.CrudDao;
import ru.practicum.shareit.common.IdentificableObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class InMemoryCrudDaoImpl<T extends IdentificableObject> implements CrudDao<T> {
    protected Map<Long, T> dataMap;
    protected long idCounter;
    protected String type;

    public InMemoryCrudDaoImpl() {
        this.dataMap = new HashMap<>();
        this.type = "Some_type";
    }

    @Override
    public T save(T object) throws ObjectAlreadyExistsException {
        long objectId = ++idCounter;
        object.setId(objectId);
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
    public T getById(long id) throws ObjectNotFoundException {
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
    public T update(long objectId, T object) throws ObjectNotFoundException {
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

    public T deleteById(long id) throws ObjectNotFoundException {
        if (!dataMap.containsKey(id)) {
            throw new ObjectNotFoundException(String.format("Удаление объекта '%s' с идентификатором '%d' " +
                    "невозможно, так как он не был сохранен в базе данных приложения", type, id));
        }
        return dataMap.remove(id);
    }

}
