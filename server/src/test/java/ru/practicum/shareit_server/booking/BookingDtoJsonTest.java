package ru.practicum.shareit_server.booking;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit_server.booking.dto.BookedItem;
import ru.practicum.shareit_server.booking.dto.Booker;
import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.booking.dto.BookingRestCommand;
import ru.practicum.shareit_server.booking.dto.BookingRestView;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

@JsonTest
public class BookingDtoJsonTest {
    @Autowired
    private JacksonTester<BookingRestCommand> commandJsonTester;
    @Autowired
    private JacksonTester<BookingRestView> viewJsonTester;

    @Test
    public void shouldReturnCorrectBookingCommand() throws IOException {
        String bookingRestCommandJson = "{\"itemId\":\"1\",\"start\":\"2023-07-07T07:07:07\"," +
                "\"end\":null}";

        BookingRestCommand bookingRestCommand = commandJsonTester.parseObject(bookingRestCommandJson);

        assertThat(bookingRestCommand).isNotNull();
        assertEquals(1L, bookingRestCommand.getItemId());
        Assertions.assertEquals(ShareItConstants.DEFAULT_LOCAL_DATE_TIME, bookingRestCommand.getStart());
        assertNull(bookingRestCommand.getEnd());
    }

    @Test
    public void shouldReturnCorrectBookingRestViewJson() throws IOException {
        BookingRestView bookingRestView = BookingRestView.builder()
                .id(1L)
                .booker(new Booker(1L))
                .item(new BookedItem(1L, "item_name"))
                .status("APPROVED")
                .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();

        JsonContent<BookingRestView> bookingJson = viewJsonTester.write(bookingRestView);

        assertThat(bookingJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(bookingJson).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(bookingJson).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(bookingJson).extractingJsonPathStringValue("$.item.name")
                .isEqualTo("item_name");
        assertThat(bookingJson).extractingJsonPathStringValue("$.status")
                .isEqualTo("APPROVED");
        assertThat(bookingJson).extractingJsonPathStringValue("$.start")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(bookingJson).extractingJsonPathStringValue("$.end")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(bookingJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(bookingJson).extractingJsonPathStringValue("$.lastModified")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
    }

}