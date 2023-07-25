package ru.practicum.shareit_server.request;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;
import ru.practicum.shareit_server.request.dto.RequestedItem;

import java.io.IOException;
import java.util.List;

@JsonTest
public class ItemRequestDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRequestRestCommand> commandJsonTester;
    @Autowired
    private JacksonTester<ItemRequestRestView> viewJsonTester;

    @Test
    public void shouldReturnCorrectItemRequestCommand() throws IOException {
        String itemRestCommandJson = "{\"description\":\"ru.practicum.shareit.request description\"}";

        ItemRequestRestCommand itemRequestRestCommand = commandJsonTester.parseObject(itemRestCommandJson);

        assertThat(itemRequestRestCommand).isNotNull();
        assertEquals("ru.practicum.shareit.request description", itemRequestRestCommand.getDescription());
    }

    @Test
    public void shouldReturnCorrectItemRestViewJson() throws IOException {
        RequestedItem requestedItem = RequestedItem.builder()
                .id(1L)
                .requestId(1L)
                .name("Кошачья чесалка")
                .description("Берегите котов")
                .available(true)
                .build();
        ItemRequestRestView itemRequestRestView = ItemRequestRestView.builder()
                .id(1L)
                .requesterId(1L)
                .description("Очень срочна нужна кошачья чесалка")
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .items(List.of(requestedItem))
                .build();

        JsonContent<ItemRequestRestView> requestJson = viewJsonTester.write(itemRequestRestView);

        assertThat(requestJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(requestJson).extractingJsonPathNumberValue("$.requesterId").isEqualTo(1);
        assertThat(requestJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("Очень срочна нужна кошачья чесалка");
        assertThat(requestJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(requestJson).extractingJsonPathStringValue("$.lastModified")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(requestJson).extractingJsonPathArrayValue("$.items").doesNotContainNull();
        assertThat(requestJson).extractingJsonPathArrayValue("$.items")
                .asString().contains(List.of("available", "Кошачья чесалка", "Берегите котов", "true", "1"));
    }

}