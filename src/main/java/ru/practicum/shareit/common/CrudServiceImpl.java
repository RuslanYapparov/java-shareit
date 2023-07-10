package ru.practicum.shareit.common;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.InternalLogicException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserShort;

@Slf4j
@NoArgsConstructor
public class CrudServiceImpl<E extends UpdatableUserDependedEntity, T, C, V>
        implements CrudService<C, V> {
    // E - тип данных объекта слоя репозитроиев (entity)
    // T - тип данных объекта сервисного слоя (domain type)
    // C - тип данных объекта, поступившего из внешней среды (command)
    // V - тип данных объекта, отправляемого во внешнюю среду (view)
    protected JpaRepository<E, Long> entityRepository;
    protected UserRepository userRepository;
    protected DomainObjectValidator<T> domainObjectValidator;
    protected ObjectMapper<E, T, C, V> objectMapper;
    protected String type = "Some_type";

    public CrudServiceImpl(JpaRepository<E, Long> jpaRepository,
    UserRepository userRepository,
    DomainObjectValidator<T> objectValidator,
    ObjectMapper<E, T, C, V> objectMapper) {
        this.entityRepository = jpaRepository;
        this.userRepository = userRepository;
        this.domainObjectValidator = objectValidator;
        this.objectMapper = objectMapper;
    }

    @Override
    public V save(long userId, C commandObject) {       // Переопределяется в сервисе любой сущности, связанной с User
        checkUserExistingAndReturnUserShort(userId);
        T object = objectMapper.fromRestCommand(commandObject);
        object = domainObjectValidator.validateAndAssignNullFields(object);
        E objectEntity = objectMapper.toDbEntity(object);
        objectEntity = entityRepository.save(objectEntity);
        object = objectMapper.fromDbEntity(objectEntity);
        log.info("Пользователь с идентификатором id{} сохранил новый объект типа '{}'. Присвоен идентификатор id{}",
                userId, type, objectEntity.getId());
        return objectMapper.toRestView(object);
    }

    @Override
    public Page<V> getAll(long userId, int from, int size) {            // Переопределяется в сервисе любой сущности,
        checkUserExistingAndReturnUserShort(userId);                                             // Cвязанной с User
        Sort sortByCreatedDate = Sort.by(Sort.Direction.DESC, "created");
        Pageable page = PageRequest.of(from, size, sortByCreatedDate);
        Page<E> entityPage = entityRepository.findAll(page);
        Page<T> objectPage = entityPage.map(objectMapper::fromDbEntity);
        log.info("Запрошен постраничный список всех сохраненных объектов '{}'. Количество объектов для отображения " +
        "на странице - {}, " +
                "объекты отображаются по порядку с {} по {}", type, size, from + 1, objectPage.getTotalElements());
        return objectPage.map(objectMapper::toRestView);
    }

    @Override
    public V getById(long userId, long objectId) {
        E objectEntity = checkUserAndObjectExistingAndReturnEntityFromDb(userId, objectId);
        T object = objectMapper.fromDbEntity(objectEntity);
        log.info("Пользователь с идентификатором id{} запросил данные объекта '{}' с идентификатором id{}",
                userId, type, objectId);
        return objectMapper.toRestView(object);
    }

    @Override
    public V update(long userId, long objectId, C commandObject) {        // Переопределяется в сервисе любой сущности,
        checkUserExistingAndReturnUserShort(userId);                                             // Связанной с User
        T object = objectMapper.fromRestCommand(commandObject);
        object = domainObjectValidator.validateAndAssignNullFields(object);
        E objectEntity = entityRepository.save(objectMapper.toDbEntity(object));
        object = objectMapper.fromDbEntity(objectEntity);
        log.info("Пользователь с идентификатором id{} обновил данные объекта '{}' с идентификатором id{}",
                userId, type, objectId);
        return objectMapper.toRestView(object);
    }

    @Override
    public void deleteAll(long userId) {                // Переопределяется в сервисе любой сущности, связанной с User
        checkUserExistingAndReturnUserShort(userId);
        long quantity = entityRepository.count();
        entityRepository.deleteAll();
        if (entityRepository.count() != 0) {
            throw new InternalLogicException(String.format("Произошла ощибка при удалении всех объектов '%s'. Если " +
                    "Вы видите это сообщение, пожалуйста, обратитесь к разработчикам", type));
        }
        log.info("Удалены все объекты '{}' из хранилища. Количество объектов, которое было в хранилище " +
                "до удаления: {}", type, quantity);
    }

    @Override
    public V deleteById(long userId, long objectId) {
        E objectEntity = checkUserAndObjectExistingAndReturnEntityFromDb(userId, objectId);
        entityRepository.deleteById(objectId);
        T object = objectMapper.fromDbEntity(objectEntity);
        log.info("Удален объект '{}' с идентификатором '{}'", type, objectId);
        return objectMapper.toRestView(object);
    }

    protected UserShort checkUserExistingAndReturnUserShort(long userId) {
        if (userId == 0L) {
            throw new BadRequestHeaderException("Не указан идентификатор пользователя-хозяина вещи в заголовке " +
                    "Http-запроса, либо указано значение 0");
        }
        return userRepository.findUserShortById(userId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("Указанный в заголовке Http-запроса идентификатор " +
                    "пользователя-хозяина id'%d' не соответствует ни одному сохраненному пользователю", userId)));
    }

    protected E checkUserAndObjectExistingAndReturnEntityFromDb(long userId, long objectId) {
        checkUserExistingAndReturnUserShort(userId);
        return entityRepository.findById(objectId).orElseThrow(() ->
                new ObjectNotFoundException(String.format("В ходе выполнения операции над объектом '%s' с " +
                "идентификатором id%d произошла ошибка: объект ранее не был сохранен", type, objectId)));
    }

}