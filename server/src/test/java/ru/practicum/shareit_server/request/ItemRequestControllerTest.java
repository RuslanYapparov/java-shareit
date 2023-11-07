package ru.practicum.shareit_server.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;
import ru.practicum.shareit_server.request.dto.RequestedItem;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestService itemRequestService;
    @Autowired
    private MockMvc mvc;

    private final ItemRequestRestView firstReturnedRequest = ItemRequestRestView.builder()
            .id(1)
            .requesterId(1)
            .description("Дайте, пожалуйста, мазохинатор")
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .items(new ArrayList<>(List.of(RequestedItem.builder().build())))
            .build();
    private final ItemRequestRestView secondReturnedRequest = ItemRequestRestView.builder()
            .id(2)
            .requesterId(1)
            .description("Одолжу на пару дней аппарат МРТ")
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .items(new ArrayList<>(List.of(RequestedItem.builder().build())))
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(itemRequestService.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenReturn(firstReturnedRequest);

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand("ru.practicum.shareit.request")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstReturnedRequest.getId()), Long.class))
                .andExpect(jsonPath("$.requesterId", is(firstReturnedRequest.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.description", is(firstReturnedRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(firstReturnedRequest.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(firstReturnedRequest.getLastModified().toString())))
                .andExpect(jsonPath("$.items", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        firstReturnedRequest.getItems()), List.class))));

        verify(itemRequestService, Mockito.times(1))
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @Test
    public void getAllRequestsOfRequester_whenGetCorrectParameters_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(itemRequestService.getAllRequestsOfRequester(Mockito.anyLong()))
                .thenReturn(List.of(firstReturnedRequest, secondReturnedRequest));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(firstReturnedRequest, secondReturnedRequest)), List.class))));
        verify(itemRequestService, Mockito.times(1))
                .getAllRequestsOfRequester(Mockito.anyLong());
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(itemRequestService.getAll(1L, 0, 20))
                .thenReturn(new PageImpl<>(List.of(firstReturnedRequest, secondReturnedRequest)));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(firstReturnedRequest, secondReturnedRequest)), List.class))));
        // Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(itemRequestService, Mockito.times(1))
                .getAll(1L, 0, 20);
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(itemRequestService.getById(1L, 1L))
                .thenReturn(firstReturnedRequest);

        mvc.perform(get("/requests/{request_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.id", is(firstReturnedRequest.getId()), Long.class))
                .andExpect(jsonPath("$.requesterId", is(firstReturnedRequest.getRequesterId()), Long.class))
                .andExpect(jsonPath("$.description", is(firstReturnedRequest.getDescription())))
                .andExpect(jsonPath("$.created", is(firstReturnedRequest.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(firstReturnedRequest.getLastModified().toString())))
                .andExpect(jsonPath("$.items", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        firstReturnedRequest.getItems()), List.class))));
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(itemRequestService, Mockito.times(1))
                .getById(1L, 1L);
    }

    private ItemRequestRestCommand initializeNewItemRequestRestCommand(String description) {
        ItemRequestRestCommand newItemRequestRestCommand = new ItemRequestRestCommand();
        newItemRequestRestCommand.setDescription(description);
        return newItemRequestRestCommand;
    }

}