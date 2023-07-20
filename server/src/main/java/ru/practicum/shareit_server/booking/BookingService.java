package ru.practicum.shareit_server.booking;

import org.springframework.data.domain.Page;

import ru.practicum.shareit_server.booking.dto.BookingRestCommand;
import ru.practicum.shareit_server.booking.dto.BookingRestView;
import ru.practicum.shareit_server.common.CrudService;

public interface BookingService extends CrudService<BookingRestCommand, BookingRestView> {

    BookingRestView changeBookingStatus(long userId, long bookingId, boolean isApproved);

    Page<BookingRestView> getAllForBookerWithStateParameter(long userId, String state, int from, int size);

    Page<BookingRestView> getAllForItemOwnerWithStateParameter(long userId, String state, int from, int size);

}