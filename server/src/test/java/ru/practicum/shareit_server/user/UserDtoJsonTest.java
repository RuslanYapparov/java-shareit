package ru.practicum.shareit_server.user;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class UserDtoJsonTest {
    @Autowired
    private JacksonTester<UserRestCommand> commandJsonTester;
    @Autowired
    private JacksonTester<UserRestView> viewJsonTester;

    @Test
    public void shouldReturnCorrectUserCommand() throws IOException {
        String userRestCommandJson = "{\"name\":\"user_name\", \"email\":\"user_email\"}";

        UserRestCommand userRestCommand = commandJsonTester.parseObject(userRestCommandJson);

        assertThat(userRestCommand).isNotNull();
        assertEquals("user_name", userRestCommand.getName());
        assertEquals("user_email", userRestCommand.getEmail());
    }

    @Test
    public void shouldReturnCorrectUserViewJson() throws IOException {
        UserAddress address = UserAddress.builder()
                .country("страна")
                .region("регион")
                .cityOrSettlement("город")
                .cityDistrict("район")
                .street("улица")
                .houseNumber(777)
                .build();
        UserRestView userRestView = UserRestView.builder()
                .id(1L)
                .name("имя")
                .email("почта")
                .telephoneNumber("телефон")
                .telephoneVisible(true)
                .avatarUri(ShareItConstants.DEFAULT_URI)
                .address(address)
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .userRating(0.0F)
                .build();

        JsonContent<UserRestView> userJson = viewJsonTester.write(userRestView);

        assertThat(userJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(userJson).extractingJsonPathNumberValue("$.userRating").isEqualTo(0.0);
        assertThat(userJson).extractingJsonPathStringValue("$.name")
                .isEqualTo("имя");
        assertThat(userJson).extractingJsonPathStringValue("$.email")
                .isEqualTo("почта");
        assertThat(userJson).extractingJsonPathStringValue("$.telephoneNumber")
                .isEqualTo("телефон");
        assertThat(userJson).extractingJsonPathStringValue("$.avatarUri")
                .isEqualTo(ShareItConstants.NOT_ASSIGNED);
        assertThat(userJson).extractingJsonPathBooleanValue("$.telephoneVisible")
                .isEqualTo(true);
        assertThat(userJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(userJson).extractingJsonPathStringValue("$.lastModified")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(userJson).extractingJsonPathStringValue("$.address.country")
                .isEqualTo("страна");
        assertThat(userJson).extractingJsonPathStringValue("$.address.region")
                .isEqualTo("регион");
        assertThat(userJson).extractingJsonPathStringValue("$.address.cityOrSettlement")
                .isEqualTo("город");
        assertThat(userJson).extractingJsonPathStringValue("$.address.cityDistrict")
                .isEqualTo("район");
        assertThat(userJson).extractingJsonPathStringValue("$.address.street")
                .isEqualTo("улица");
        assertThat(userJson).extractingJsonPathNumberValue("$.address.houseNumber").isEqualTo(777);
    }

}