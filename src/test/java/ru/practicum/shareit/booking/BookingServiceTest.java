package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.exception.BadRequestParameterException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.item.ItemService;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class BookingServiceTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;

    private UserRestView firstUser;
    private UserRestView secondUser;
    private ItemRestView item;
    private BookingRestView booking;

    @BeforeEach
    public void prepareDbForTest() {
        UserRestCommand firstUserRestCommand = UserRestCommand.builder()
                .name("Валера")
                .email("valera@user.ru")
                .build();
        UserRestCommand secondUserRestCommand = UserRestCommand.builder()
                .name("Илюша")
                .email("iliusha@user.ru")
                .build();
        firstUser = userService.save(firstUserRestCommand);
        secondUser = userService.save(secondUserRestCommand);

        ItemRestCommand itemRestCommand = ItemRestCommand.builder()
                .name("Мотыга")
                .description("Тупая тяжелая мотыга для проблемной работы")
                .available(true)
                .build();
        item = itemService.save(firstUser.getId(), itemRestCommand);

        BookingRestCommand bookingRestCommand = BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build();
        booking = bookingService.save(secondUser.getId(), bookingRestCommand);
    }

    @Test
    public void saveFromBeforeEachAndGetById_whenGetCorrectData_thenReturnCorrectRequestRestView() {
        BookingRestView bookingRestView = bookingService.getById(secondUser.getId(), booking.getId());
        assertThat(bookingRestView, notNullValue());
        assertThat(bookingRestView.getId(), equalTo(booking.getId()));
        assertThat(bookingRestView.getBooker().getId(), equalTo(secondUser.getId()));
        assertThat(bookingRestView.getStatus(), equalTo("WAITING"));
        assertThat(bookingRestView.getStart(), equalTo(booking.getStart()));
        assertThat(bookingRestView.getEnd(), equalTo(booking.getEnd()));
        assertThat(bookingRestView.getItem().getId(), equalTo(item.getId()));
        assertThat(bookingRestView.getCreated(), equalTo(booking.getCreated()));
        assertThat(bookingRestView.getLastModified(), equalTo(booking.getLastModified()));
    }

    @Test
    public void save_whenGetIncorrectUserOrItemParameters_thenThrowException() {
        long notFoundUserId = secondUser.getId() + 1;
        BookingRestCommand bookingRestCommand = BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.save(notFoundUserId, bookingRestCommand));
        assertThat(objectNotFoundException.getMessage(), equalTo(String.format("Указанный в заголовке Http-запроса идентификатор " +
                "пользователя-хозяина id'%d' не соответствует ни одному сохраненному пользователю", notFoundUserId)));

        objectNotFoundException = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.save(firstUser.getId(), bookingRestCommand));
        assertThat(objectNotFoundException.getMessage(), equalTo(String.format("При бронировании вещи с идентификатором id%d произошла " +
                "ошибка: в заголовке запроса передан идентификатор пользователя id%d, являющегося " +
                "владельцем вещи", item.getId(), firstUser.getId())));

        long notFoundItemId = item.getId() + 1;
        BookingRestCommand anotherBookingRestCommand = BookingRestCommand.builder()
                .itemId(notFoundItemId)
                .start(LocalDateTime.now().plusDays(3))
                .end(LocalDateTime.now().plusDays(4))
                .build();
        objectNotFoundException = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.save(secondUser.getId(), anotherBookingRestCommand));
        assertThat(objectNotFoundException.getMessage(), equalTo(String.format("При бронировании вещи " +
                "с идентификатором id%d произошла ошибка: указан неверный идентификатор, вещь с этим идентификатором " +
                "не была сохранена", notFoundItemId)));

        ItemRestCommand itemRestCommand = ItemRestCommand.builder()
                .available(false)
                .build();
        item = itemService.update(firstUser.getId(), item.getId(), itemRestCommand);
        BadRequestBodyException badRequestBodyException = assertThrows(BadRequestBodyException.class, () ->
                bookingService.save(secondUser.getId(), bookingRestCommand));
        assertThat(badRequestBodyException.getMessage(), equalTo(String.format("При бронировании вещи с идентификатором id%d произошла " +
                "ошибка: данная вещь не доступна для бронирования", item.getId())));
    }

    @Test
    public void save_whenGetIncorrectDateTimeParameters_thenThrowException() {
        BookingRestCommand incorrectBookingRestCommand = BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusSeconds(1))
                .end(LocalDateTime.now())
                .build();
        BadRequestBodyException badRequestBodyException = assertThrows(BadRequestBodyException.class, () ->
                bookingService.save(secondUser.getId(), incorrectBookingRestCommand));
        assertThat(badRequestBodyException.getMessage(), equalTo(String.format("При бронировании вещи с " +
                        "идентификатором id%d произошла ошибка: переданная дата конца бронирования '%s' раньше " +
                        "даты начала '%s' или равна ей", item.getId(), incorrectBookingRestCommand.getEnd(),
                incorrectBookingRestCommand.getStart())));

        bookingService.changeBookingStatus(firstUser.getId(), booking.getId(), true);
        BookingRestCommand busiedBookingRestCommand = BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusHours(37))
                .end(LocalDateTime.now().plusHours(77))
                .build();
        badRequestBodyException = assertThrows(BadRequestBodyException.class, () ->
                bookingService.save(secondUser.getId(), busiedBookingRestCommand));
        assertThat(badRequestBodyException.getMessage(), equalTo(String.format("При бронировании вещи с " +
                "идентификатором id%d произошла ошибка: данная вещь уже была забронирована на этот период",
                item.getId())));
    }

    @Test
    public void changeBookingStatus_whenGetCorrectParameters_thenReturnCorrectBookingRestViewOrThrowExceptionIfTwice() {
        bookingService.changeBookingStatus(firstUser.getId(), booking.getId(), true);
        booking = bookingService.getById(secondUser.getId(), booking.getId());
        assertThat(booking.getStatus(), equalTo("APPROVED"));

        BadRequestParameterException badRequestParameterException = assertThrows(BadRequestParameterException.class,
                () -> bookingService.changeBookingStatus(firstUser.getId(), booking.getId(), true));
        assertThat(badRequestParameterException.getMessage(), equalTo(String.format("Пользователь с id%d уже " +
                        "устанавливал статус %s бронированию с id%d для своей вещи %s", firstUser.getId(), "APPROVED",
                booking.getId(), booking.getItem().getName())));
    }

    @Test
    public void changeBookingStatus_whenGetIncorrectParameters_thenThrowsException() {
        UserRestCommand anotherUserRestCommand = UserRestCommand.builder()
                .name("Вазгенчик")
                .email("vazgenchick@user.ru")
                .build();
        UserRestView anotherUser = userService.save(anotherUserRestCommand);

        ObjectNotFoundException objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.changeBookingStatus(anotherUser.getId(), booking.getId(), true));
        assertThat(objectNotFoundException.getMessage(), equalTo(String.format("В ходе выполнения операции над " +
                "объектом 'booking' с идентификатором id%d произошла ошибка: пользователь с id%d не является " +
                "владельцем вещи и не бронировал ее у другого пользователя", booking.getId(), anotherUser.getId())));

        long notFoundBookingId = booking.getId() + 1;
        objectNotFoundException = assertThrows(ObjectNotFoundException.class,
                () -> bookingService.changeBookingStatus(firstUser.getId(), notFoundBookingId, true));
        assertThat(objectNotFoundException.getMessage(), equalTo(String.format("В ходе выполнения операции над " +
                "объектом 'booking' с идентификатором id%d произошла ошибка: " +
                "объект ранее не был сохранен", notFoundBookingId)));

        objectNotFoundException = assertThrows(ObjectNotFoundException.class, () ->
                bookingService.changeBookingStatus(secondUser.getId(), booking.getId(), true));
        assertThat(objectNotFoundException.getMessage(), equalTo(String.format("Операция изменения статуса " +
                "бронирования с идентификатором id'%d' отклонена: " +
                "только владелец вещи может изменять статус", booking.getId())));
    }

}