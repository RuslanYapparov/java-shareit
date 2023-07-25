package ru.practicum.shareit_server.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_server.exception.*;
import ru.practicum.shareit_server.item.ItemController;
import ru.practicum.shareit_server.item.ItemService;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ShareitExceptionHandlerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    @Test
    public void handleInternalLogicException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception", is("InternalLogicException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleObjectNotFoundException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new ObjectNotFoundException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.exception", is("ObjectNotFoundException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleObjectAlreadyExistException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new ObjectAlreadyExistsException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.exception", is("ObjectAlreadyExistsException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleBadRequestBodyException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new BadRequestBodyException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("BadRequestBodyException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleBadRequestParameterException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new BadRequestParameterException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("BadRequestParameterException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleBadRequestHeaderException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new BadRequestHeaderException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("BadRequestHeaderException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleUnsupportedStateException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new UnsupportedStateException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error", is("Что-то пошло не так")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleAnotherUnhandledException_whenServerThrowRuntimeException_returnErrorResponseView() throws Exception {
        when(itemService.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(anyValue -> 5 / 0);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception", is("class java.lang.ArithmeticException")));

        verify(itemService, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

}