package ru.practicum.shareit.booking;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.common.ObjectMapper;

@Mapper(componentModel = "spring")
public interface BookingMapper extends ObjectMapper<BookingEntity, Booking, BookingRestCommand, BookingRestView> {

    @Override
    @Mapping(target = "booker", expression = "java(new ru.practicum.shareit.booking.dto." +
            "Booker(booking.getBookerId()))")
    @Mapping(target = "item", expression = "java(new ru.practicum.shareit.booking.dto." +
            "BookedItem(booking.getItemId(), booking.getItemName()))")
    @Mapping(target = "status", expression = "java(booking.getBookingStatus().name())")
    BookingRestView toRestView(Booking booking);

    @Override
    @Mapping(target = "bookerId", expression = "java(bookingEntity.getUserId())")
    @Mapping(target = "itemId", expression = "java(bookingEntity.getItem().getId())")
    @Mapping(target = "itemOwnerId", expression = "java(bookingEntity.getItem().getUserId())")
    @Mapping(target = "itemName", expression = "java(bookingEntity.getItem().getName())")
    @Mapping(target = "bookingStatus", expression = "java(BookingStatus.valueOf(bookingEntity.getStatus()))")
    Booking fromDbEntity(BookingEntity bookingEntity);

    @Override
    @Mapping(target = "status", expression = "java(booking.getBookingStatus().name())")
    @Mapping(target = "userId", expression = "java(booking.getBookerId())")
    BookingEntity toDbEntity(Booking booking);

}