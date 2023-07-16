package ru.practicum.shareit.booking;

import org.springframework.data.domain.Page;

import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.common.CrudService;

public interface BookingService extends CrudService<BookingRestCommand, BookingRestView> {

    BookingRestView changeBookingStatus(long userId, long bookingId, boolean isApproved);

    Page<BookingRestView> getAllForBookerWithStateParameter(long userId, String state, int from, int size);

    Page<BookingRestView> getAllForItemOwnerWithStateParameter(long userId, String state, int from, int size);

}