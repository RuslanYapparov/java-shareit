package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
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
        return bookingService.save(userId, bookingRestCommand);
    }

    @PatchMapping("{booking_id}")
    public BookingRestView changeStatus(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "booking_id") @Positive long bookingId,
            @RequestParam(name = "approved", defaultValue = "null") Boolean isApproved) {
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
        return bookingService.getAllForBookerWithStateParameter(userId, state, from, size).toList();
    }

    @GetMapping("/owner")
    public List<BookingRestView> getAllWithStateForItemOwner(
            @RequestHeader(value = "X-Sharer-User-Id") @PositiveOrZero long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return bookingService.getAllForItemOwnerWithStateParameter(ownerId, state, from, size).toList();
    }

    @GetMapping("{booking_id}")
    public BookingRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "booking_id") @Positive long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

}