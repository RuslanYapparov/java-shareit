package ru.practicum.shareit.common;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.InternalLogicException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserShort;

import java.time.LocalDateTime;
import java.util.Optional;

public class CrudServiceTest {
    private static CrudServiceImpl<Foo.Entity, Foo.Domain, Foo.RestCommand, Foo.RestView> crudService;
    private static final JpaRepository<Foo.Entity, Long> entityRepository = Mockito.mock(JpaRepository.class);
    private static final UserRepository userRepository = Mockito.mock(UserRepository.class);
    private static final ObjectMapper<Foo.Entity, Foo.Domain, Foo.RestCommand, Foo.RestView> objectMapper =
            Mockito.mock(ObjectMapper.class);
    private static final DomainObjectValidator<Foo.Domain> domainObjectValidator = Mockito.mock(DomainObjectValidator.class);

    private static final Foo.Domain foo = Foo.DOMAIN;
    private static final Foo.Entity fooEntity = Foo.ENTITY;
    private static final Foo.RestCommand fooRestCommand = Foo.REST_COMMAND;
    private static final Foo.RestView fooRestView = Foo.REST_VIEW;

    @BeforeAll
    public static void setUp() {
        Mockito.when(entityRepository.save(Mockito.any(Foo.Entity.class))).thenReturn(fooEntity);
        Mockito.when(entityRepository.findById(1L)).thenReturn(Optional.of(fooEntity));
        Mockito.when(entityRepository.findById(2L)).thenReturn(Optional.empty());
        Mockito.when(entityRepository.findAll(Mockito.any(Pageable.class))).thenReturn(Page.empty());

        Mockito.when(userRepository.findUserShortById(1L))
                .thenReturn(Optional.of(new UserShort() {
                    @Override
                    public String getName() {
                        return "userName";
                    }

                    @Override
                    public String getEmail() {
                        return "user@email.com";
                    }

                    @Override
                    public LocalDateTime getCreated() {
                        return LocalDateTime.now();
                    }
                }));
        Mockito.when(userRepository.findUserShortById(2L)).thenReturn(Optional.empty());

        Mockito.when(objectMapper.fromRestCommand(Mockito.any(Foo.RestCommand.class))).thenReturn(foo);
        Mockito.when(objectMapper.toRestView(Mockito.any(Foo.Domain.class))).thenReturn(fooRestView);
        Mockito.when(objectMapper.fromDbEntity(Mockito.any(Foo.Entity.class))).thenReturn(foo);
        Mockito.when(objectMapper.toDbEntity(Mockito.any(Foo.Domain.class))).thenReturn(fooEntity);

        Mockito.when(domainObjectValidator.validateAndAssignNullFields(Mockito.any(Foo.Domain.class))).thenReturn(foo);

        crudService = new CrudServiceImpl<>(entityRepository, userRepository, domainObjectValidator, objectMapper);
    }

    @Test
    public void saveAndAllMethods_whenGetIncorrectUserId_thenThrowException() {
        BadRequestHeaderException badRequestHeaderException = assertThrows(BadRequestHeaderException.class,
                () -> crudService.save(0, fooRestCommand));
        assertEquals("Не указан идентификатор пользователя-хозяина вещи в заголовке " +
                "Http-запроса, либо указано значение 0", badRequestHeaderException.getMessage());

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> crudService.save(2, fooRestCommand));
        assertEquals("Указанный в заголовке Http-запроса идентификатор пользователя-хозяина id'2' " +
                "не соответствует ни одному сохраненному пользователю", objectNotFoundException.getMessage());
    }

    @Test
    public void save_whenGetFooRestCommandAndCorrectUserId_thenReturnFooRestView() {
        Foo.RestView savedFoo = crudService.save(1, fooRestCommand);

        Mockito.verify(domainObjectValidator, Mockito.atMost(2))
                .validateAndAssignNullFields(foo);
        Mockito.verify(entityRepository, Mockito.atMost(2)).save(fooEntity);
        assertEquals(fooRestView, savedFoo);
    }

    @Test
    public void getById_whenGetCorrectObjectId_thenReturnFooRestView() {
        Foo.RestView returnedFoo = crudService.getById(1, 1);

        Mockito.verify(entityRepository, Mockito.atMost(2)).findById(1L);
        assertEquals(fooRestView, returnedFoo);
    }

    @Test
    public void getById_whenGetIncorrectObjectId_thenThrowException() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> crudService.getById(1, 2));

        Mockito.verify(entityRepository, Mockito.atMost(2)).findById(2L);
        assertEquals("В ходе выполнения операции над объектом 'Some_type' с " +
                "идентификатором id2 произошла ошибка: объект ранее не был сохранен", exception.getMessage());
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenReturnPageOfObjects() {
        Page<Foo.RestView> fooRestViewsPage = crudService.getAll(1, 0, 10);

        Mockito.verify(entityRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
        assertEquals(Page.empty(), fooRestViewsPage);
    }

    @ParameterizedTest
    @ValueSource(ints = { -1, 0 })
    public void getAll_whenGetIncorrectSizeParameter_thenThrowException(int size) {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> crudService.getAll(1, 0, size));
        assertEquals("Page size must not be less than one", exception.getMessage());
    }

    @Test
    public void getAll_whenGetIncorrectFromParameter_thenThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> crudService.getAll(1, -1, 10));
        assertEquals("Page index must not be less than zero", exception.getMessage());
    }

    @Test
    public void update_whenGetFooRestCommandAndCorrectUserId_thenReturnFooRestView() {
        Foo.RestCommand updatedRestCommand = new Foo.RestCommand(1, "name",
                ShareItConstants.DEFAULT_URI, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        Foo.RestView updatedFoo = crudService.update(1, 1, updatedRestCommand);

        Mockito.verify(domainObjectValidator, Mockito.times(1))
                .validateAndAssignNullFields(foo);
        Mockito.verify(entityRepository, Mockito.atMost(2)).save(fooEntity);
        assertEquals(fooRestView, updatedFoo);
    }

    @Test
    public void deleteAll_whenGetCorrectUserId_thenDeleteAllObjectsOrThrowException() {
        crudService.deleteAll(1);

        Mockito.verify(entityRepository, Mockito.times(1)).deleteAll();


        Mockito.when(entityRepository.count()).thenReturn(1L);

        InternalLogicException exception = assertThrows(InternalLogicException.class, () ->
                crudService.deleteAll(1L));

        Mockito.verify(entityRepository, Mockito.atMost(2)).deleteAll();
        assertEquals("Произошла ощибка при удалении всех объектов 'Some_type'. Если " +
                "Вы видите это сообщение, пожалуйста, обратитесь к разработчикам", exception.getMessage());
    }

    @Test
    public void deleteById_whenGetCorrectUserIdAndObjectId_thenReturnDeletedFooRestView() {
        Foo.RestView deletedFoo = crudService.deleteById(1, 1);

        Mockito.verify(entityRepository, Mockito.times(1)).deleteById(1L);
        assertEquals(fooRestView, deletedFoo);
    }

    @Test
    public void deleteById_whenGetIncorrectObjectId_thenThrowException() {
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class,
                () -> crudService.deleteById(1, 2));

        Mockito.verify(entityRepository, Mockito.atMost(2)).findById(2L);
        assertEquals("В ходе выполнения операции над объектом 'Some_type' с " +
                "идентификатором id2 произошла ошибка: объект ранее не был сохранен", exception.getMessage());
    }

}