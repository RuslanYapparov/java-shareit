package ru.practicum.shareit_gateway.user;

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
import ru.practicum.shareit_gateway.user.dto.UserRestCommand;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserClient userClient;
    @Autowired
    private MockMvc mvc;

    private final UserRestView firstReturnedUser = UserRestView.builder()
            .id(1)
            .name("user_name")
            .email("first@ru.practicum.shareit.user.ru")
            .telephoneNumber("777")
            .telephoneVisible(true)
            .avatarUri(ShareItConstants.DEFAULT_URI)
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .userRating(1.1F)
            .address(UserAddress.builder()
                    .country("country")
                    .region("region")
                    .cityOrSettlement("city")
                    .cityDistrict("district")
                    .street("street")
                    .houseNumber(1)
                    .build())
            .build();
    private final UserRestView secondReturnedUser = UserRestView.builder()
            .id(2)
            .name("user_name2")
            .email("second@ru.practicum.shareit.user.ru")
            .telephoneNumber("777")
            .telephoneVisible(false)
            .avatarUri(ShareItConstants.DEFAULT_URI)
            .created(LocalDateTime.now().withNano(0))
            .lastModified(LocalDateTime.now().withNano(0))
            .userRating(2.2F)
            .address(UserAddress.builder()
                    .country("country")
                    .region("region")
                    .cityOrSettlement("city")
                    .cityDistrict("district")
                    .street("street")
                    .houseNumber(2)
                    .build())
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(userClient.save(Mockito.any(UserRestCommand.class)))
                .thenReturn(ResponseEntity.ok(firstReturnedUser));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(firstReturnedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstReturnedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstReturnedUser.getName())))
                .andExpect(jsonPath("$.email", is(firstReturnedUser.getEmail())))
                .andExpect(jsonPath("$.telephoneNumber", is(firstReturnedUser.getTelephoneNumber())))
                .andExpect(jsonPath("$.created", is(firstReturnedUser.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(firstReturnedUser.getLastModified().toString())))
                .andExpect(jsonPath("$.address.country", is(firstReturnedUser.getAddress().getCountry())));

        verify(userClient, Mockito.times(1))
                .save(Mockito.any(UserRestCommand.class));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", " ", "  ", "\t", "\n", "\r"})
    @NullSource
    public void save_whenGetIncorrectRestCommand_thenThrowException(String incorrectString) throws Exception {
        when(userClient.save(Mockito.any(UserRestCommand.class)))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(UserRestCommand.builder()
                                .name("user_name")
                                .email(incorrectString)
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException"), String.class));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(UserRestCommand.builder()
                                .name(incorrectString)
                                .email("ru.practicum.shareit.user@email.ru")
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("MethodArgumentNotValidException"), String.class));

        verify(userClient, Mockito.never())
                .save(Mockito.any(UserRestCommand.class));
    }

    @Test
    public void save_whenGetEmailWithoutDot_thenThrowException() throws Exception {
        when(userClient.save(Mockito.any(UserRestCommand.class)))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(post("/users")
                        .content(objectMapper.writeValueAsString(UserRestCommand.builder()
                                .name("Gosha")
                                .email("gosha@gosha")
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class))
                .andExpect(jsonPath("$.exception", is("BadRequestBodyException"), String.class));
    }

    @Test
    public void getAll_whenCalled_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(userClient.getAll()).thenReturn(ResponseEntity.ok().body(List.of(firstReturnedUser, secondReturnedUser)));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(firstReturnedUser, secondReturnedUser)), List.class))));
        // Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(userClient, Mockito.times(1))
                .getAll();
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(userClient.getById(1L)).thenReturn(ResponseEntity.ok().body(firstReturnedUser));

        mvc.perform(get("/users/{user_id}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstReturnedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstReturnedUser.getName())))
                .andExpect(jsonPath("$.email", is(firstReturnedUser.getEmail())))
                .andExpect(jsonPath("$.telephoneNumber", is(firstReturnedUser.getTelephoneNumber())))
                .andExpect(jsonPath("$.created", is(firstReturnedUser.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(firstReturnedUser.getLastModified().toString())))
                .andExpect(jsonPath("$.address.country", is(firstReturnedUser.getAddress().getCountry())));
        verify(userClient, Mockito.times(1))
                .getById(1L);
    }

    @ParameterizedTest
    @ValueSource(longs = {0, -1})
    public void getById_whenGetIncorrectArguments_thenThrowException(long value) throws Exception {
        when(userClient.getById(Mockito.anyLong()))
                .thenThrow(new InternalLogicException("Это исключение не должно появиться"));

        mvc.perform(get("/users/{user_id}", String.valueOf(value))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.statusCode", is(400), Integer.class));

        verify(userClient, Mockito.never())
                .getById(Mockito.anyLong());
    }

    @Test
    public void update_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(userClient.update(Mockito.anyLong(), Mockito.any(UserRestCommand.class)))
                .thenReturn(ResponseEntity.ok(firstReturnedUser));

        mvc.perform(patch("/users/{user_id}", "1")
                        .content(objectMapper.writeValueAsString(firstReturnedUser))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstReturnedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstReturnedUser.getName())))
                .andExpect(jsonPath("$.email", is(firstReturnedUser.getEmail())))
                .andExpect(jsonPath("$.telephoneNumber", is(firstReturnedUser.getTelephoneNumber())))
                .andExpect(jsonPath("$.created", is(firstReturnedUser.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(firstReturnedUser.getLastModified().toString())))
                .andExpect(jsonPath("$.address.country", is(firstReturnedUser.getAddress().getCountry())));

        verify(userClient, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.any(UserRestCommand.class));
    }

    @Test
    public void deleteById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(userClient.deleteById(1L)).thenReturn(ResponseEntity.ok().body(firstReturnedUser));

        mvc.perform(delete("/users/{user_id}", "1")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(firstReturnedUser.getId()), Long.class))
                .andExpect(jsonPath("$.name", is(firstReturnedUser.getName())))
                .andExpect(jsonPath("$.email", is(firstReturnedUser.getEmail())))
                .andExpect(jsonPath("$.telephoneNumber", is(firstReturnedUser.getTelephoneNumber())))
                .andExpect(jsonPath("$.created", is(firstReturnedUser.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(firstReturnedUser.getLastModified().toString())))
                .andExpect(jsonPath("$.address.country", is(firstReturnedUser.getAddress().getCountry())));
        verify(userClient, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    public void deleteAll_whenCalled_thenReturnNothing()
            throws Exception {

        mvc.perform(delete("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userClient, Mockito.times(1))
                .deleteAll();
    }

}