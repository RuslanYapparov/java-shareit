package ru.practicum.shareit_gateway.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.client.MockRestServiceServer;
import ru.practicum.shareit_gateway.common.ShareItConstants;
import ru.practicum.shareit_gateway.item.dto.*;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.*;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@RestClientTest(ItemClient.class)
public class ItemClientTest {
    @Autowired
    private ItemClient itemClient;
    @Autowired
    private MockRestServiceServer mockServer;
    @Autowired
    private ObjectMapper objectMapper;

    private final ItemRestCommand itemCommand = ItemRestCommand.builder()
            .requestId(1)
            .name("item_name")
            .description("item_description")
            .available(true)
            .build();

    private final ItemRestView itemView = ItemRestView.builder()
            .id(1)
            .ownerId(1)
            .requestId(1)
            .name("item_name")
            .description("item_description")
            .available(true)
            .itemPhotoUri(ShareItConstants.DEFAULT_URI)
            .comments(List.of(Comment.builder()
                    .id(1)
                    .authorId(1)
                    .text("text")
                    .authorName("name")
                    .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                    .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                    .build()))
            .rent(0.0F)
            .itemRating(0.0F)
            .nextBooking(null)
            .lastBooking(new ItemBooking(1, 2, "APPROVED", LocalDateTime.now(), LocalDateTime.now()))
            .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .build();

    @Test
    public void save_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(itemCommand)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(itemView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.save(1L, itemCommand);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        ItemRestView receivedItemView = objectMapper.readValue(jsonFromResponse, ItemRestView.class);
        assertThat(receivedItemView, equalTo(itemView));
    }

    @Test
    public void update_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items/14"))
                .andExpect(method(HttpMethod.PATCH))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(ItemRestCommand.builder()
                        .name("item_name")
                        .build())))
                .andRespond(withSuccess(objectMapper.writeValueAsString(itemView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.update(7L, 14L, ItemRestCommand.builder()
                .name("item_name")
                .build());
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        ItemRestView receivedItemView = objectMapper.readValue(jsonFromResponse, ItemRestView.class);
        assertThat(receivedItemView, equalTo(itemView));
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items?from=0&size=5"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(itemView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getAll(3L, 0, 5);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedItemViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedItemViews.get(0));
        ItemRestView itemRestViewFromJson = objectMapper.readValue(jsonObjectFromList, ItemRestView.class);
        assertThat(itemRestViewFromJson, equalTo(itemView));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items/2"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(itemView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.getById(7L, 2L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        ItemRestView receivedItemView = objectMapper.readValue(jsonFromResponse, ItemRestView.class);
        assertThat(receivedItemView, equalTo(itemView));
    }

    @Test
    public void deleteAll_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", "3"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess());

        ResponseEntity<Object> response = itemClient.deleteAll(3L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
    }

    @Test
    public void deleteById_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items/2"))
                .andExpect(method(HttpMethod.DELETE))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(itemView), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.deleteById(7L, 2L);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        ItemRestView receivedItemView = objectMapper.readValue(jsonFromResponse, ItemRestView.class);
        assertThat(receivedItemView, equalTo(itemView));
    }

    @Test
    public void searchInNamesAndDescriptionsByText_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        mockServer.expect(requestTo("http://localhost:9090/items/search?text=text&from=0&size=5"))
                .andExpect(method(HttpMethod.GET))
                .andExpect(header("X-Sharer-User-Id", "7"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andRespond(withSuccess(objectMapper.writeValueAsString(List.of(itemView)), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.searchInNamesAndDescriptionsByText(7L, "text", 0, 5);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        List<Object> receivedItemViews = objectMapper.readValue(jsonFromResponse, List.class);
        String jsonObjectFromList = objectMapper.writeValueAsString(receivedItemViews.get(0));
        ItemRestView itemRestViewFromJson = objectMapper.readValue(jsonObjectFromList, ItemRestView.class);
        assertThat(itemRestViewFromJson, equalTo(itemView));
    }

    @Test
    public void addNewComment_whenGetCorrectParameters_thenSendCorrectRequest() throws Exception {
        Comment comment = Comment.builder()
                .id(1L)
                .authorId(1L)
                .authorName("author_name")
                .text("comment_text")
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
        CommentRestCommand commentRestCommand = new CommentRestCommand();
        commentRestCommand.setText("comment_text");

        mockServer.expect(requestTo("http://localhost:9090/items/3/comment"))
                .andExpect(method(HttpMethod.POST))
                .andExpect(header("X-Sharer-User-Id", "1"))
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(commentRestCommand)))
                .andRespond(withSuccess(objectMapper.writeValueAsString(comment), MediaType.APPLICATION_JSON));

        ResponseEntity<Object> response = itemClient.addCommentToItem(1L, 3L, commentRestCommand);
        assertThat(response.getStatusCodeValue(), equalTo(200));
        String jsonFromResponse = objectMapper.writeValueAsString(response.getBody());
        Comment commentFromJson = objectMapper.readValue(jsonFromResponse, Comment.class);
        assertThat(commentFromJson, equalTo(comment));
    }

}
