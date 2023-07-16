package ru.practicum.shareit.booking;

import org.junit.jupiter.api.Test;

import ru.practicum.shareit.booking.dto.BookedItem;
import ru.practicum.shareit.booking.dto.Booker;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.item.dao.ItemEntity;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperTest {
    private final BookingMapper bookingMapper = new BookingMapperImpl();

    @Test
    public void fromRestCommand_whenGetRestCommand_thenReturnBookingFromRestCommand() {
        BookingRestCommand bookingRestCommand = initializeNewBookingRestCommand();
        assertNotNull(bookingMapper.fromRestCommand(bookingRestCommand));
        assertNotEquals(Booking.builder().build(), bookingMapper.fromRestCommand(bookingRestCommand));
        assertEquals(Booking.builder().build(), bookingMapper.fromRestCommand(BookingRestCommand.builder().build()));
        assertEquals(initializeNewBookingFromRestCommand(), bookingMapper.fromRestCommand(bookingRestCommand));
    }

    @Test
    public void toRestView_whenGetBooking_thenReturnBookingRestView() {
        Booking booking = initializeNewBooking();
        BookingRestView bookingRestView = initializeNewBookingRestView();
        assertNotNull(bookingMapper.toRestView(booking));
        assertEquals(bookingRestView, bookingMapper.toRestView(booking));
    }

    @Test
    public void fromDbEntity_whenGetBookingEntity_thenReturnBooking() {
        BookingEntity bookingEntity = initializeNewBookingEntity();
        Booking booking = initializeNewBooking();
        assertNotNull(bookingMapper.fromDbEntity(bookingEntity));
        assertNotEquals(Booking.builder().build(), bookingMapper.fromDbEntity(bookingEntity));
        assertEquals(booking, bookingMapper.fromDbEntity(bookingEntity));
    }

    @Test
    public void toDbEntity_whenGetBooking_thenReturnBookingEntity() {
        BookingEntity bookingEntity = initializeNewBookingEntity();
        Booking booking = initializeNewBooking();
        assertNotNull(bookingMapper.toDbEntity(booking));
        assertNotEquals(new BookingEntity(), bookingMapper.toDbEntity(booking));
        assertEquals(bookingEntity, bookingMapper.toDbEntity(booking));
    }

    private BookingRestCommand initializeNewBookingRestCommand() {
        return BookingRestCommand.builder()
                .itemId(1L)
                .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
    }

    private BookingRestView initializeNewBookingRestView() {
        return BookingRestView.builder()
                .id(1L)
                .booker(new Booker(1L))
                .item(new BookedItem(1L, "item_name"))
                .status("APPROVED")
                .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
    }

    private Booking initializeNewBooking() {
        return Booking.builder()
                .id(1L)
                .bookerId(1L)
                .itemId(1L)
                .itemOwnerId(1L)
                .itemName("item_name")
                .bookingStatus(BookingStatus.APPROVED)
                .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
    }

    private Booking initializeNewBookingFromRestCommand() {
        return Booking.builder()
                .itemId(1L)
                .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
    }

    private BookingEntity initializeNewBookingEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setId(1L);
        itemEntity.setUserId(1L);
        itemEntity.setName("item_name");

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setId(1);
        bookingEntity.setUserId(1);
        bookingEntity.setStatus("APPROVED");
        bookingEntity.setItem(itemEntity);
        bookingEntity.setStart(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        bookingEntity.setEnd(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        bookingEntity.setCreated(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        bookingEntity.setLastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        return bookingEntity;
    }

}