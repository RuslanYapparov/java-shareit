package ru.practicum.shareit.booking;

import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.common.CrudService;

import java.util.List;

public interface BookingService extends CrudService<BookingRestCommand, BookingRestView> {

    BookingRestView changeBookingStatus(long userId, long bookingId, boolean isApproved);

    List<BookingRestView> getAllForBookerWithStateParameter(long userId, String state);

    List<BookingRestView> getAllForItemOwnerWithStateParameter(long userId, String state);

}