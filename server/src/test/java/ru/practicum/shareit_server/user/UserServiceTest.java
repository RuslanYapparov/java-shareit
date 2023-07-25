package ru.practicum.shareit_server.user;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.exception.ObjectAlreadyExistsException;
import ru.practicum.shareit_server.exception.ObjectNotFoundException;
import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class UserServiceTest {
    private final UserService userService;

    private UserRestView firstUser;
    private UserRestView secondUser;

    @BeforeEach
    public void prepareDbForTest() {
        UserRestCommand firstUserRestCommand = UserRestCommand.builder()
                .name("Валера")
                .email("valera@ru.practicum.shareit.user.ru")
                .build();
        UserRestCommand secondUserRestCommand = UserRestCommand.builder()
                .name("Илюша")
                .email("iliusha@ru.practicum.shareit.user.ru")
                .build();
        firstUser = userService.save(firstUserRestCommand);
        secondUser = userService.save(secondUserRestCommand);

    }

    @Test
    public void saveAndGetById_whenGetCorrectData_thenReturnCorrectUserRestView() {
        UserRestView userRestView = userService.getById(firstUser.getId());
        assertThat(userRestView, notNullValue());
        assertThat(userRestView.getId(), equalTo(firstUser.getId()));
        assertThat(userRestView.getName(), equalTo(firstUser.getName()));
        assertThat(userRestView.getEmail(), equalTo(firstUser.getEmail()));
        assertThat(userRestView.getAvatarUri(), equalTo(ShareItConstants.DEFAULT_URI));
        assertThat(userRestView.getTelephoneNumber(), equalTo(ShareItConstants.NOT_ASSIGNED));
        assertThat(userRestView.getAddress(), equalTo(firstUser.getAddress()));
        assertThat(userRestView.getCreated(), equalTo(firstUser.getCreated()));
        assertThat(userRestView.getLastModified(), equalTo(firstUser.getLastModified()));
        assertThat(userRestView.getUserRating(), equalTo(firstUser.getUserRating()));

        long notFoundId = secondUser.getId() + 1;
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.getById(notFoundId));
        assertThat(exception.getMessage(), equalTo(String.format("Пользователь с идентификатором id'%d' " +
                "не был сохранен", notFoundId)));
    }

    @ParameterizedTest
    @ValueSource(strings = {"valera@ru.practicum.shareit.user.ru", "iliusha@ru.practicum.shareit.user.ru"})
    public void save_whenGetDuplicateEmail_thenThrowException(String email) {
        UserRestCommand userRestCommand = UserRestCommand.builder()
                .name("Gosha")
                .email(email)
                .build();
        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class, () ->
                userService.save(userRestCommand));
        assertThat(exception.getMessage(), equalTo(String.format("Пользователь с адресом электронной почты '%s' " +
                "уже сохранен", email)));
    }

    @Test
    public void getAll_whenCalled_thenReturnListOfAllUsers() {
        List<UserRestView> users = userService.getAll();
        assertThat(users, notNullValue());
        assertThat(users.size(), equalTo(2));
        assertThat(users.get(0), instanceOf(UserRestView.class));

        UserRestView firstUserRestView = users.get(0);
        UserRestView secondUserRestView = users.get(1);
        assertThat(firstUserRestView, equalTo(firstUser));
        assertThat(secondUserRestView, equalTo(secondUser));
    }

    @Test
    public void update_whenGetRestCommandWithOneField_thenReturnUpdatedUserRestView() {
        UserRestCommand userRestCommand = UserRestCommand.builder()
                .name("Gosha")
                .build();
        LocalDateTime created = firstUser.getCreated();
        LocalDateTime lastModified = firstUser.getLastModified();
        userService.update(firstUser.getId(), userRestCommand);
        firstUser = userService.getById(firstUser.getId());
        assertThat(firstUser.getName(), equalTo("Gosha"));
        assertThat(firstUser.getEmail(), equalTo("valera@ru.practicum.shareit.user.ru"));
        assertThat(firstUser.getCreated(), equalTo(created));
        assertThat(firstUser.getLastModified(), not(lastModified));

        userRestCommand = UserRestCommand.builder()
                .email("valera@ru.practicum.shareit.user.ru")
                .build();
        lastModified = firstUser.getLastModified();
        userService.update(firstUser.getId(), userRestCommand);
        firstUser = userService.getById(firstUser.getId());
        assertThat(firstUser.getName(), equalTo("Gosha"));
        assertThat(firstUser.getEmail(), equalTo("valera@ru.practicum.shareit.user.ru"));
        assertThat(firstUser.getCreated(), equalTo(created));
        assertThat(firstUser.getLastModified(), not(equalTo(lastModified)));

        userRestCommand = UserRestCommand.builder()
                .email("gosha@ru.practicum.shareit.user.ru")
                .build();
        lastModified = firstUser.getLastModified();
        userService.update(firstUser.getId(), userRestCommand);
        firstUser = userService.getById(firstUser.getId());
        assertThat(firstUser.getName(), equalTo("Gosha"));
        assertThat(firstUser.getEmail(), equalTo("gosha@ru.practicum.shareit.user.ru"));
        assertThat(firstUser.getCreated(), equalTo(created));
        assertThat(firstUser.getLastModified(), not(lastModified));
    }

    @Test
    public void update_whenGetDuplicateEmailWithOtherUser_thenThrowException() {
        UserRestCommand userRestCommand = UserRestCommand.builder()
                .email("iliusha@ru.practicum.shareit.user.ru")
                .build();
        ObjectAlreadyExistsException exception = assertThrows(ObjectAlreadyExistsException.class, () ->
                userService.update(firstUser.getId(), userRestCommand));
        assertThat(exception.getMessage(), equalTo("Пользователь с адресом электронной почты " +
                "'iliusha@ru.practicum.shareit.user.ru' уже сохранен"));
    }

    @Test
    public void update_whenGetIncorrectId_thenThrowException() {
        long notFoundId = secondUser.getId() + 1;
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.update(notFoundId, UserRestCommand.builder().build()));
        assertThat(exception.getMessage(), equalTo(String.format("Пользователь с идентификатором id'%d' " +
                "не был сохранен", notFoundId)));
    }

    @Test
    public void deleteAll_whenCalled_deleteAllUsers() {
        userService.deleteAll();
        List<UserRestView> users = userService.getAll();
        assertThat(users, emptyIterable());
    }

    @Test
    public void deleteById_whenGetCorrectParameters_thenDeleteUser() {
        UserRestView deletedUser = userService.deleteById(firstUser.getId());
        assertThat(deletedUser, equalTo(firstUser));
        List<UserRestView> allUsers = userService.getAll();
        assertThat(allUsers, iterableWithSize(1));
        assertThat(allUsers.get(0), equalTo(secondUser));
    }

    @Test
    public void deleteById_whenGetIncorrectParameters_thenThrowException() {
        long notFoundId = secondUser.getId() + 1;
        ObjectNotFoundException exception = assertThrows(ObjectNotFoundException.class, () ->
                userService.deleteById(notFoundId));
        assertThat(exception.getMessage(), equalTo(String.format("Пользователь с идентификатором id'%d' " +
                "не был сохранен", notFoundId)));
    }

}