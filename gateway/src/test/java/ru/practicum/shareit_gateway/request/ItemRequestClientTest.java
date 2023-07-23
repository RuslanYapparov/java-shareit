package ru.practicum.shareit_gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;

import ru.practicum.shareit_gateway.common.ShareItConstants;
import ru.practicum.shareit_gateway.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_gateway.request.dto.ItemRequestRestView;
import ru.practicum.shareit_gateway.request.dto.RequestedItem;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemRequestClient.class)
public class ItemRequestClientTest {
    @Autowired
    private ItemRequestClient itemRequestClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    private final ItemRequestRestCommand requestCommand =
            initializeNewItemRequestRestCommand("request_description");

    private final ItemRequestRestView requestView = ItemRequestRestView.builder()
            .id(1)
            .requesterId(1)
            .description("request_description")
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .items(new ArrayList<>(List.of(RequestedItem.builder().build())))
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(requestCommand)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(requestView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.save(1L, requestCommand);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        ItemRequestRestView receivedItemRequestView = objectMapper.readValue(jsonFromResponse, ItemRequestRestView.class);
        assertThat(receivedItemRequestView, equalTo(requestView));
    }

    @Test
    public void getAllRequestsOfRequester_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/requests"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(requestView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getAllRequestsOfRequester(3);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedItemRequestViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedItemRequestViews.get(0));
        ItemRequestRestView itemRequestRestViewFromJson = objectMapper.readValue(jsonObjectFromList, ItemRequestRestView.class);
        assertThat(itemRequestRestViewFromJson, equalTo(requestView));
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/requests/all?from=0&size=7"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(requestView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getAll(3, 0, 7);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedItemRequestViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedItemRequestViews.get(0));
        ItemRequestRestView itemRequestRestViewFromJson = objectMapper.readValue(jsonObjectFromList, ItemRequestRestView.class);
        assertThat(itemRequestRestViewFromJson, equalTo(requestView));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/requests/2"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(requestView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemRequestClient.getById(7L, 2L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        ItemRequestRestView receivedItemRequestView = objectMapper.readValue(jsonFromResponse, ItemRequestRestView.class);
        assertThat(receivedItemRequestView, equalTo(requestView));
    }

    private ItemRequestRestCommand initializeNewItemRequestRestCommand(String description) {
        ItemRequestRestCommand newItemRequestRestCommand = new ItemRequestRestCommand();
        newItemRequestRestCommand.setDescription(description);
        return newItemRequestRestCommand;
    }

}
