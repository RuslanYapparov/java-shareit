package ru.practicum.shareit_gateway.common;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit_gateway.exception.InternalLogicException;
import ru.practicum.shareit_gateway.item.*;

import java.nio.charset.StandardCharsets;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ShareitExceptionHandlerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemClient itemClient;
    @Autowired
    private MockMvc mvc;

    @Test
    public void handleInternalLogicException_whenMistakeInServerCode_returnErrorResponseView() throws Exception {
        when(itemClient.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Что-то пошло не так"));

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception", is("InternalLogicException")))
                .andExpect(jsonPath("$.debugMessage", is("Что-то пошло не так")));

        verify(itemClient, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleMethodArgumentTypeMismatch_whenGetMismatchedParameter_returnErrorResponseView() throws Exception {
        when(itemClient.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Что-то пошло не так"));

        mvc.perform(get("/items/{item_id}", "first_item")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.exception", is("MethodArgumentTypeMismatchException")));

        verify(itemClient, Mockito.never())
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

    @Test
    public void handleAnotherUnhandledException_whenServerThrowRuntimeException_returnErrorResponseView() throws Exception {
        when(itemClient.getById(Mockito.anyLong(), Mockito.anyLong()))
                .thenAnswer(anyValue -> 5 / 0);

        mvc.perform(get("/items/1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.exception", is("class java.lang.ArithmeticException")));

        verify(itemClient, Mockito.times(1))
                .getById(Mockito.anyLong(), Mockito.anyLong());
    }

}