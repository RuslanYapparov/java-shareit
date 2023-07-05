package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingRestView save(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                                @RequestBody @Valid BookingRestCommand bookingRestCommand) {
        return bookingService.save(userId, bookingRestCommand);
    }

    @PatchMapping("{booking_id}")
    public BookingRestView changeStatus(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                                        @PathVariable(name = "booking_id") @Positive long bookingId,
                                        @RequestParam(name = "approved") boolean isApproved) {
        return bookingService.changeBookingStatus(userId, bookingId, isApproved);
    }

    @GetMapping
    public List<BookingRestView> getAllWithStateForBooker(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                                                 @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllForBookerWithStateParameter(userId, state);
    }

    @GetMapping("{booking_id}")
    public BookingRestView getById(@RequestHeader(value = "X-Sharer-User-Id") @Positive long userId,
                                   @PathVariable(name = "booking_id") @Positive long bookingId) {
        return bookingService.getById(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<BookingRestView> getAllWithStateForItemOwner(
            @RequestHeader(value = "X-Sharer-User-Id") @Positive long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") String state) {
        return bookingService.getAllForItemOwnerWithStateParameter(ownerId, state);
    }


}