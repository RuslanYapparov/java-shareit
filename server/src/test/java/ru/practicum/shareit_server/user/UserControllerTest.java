package ru.practicum.shareit_server.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = UserController.class)
public class UserControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    UserService userService;
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
        when(userService.save(Mockito.any(UserRestCommand.class)))
                .thenReturn(firstReturnedUser);

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

        verify(userService, Mockito.times(1))
                .save(Mockito.any(UserRestCommand.class));
    }

    @Test
    public void getAll_whenCalled_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(userService.getAll()).thenReturn(List.of(firstReturnedUser, secondReturnedUser));

        mvc.perform(get("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(firstReturnedUser, secondReturnedUser)), List.class))));
        verify(userService, Mockito.times(1))
                .getAll();
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(userService.getById(1L)).thenReturn(firstReturnedUser);

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
        verify(userService, Mockito.times(1))
                .getById(1L);
    }

    @Test
    public void update_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(userService.update(Mockito.anyLong(), Mockito.any(UserRestCommand.class)))
                .thenReturn(firstReturnedUser);

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

        verify(userService, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.any(UserRestCommand.class));
    }

    @Test
    public void deleteById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(userService.deleteById(1L)).thenReturn(firstReturnedUser);

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
        verify(userService, Mockito.times(1))
                .deleteById(1L);
    }

    @Test
    public void deleteAll_whenCalled_thenReturnCorrectListOfRestViewObject()
            throws Exception {

        mvc.perform(delete("/users")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(userService, Mockito.times(1))
                .deleteAll();
    }

}