package ru.practicum.shareit_server.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_server.booking.dto.BookedItem;
import ru.practicum.shareit_server.booking.dto.Booker;
import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.booking.dto.BookingRestCommand;
import ru.practicum.shareit_server.booking.dto.BookingRestView;

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
    BookingService bookingService;
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
        when(bookingService.save(Mockito.anyLong(), Mockito.any(BookingRestCommand.class)))
                .thenReturn(booking);

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

        verify(bookingService, Mockito.times(1))
                .save(Mockito.anyLong(), Mockito.any(BookingRestCommand.class));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(bookingService.getById(1L, 1L))
                .thenReturn(booking);

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
        verify(bookingService, Mockito.times(1))
                .getById(1L, 1L);
    }

    @Test
    public void changeStatus_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(bookingService.changeBookingStatus(1L, 1, true))
                .thenReturn(booking);

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

        verify(bookingService, Mockito.times(1))
                .changeBookingStatus(1L, 1, true);
    }

    @Test
    public void getAllWithStateForBooker_whenGetCorrectParameters_thenReturnListOfBookingRestViews()
            throws Exception {
        when(bookingService.getAllForBookerWithStateParameter(1L, "FUTURE", 0, 10))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(
                        objectMapper.writeValueAsString(List.of(booking)), List.class))));

        verify(bookingService, Mockito.times(1))
                .getAllForBookerWithStateParameter(1L, "FUTURE", 0, 10);
        System.out.println(ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
    }

    @Test
    public void getAllWithStateForItemOwner_whenGetCorrectParameters_thenReturnListOfBookingRestViews()
            throws Exception {
        when(bookingService.getAllForItemOwnerWithStateParameter(1L, "FUTURE", 0, 10))
                .thenReturn(new PageImpl<>(List.of(booking)));

        mvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1L)
                        .param("state", "FUTURE")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(
                        objectMapper.writeValueAsString(List.of(booking)), List.class))));

        verify(bookingService, Mockito.times(1))
                .getAllForItemOwnerWithStateParameter(1L, "FUTURE", 0, 10);
    }

}