package ru.practicum.shareit.request;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
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

import ru.practicum.shareit.exception.InternalLogicException;
import ru.practicum.shareit.exception.ObjectNotFoundException;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;
import ru.practicum.shareit.request.dto.RequestedItem;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
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
            .created(LocalDateTime.now().withNano(0))
            .lastModified(LocalDateTime.now().withNano(0))
            .items(new ArrayList<>(List.of(RequestedItem.builder().build())))
            .build();
    private final ItemRequestRestView secondReturnedRequest = ItemRequestRestView.builder()
            .id(2)
            .requesterId(1)
            .description("Одолжу на пару дней аппарат МРТ")
            .created(LocalDateTime.now().withNano(0))
            .lastModified(LocalDateTime.now().withNano(0))
            .items(new ArrayList<>(List.of(RequestedItem.builder().build())))
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(itemRequestService.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenReturn(firstReturnedRequest);

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
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
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

    @ParameterizedTest
    @ValueSource(longs = {0L, -2L})
    public void save_whenGetIncorrectUserId_thenThrowException(long userId) throws Exception {
        when(itemRequestService.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenThrow(new ObjectNotFoundException("Это исключение не должно появиться"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", userId)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand("request")))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class));

        verify(itemRequestService, Mockito.never())
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r"})
    @NullSource
    public void save_whenGetIncorrectRestCommand_thenThrowException(String description) throws Exception {
        when(itemRequestService.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenThrow(new ObjectNotFoundException("Это исключение не должно появиться"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand(description)))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException"), String.class));

        verify(itemRequestService, Mockito.never())
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @Test
    public void save_whenGetRestCommandWithMoreThan2000Characters_thenThrowException() throws Exception {
        when(itemRequestService.save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class)))
                .thenThrow(new ObjectNotFoundException("Это исключение не должно появиться"));

        mvc.perform(post("/requests")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(initializeNewItemRequestRestCommand("a".repeat(2001))))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException"), String.class));

        verify(itemRequestService, Mockito.never())
                .save(Mockito.anyLong(), Mockito.any(ItemRequestRestCommand.class));
    }

    @ParameterizedTest
    @ValueSource(ints = {0, -1})
    public void getAllAndGetAllRequestsOfRequester_whenGetIncorrectParameters_thenThrowException(int value)
            throws Exception {
        when(itemRequestService.getAllRequestsOfRequester(Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));
        when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(get("/requests")
                        .header("X-Sharer-User-Id", value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class));
        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("size", String.valueOf(value))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("BadRequestParameterException"), String.class));

        verify(itemRequestService, Mockito.never())
                .getAllRequestsOfRequester(Mockito.anyLong());
        verify(itemRequestService, Mockito.never())
                .getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }



    @Test
    public void getAll_whenGetIncorrectFromParameter_thenThrowException() throws Exception {
        when(itemRequestService.getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(get("/requests/all")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "-1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("BadRequestParameterException"), String.class));

        verify(itemRequestService, Mockito.never())
                .getAll(Mockito.anyLong(), Mockito.anyInt(), Mockito.anyInt());
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    public void getById_whenGetIncorrectArguments_thenThrowException(long value) throws Exception {
        when(itemRequestService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(get("/requests/{request_id}", String.valueOf(value))
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class));
        mvc.perform(get("/requests/{request_id}", 1L)
                        .header("X-Sharer-User-Id", value)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class));

        verify(itemRequestService, Mockito.never())
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    private ItemRequestRestCommand initializeNewItemRequestRestCommand(String description) {
        ItemRequestRestCommand newItemRequestRestCommand = new ItemRequestRestCommand();
        newItemRequestRestCommand.setDescription(description);
        return newItemRequestRestCommand;
    }

}