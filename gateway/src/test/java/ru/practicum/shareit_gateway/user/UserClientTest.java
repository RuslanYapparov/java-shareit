package ru.practicum.shareit_gateway.user;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;

import ru.practicum.shareit_gateway.common.ShareItConstants;
import ru.practicum.shareit_gateway.user.dto.UserRestCommand;

import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(UserClient.class)
public class UserClientTest {
    @Autowired
    private UserClient userClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    private final UserRestCommand userCommand = UserRestCommand.builder()
            .name("user_name")
            .email("user@user.tv")
            .build();

    private final UserRestView userView = UserRestView.builder()
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

    @Test
    public void save_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(userCommand)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(userView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.save(userCommand);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        UserRestView receivedUserView = objectMapper.readValue(jsonFromResponse, UserRestView.class);
        assertThat(receivedUserView, equalTo(userView));
    }

    @Test
    public void update_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/users/1"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(UserRestCommand.builder()
                        .name("user_name")
                        .build())))
                .andRespond(withSuccess(objectMapper.writeValueAsString(userView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.update(1L, UserRestCommand.builder()
                .name("user_name")
                .build());
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        UserRestView receivedUserView = objectMapper.readValue(jsonFromResponse, UserRestView.class);
        assertThat(receivedUserView, equalTo(userView));
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(userView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getAll();
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedUserViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedUserViews.get(0));
        UserRestView userRestViewFromJson = objectMapper.readValue(jsonObjectFromList, UserRestView.class);
        assertThat(userRestViewFromJson, equalTo(userView));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/users/2"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(userView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.getById(2L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        UserRestView receivedUserView = objectMapper.readValue(jsonFromResponse, UserRestView.class);
        assertThat(receivedUserView, equalTo(userView));
    }

    @Test
    public void deleteAll_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/users"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = userClient.deleteAll();
        assertThat(response.getStatusCodeValue(), equalTo(200));
    }

    @Test
    public void deleteById_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/users/2"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(userView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = userClient.deleteById(2L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        UserRestView receivedUserView = objectMapper.readValue(jsonFromResponse, UserRestView.class);
        assertThat(receivedUserView, equalTo(userView));
    }

}
