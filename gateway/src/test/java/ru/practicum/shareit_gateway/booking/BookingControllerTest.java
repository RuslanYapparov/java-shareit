package ru.practicum.shareit_gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_gateway.booking.dto.*;
import ru.practicum.shareit_gateway.common.ShareItConstants;
import ru.practicum.shareit_gateway.exception.BadRequestParameterException;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
public class BookingControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    BookingClient bookingClient;
    @Autowired
    private MockMvc mvc;

    private final BookingRestView booking = BookingRestView.builder()
            .id(1L)
            .booker(new Booker(1L))
            .item(new BookedItem(1L, "item_name"))
            .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .status("WAITING")
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(bookingClient.save(Mockito.anyLong(), Mockito.any(BookingRestCommand.class)))
                .thenReturn(ResponseEntity.ok(booking));

        mvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(BookingRestCommand.builder()
                                .itemId(1L)
                                .start(LocalDateTime.now().plusDays(1))
                                .end(LocalDateTime.now().plusDays(2))
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.created", is(booking.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(booking.getLastModified().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus())));

        verify(bookingClient, Mockito.times(1))
                .save(Mockito.anyLong(), Mockito.any(BookingRestCommand.class));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(bookingClient.getById(1L, 1L))
                .thenReturn(ResponseEntity.ok(booking));

        mvc.perform(get("/bookings/{booking_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.created", is(booking.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(booking.getLastModified().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus())));
        verify(bookingClient, Mockito.times(1))
                .getById(1L, 1L);
    }

    @Test
    public void changeStatus_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(bookingClient.changeStatus(1L, 1, true))
                .thenReturn(ResponseEntity.ok(booking));

        mvc.perform(patch("/bookings/{booking_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .param("approved", "true")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(booking.getId()), Long.class))
                .andExpect(jsonPath("$.booker.id", is(booking.getBooker().getId()), Long.class))
                .andExpect(jsonPath("$.item.id", is(booking.getItem().getId()), Long.class))
                .andExpect(jsonPath("$.item.name", is(booking.getItem().getName())))
                .andExpect(jsonPath("$.start", is(booking.getStart().toString())))
                .andExpect(jsonPath("$.end", is(booking.getEnd().toString())))
                .andExpect(jsonPath("$.created", is(booking.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(booking.getLastModified().toString())))
                .andExpect(jsonPath("$.status", is(booking.getStatus())));

        verify(bookingClient, Mockito.times(1))
                .changeStatus(1L, 1, true);
    }

    @Test
    public void changeStatus_whenGetNullApproved_thenThrowBadRequestParameterException() throws Exception {
        when(bookingClient.changeStatus(1L, 1L, false))
                .thenThrow(new BadRequestParameterException("Это исключение не должно быть выброшено"));

        mvc.perform(patch("/bookings/{booking_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("BadRequestParameterException")))
                .andExpect(jsonPath("$.debugMessage", is("В запросе на изменение статуса бронирования " +
                        "не указано подтверждено оно или нет")));

        verify(bookingClient, Mockito.never())
                .changeStatus(1L, 1L, false);
    }

    @Test
    public void getAllWithStateForBooker_whenGetCorrectParameters_thenReturnListOfBookingRestViews()
            throws Exception {
        when(bookingClient.getBookingsOfUser(1L, BookingState.FUTURE, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(booking)));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(
                        objectMapper.writeValueAsString(List.of(booking)), List.class))));

        verify(bookingClient, Mockito.times(1))
                .getBookingsOfUser(1L, BookingState.FUTURE, 0, 10);
        System.out.println(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
    }

    @Test
    public void getAllWithStateForBooker_whenGetIncorrectState_thenThrowUnsupportedStatusException()
            throws Exception {
        when(bookingClient.getBookingsOfUser(1L, BookingState.ALL, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(booking)));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "some_state")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Unknown state: some_state")));

        verify(bookingClient, Mockito.never())
                .getBookingsOfUser(1L, BookingState.ALL, 0, 10);
        System.out.println(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
    }

    @Test
    public void getAllWithStateForItemOwner_whenGetCorrectParameters_thenReturnListOfBookingRestViews()
            throws Exception {
        when(bookingClient.getBookingsOfOwnersItems(1L, BookingState.FUTURE, 0, 10))
                .thenReturn(ResponseEntity.ok(List.of(booking)));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(
                        objectMapper.writeValueAsString(List.of(booking)), List.class))));

        verify(bookingClient, Mockito.times(1))
                .getBookingsOfOwnersItems(1L, BookingState.FUTURE, 0, 10);
    }

}