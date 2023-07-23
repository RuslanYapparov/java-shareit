package ru.practicum.shareit_gateway.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit_gateway.booking.dto.*;
import ru.practicum.shareit_gateway.common.ShareItConstants;
import ru.practicum.shareit_gateway.exception.ErrorResponseView;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.*;

@RestClientTest(BookingClient.class)
public class BookingClientTest {
    @Autowired
    private BookingClient bookingClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    private final BookingRestCommand bookingCommand = BookingRestCommand.builder()
            .itemId(1L)
            .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .build();

    private final BookingRestView bookingView = BookingRestView.builder()
            .id(1)
            .booker(new Booker(1L))
            .item(new BookedItem(1L, "item_name"))
            .status("WAITING")
            .start(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .end(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/bookings"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(bookingCommand)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(bookingView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.save(1L, bookingCommand);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        BookingRestView receivedBookingView = objectMapper.readValue(jsonFromResponse, BookingRestView.class);
        assertThat(receivedBookingView, equalTo(bookingView));
    }

    @Test
    public void changeStatus_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/bookings/14?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andRespond(withSuccess(objectMapper.writeValueAsString(bookingView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.changeStatus(7L, 14L, true);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        BookingRestView receivedBookingView = objectMapper.readValue(jsonFromResponse, BookingRestView.class);
        assertThat(receivedBookingView, equalTo(bookingView));
    }

    @Test
    public void changeStatus_whenGetUnsuccessfulStatus_thenSendRequestWithBody() throws Exception {
        ErrorResponseView exception = new ErrorResponseView(500, "exception", "exception_message");
        String exceptionJson = objectMapper.writeValueAsString(exception);

        mockServer.expect(requestTo("http://localhost:9090/bookings/14?approved=true"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR)
                        .body(exceptionJson));

        ResponseEntity<Object> response = bookingClient.changeStatus(7L, 14L, true);
        assertThat(response.getStatusCodeValue(), equalTo(500));
        assertThat(response.getBody(), notNullValue());
    }

    @Test
    public void getBookingsOfOwnersItems_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/bookings/owner?state=ALL&from=0&size=5"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(bookingView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingsOfOwnersItems(3L, BookingState.ALL, 0, 5);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedBookingViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedBookingViews.get(0));
        BookingRestView bookingRestViewFromJson = objectMapper.readValue(jsonObjectFromList, BookingRestView.class);
        assertThat(bookingRestViewFromJson, equalTo(bookingView));
    }

    @Test
    public void getBookingsOfUser_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/bookings?state=ALL&from=0&size=5"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(bookingView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getBookingsOfUser(3L, BookingState.ALL, 0, 5);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedBookingViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedBookingViews.get(0));
        BookingRestView bookingRestViewFromJson = objectMapper.readValue(jsonObjectFromList, BookingRestView.class);
        assertThat(bookingRestViewFromJson, equalTo(bookingView));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/bookings/2"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(bookingView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = bookingClient.getById(7L, 2L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        BookingRestView receivedBookingView = objectMapper.readValue(jsonFromResponse, BookingRestView.class);
        assertThat(receivedBookingView, equalTo(bookingView));
    }

}
