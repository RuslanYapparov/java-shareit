package ru.practicum.shareit.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.user.dao.UserRepository;

@Slf4j
public class TestCrudServiceImpl extends CrudServiceImpl<Foo.Entity, Foo.Domain, Foo.RestCommand, Foo.RestView> {

    @Override
    public Foo.RestView save(long userId, Foo.RestCommand commandObject) {
        MethodParameterValidator.checkUserIdForNullValue(userId, "сохранение");
        checkExistingAndReturnUserShort(userId);
        Foo.Domain object = objectMapper.fromRestCommand(commandObject);
        object = domainObjectValidator.validateAndAssignNullFields(object);
        Foo.Entity objectEntity = objectMapper.toDbEntity(object);
        objectEntity = entityRepository.save(objectEntity);
        object = objectMapper.fromDbEntity(objectEntity);
        log.info("Пользователь с идентификатором id{} сохранил новый объект типа '{}'. Присвоен идентификатор id{}",
                userId, type, objectEntity.getId());
        return objectMapper.toRestView(object);
    }

    public TestCrudServiceImpl(JpaRepository<Foo.Entity, Long> repository,
                               UserRepository userRepository,
                               DomainObjectValidator<Foo.Domain> validator,
                               ObjectMapper<Foo.Entity, Foo.Domain, Foo.RestCommand, Foo.RestView> objectMapper) {
        super(repository, userRepository, validator, objectMapper);
    }

}
