package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestParameterException;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingRestView save(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @RequestBody @Valid BookingRestCommand bookingRestCommand) {
        checkUserIdForNullValue(userId, "сохранение");
        return bookingService.save(userId, bookingRestCommand);
    }

    @PatchMapping("{booking_id}")
    public BookingRestView changeStatus(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "booking_id") @Positive long bookingId,
            @RequestParam(name = "approved", defaultValue = "null") Boolean isApproved) {
        checkUserIdForNullValue(userId, "изменение статуса");
        if (isApproved == null) {
            throw new BadRequestParameterException("В запросе на изменение статуса бронирования не указано " +
                    "подтверждено оно или нет");
        }
        return bookingService.changeBookingStatus(userId, bookingId, isApproved);
    }

    @GetMapping
    public List<BookingRestView> getAllWithStateForBooker(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        checkUserIdForNullValue(userId, "получение всех бронирований, оформленных пользователем");
        checkPaginationParameters(from, size);
        return bookingService.getAllForBookerWithStateParameter(userId, state, from, size).toList();
    }

    @GetMapping("/owner")
    public List<BookingRestView> getAllWithStateForItemOwner(
            @RequestHeader(value = "X-Sharer-User-Id") @PositiveOrZero long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        checkUserIdForNullValue(ownerId, "получение всех бронирований хозяином вещи");
        checkPaginationParameters(from, size);
        return bookingService.getAllForItemOwnerWithStateParameter(ownerId, state, from, size).toList();
    }

    @GetMapping("{booking_id}")
    public BookingRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "booking_id") @Positive long bookingId) {
        checkUserIdForNullValue(userId, "получение по идентификатору");
        return bookingService.getById(userId, bookingId);
    }



    private void checkUserIdForNullValue(long userId, String operation) {
        if (userId == 0L) {
            throw new BadRequestHeaderException(String.format("В заголовке запроса на проведение операции '%s' " +
                    "с данными объекта 'бронирование' не передан идентификатор пользователя, либо указан 0", operation));
        }
    }

    private void checkPaginationParameters(int from, int size) {
        if (from < 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение порядкового номера " +
                    "первого отображаемого элемента '" + from + "'. Значение данного параметра не должно быть меньше 0");
        }
        if (size <= 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение количества " +
                    "отображаемых элементов, равное '" + size + "'. Значение данного параметра не должно быть меньше, чем 1");
        }
    }

}