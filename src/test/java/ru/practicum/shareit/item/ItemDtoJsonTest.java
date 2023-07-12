package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@JsonTest
public class ItemDtoJsonTest {
    @Autowired
    private JacksonTester<ItemRestCommand> commandJsonTester;
    @Autowired
    private JacksonTester<ItemRestView> viewJsonTester;

    @Test
    public void shouldReturnCorrectItemCommand() throws IOException {
        String itemRestCommandJson = "{\"requestId\":\"1\",\"name\":\"item\"," +
                "\"description\":\"item_description\",\"available\":\"true\"}";

        ItemRestCommand itemRestCommand = commandJsonTester.parseObject(itemRestCommandJson);

        assertThat(itemRestCommand).isNotNull();
        assertEquals("item", itemRestCommand.getName());
        assertEquals("item_description", itemRestCommand.getDescription());
        assertEquals(true, itemRestCommand.getAvailable());
    }

    @Test
    public void shouldReturnCorrectItemRestViewJson() throws IOException {
        ItemBooking itemBooking = new ItemBooking(1L, 1L, "REJECTED",
                ShareItConstants.DEFAULT_LOCAL_DATE_TIME, ShareItConstants.DEFAULT_LOCAL_DATE_TIME);
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .authorName("user_name")
                .text("comment_text")
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
        ItemRestView itemRestView = ItemRestView.builder()
                .id(1L)
                .requestId(1L)
                .ownerId(1L)
                .name("Танцеватель")
                .description("Отличный танцеватель на все случаи жизни")
                .available(true)
                .lastBooking(itemBooking)
                .comments(List.of(comment))
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .rent(1.1F)
                .itemRating(1.1F)
                .build();

        JsonContent<ItemRestView> itemJson = viewJsonTester.write(itemRestView);

        assertThat(itemJson).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(itemJson).extractingJsonPathNumberValue("$.requestId").isEqualTo(1);
        assertThat(itemJson).extractingJsonPathNumberValue("$.ownerId").isEqualTo(1);
        assertThat(itemJson).extractingJsonPathStringValue("$.name")
                .isEqualTo("Танцеватель");
        assertThat(itemJson).extractingJsonPathStringValue("$.description")
                .isEqualTo("Отличный танцеватель на все случаи жизни");
        assertThat(itemJson).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
        assertThat(itemJson).extractingJsonPathValue("$.nextBooking").isNull();
        assertThat(itemJson).extractingJsonPathNumberValue("$.lastBooking.id").isEqualTo(1);
        assertThat(itemJson).extractingJsonPathNumberValue("$.lastBooking.bookerId").isEqualTo(1);
        assertThat(itemJson).extractingJsonPathStringValue("$.lastBooking.status")
                .isEqualTo("REJECTED");
        assertThat(itemJson).extractingJsonPathStringValue("$.lastBooking.start")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(itemJson).extractingJsonPathStringValue("$.lastBooking.end")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(itemJson).extractingJsonPathNumberValue("$.comments[0].id")
                .isEqualTo(1);
        assertThat(itemJson).extractingJsonPathNumberValue("$.comments[0].authorId")
                .isEqualTo(1);
        assertThat(itemJson).extractingJsonPathStringValue("$.comments[0].authorName")
                .isEqualTo("user_name");
        assertThat(itemJson).extractingJsonPathStringValue("$.comments[0].text")
                .isEqualTo("comment_text");
        assertThat(itemJson).extractingJsonPathStringValue("$.comments[0].created")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(itemJson).extractingJsonPathStringValue("$.comments[0].lastModified")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(itemJson).extractingJsonPathStringValue("$.created")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(itemJson).extractingJsonPathStringValue("$.lastModified")
                .isEqualTo(ShareItConstants.DEFAULT_LOCAL_DATE_TIME.toString());
        assertThat(itemJson).extractingJsonPathNumberValue("$.rent").isEqualTo(1.1);
        assertThat(itemJson).extractingJsonPathNumberValue("$.itemRating").isEqualTo(1.1);
    }

}