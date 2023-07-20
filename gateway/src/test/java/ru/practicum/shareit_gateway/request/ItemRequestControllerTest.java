package ru.practicum.shareit_gateway.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit_gateway.common.ShareItConstants;
import ru.practicum.shareit_gateway.exception.InternalLogicException;
import ru.practicum.shareit_gateway.request.dto.ItemRequestRestCommand;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemRequestController.class)
public class ItemRequestControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemRequestClient itemRequestClient;
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
        when(itemRequestClient.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenReturn(ResponseEntity.ok().body(firstReturnedRequest));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand("request")))
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
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту

        verify(itemRequestClient, Mockito.times(1))
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    public void save_whenGetIncorrectUserId_thenThrowException(long userId) throws Exception {
        when(itemRequestClient.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAllRequestsOfRequester_whenGetCorrectParameters_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(itemRequestClient.getAllRequestsOfRequester(Mockito.anyLong()))
                .thenReturn(ResponseEntity.ok().body(List.of(firstReturnedRequest, secondReturnedRequest)));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(firstReturnedRequest, secondReturnedRequest)), List.class))));
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(itemRequestClient, Mockito.times(1))
                .getAllRequestsOfRequester(Mockito.anyLong());
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(itemRequestClient.getAll(1L, 0, 20))
                .thenReturn(ResponseEntity.ok().body(List.of(firstReturnedRequest, secondReturnedRequest)));

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
        verify(itemRequestClient, Mockito.times(1))
                .getAll(1L, 0, 20);
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(itemRequestClient.getById(1L, 1L))
                .thenReturn(ResponseEntity.ok(firstReturnedRequest));

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
        verify(itemRequestClient, Mockito.times(1))
                .getById(1L, 1L);
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r"})
    @NullSource
    public void save_whenGetIncorrectRestCommand_thenThrowException(String description) throws Exception {
        when(itemRequestClient.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand(description)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException"), String.class));

        verify(itemRequestClient, Mockito.never())
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @Test
    public void save_whenGetRestCommandWithMoreThan2000Characters_thenThrowException() throws Exception {
        when(itemRequestClient.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand("a".repeat(2001))))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException"), String.class));

        verify(itemRequestClient, Mockito.never())
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void allGetMethods_whenGetIncorrectParameters_thenThrowException(int value) throws Exception {
        when(itemRequestClient.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));
        when(itemRequestClient.getAllRequestsOfRequester(Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));
        when(itemRequestClient.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", String.valueOf(value))
                        .param("from", "0")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", String.valueOf(value))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", String.valueOf(value))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/requests/{request_id}", "1")
                        .header("X-Sharer-User-Id", String.valueOf(value))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        mvc.perform(get("/requests/{request_id}", String.valueOf(value))
                        .header("X-Sharer-User-Id", 1)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void getAll_whenGetIncorrectFromParameter_thenThrowException() throws Exception {
        when(itemRequestClient.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .param("size", "10")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    private ItemRequestRestCommand initializeNewItemRequestRestCommand(String description) {
        ItemRequestRestCommand newItemRequestRestCommand = new ItemRequestRestCommand();
        newItemRequestRestCommand.setDescription(description);
        return newItemRequestRestCommand;
    }

}