package ru.practicum.shareit_server.item;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.item.comment.Comment;
import ru.practicum.shareit_server.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = ItemController.class)
public class ItemControllerTest {
    @Autowired
    ObjectMapper objectMapper;
    @MockBean
    ItemService itemService;
    @Autowired
    private MockMvc mvc;

    private final ItemRestView item = ItemRestView.builder()
            .id(1)
            .ownerId(1)
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
    public void save_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(itemService.save(Mockito.anyLong(), Mockito.any(ItemRestCommand.class)))
                .thenReturn(item);

        mvc.perform(post("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(ItemRestCommand.builder()
                                .name("n")
                                .description("d")
                                .available(true)
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(item.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
                .andExpect(jsonPath("$.created", is(item.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(item.getLastModified().toString())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())))
                .andExpect(jsonPath("$.comments", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        item.getComments()), List.class))));
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту

        verify(itemService, Mockito.times(1))
                .save(Mockito.anyLong(), Mockito.any(ItemRestCommand.class));
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(itemService.getAll(1L, 0, 20))
                .thenReturn(new PageImpl<>(List.of(item)));

        mvc.perform(get("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(item)), List.class))));
        verify(itemService, Mockito.times(1))
                .getAll(1L, 0, 20);
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(itemService.getById(1L, 1L))
                .thenReturn(item);

        mvc.perform(get("/items/{item_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(item.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
                .andExpect(jsonPath("$.created", is(item.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(item.getLastModified().toString())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())))
                .andExpect(jsonPath("$.comments", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        item.getComments()), List.class))));
        verify(itemService, Mockito.times(1))
                .getById(1L, 1L);
    }

    @Test
    public void update_whenGetCorrectParameters_thenReturnCorrectRestViewObject() throws Exception {
        when(itemService.update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemRestCommand.class)))
                .thenReturn(item);

        mvc.perform(patch("/items/{item_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(ItemRestCommand.builder()
                                .name("n")
                                .description("d")
                                .available(true)
                                .build()))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(item.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
                .andExpect(jsonPath("$.created", is(item.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(item.getLastModified().toString())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())))
                .andExpect(jsonPath("$.comments", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        item.getComments()), List.class))));

        verify(itemService, Mockito.times(1))
                .update(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ItemRestCommand.class));
    }


    @Test
    public void deleteById_whenGetCorrectParameters_thenReturnCorrectRestViewObject()
            throws Exception {
        when(itemService.deleteById(1L, 1L)).thenReturn(item);

        mvc.perform(delete("/items/{item_id}", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(item.getId()), Long.class))
                .andExpect(jsonPath("$.ownerId", is(item.getOwnerId()), Long.class))
                .andExpect(jsonPath("$.name", is(item.getName())))
                .andExpect(jsonPath("$.description", is(item.getDescription())))
                .andExpect(jsonPath("$.available", is(item.isAvailable()), Boolean.class))
                .andExpect(jsonPath("$.created", is(item.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(item.getLastModified().toString())))
                .andExpect(jsonPath("$.nextBooking", is(nullValue())))
                .andExpect(jsonPath("$.comments", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        item.getComments()), List.class))));
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(itemService, Mockito.times(1))
                .deleteById(1L, 1L);
    }

    @Test
    public void deleteAll_whenCalled_thenReturnCorrectListOfRestViewObject()
            throws Exception {

        mvc.perform(delete("/items")
                        .header("X-Sharer-User-Id", 1L)
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());

        verify(itemService, Mockito.times(1))
                .deleteAll(1L);
    }

    @Test
    public void searchInItemsNamesAndDescriptionByText_whenGetCorrectParameters_thenReturnCorrectListOfRestViewObject()
            throws Exception {
        when(itemService.searchInNamesAndDescriptionsByText(1L, "text", 0, 20))
                .thenReturn(new PageImpl<>(List.of(item)));

        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "text")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(objectMapper.readValue(objectMapper.writeValueAsString(
                        List.of(item)), List.class))));
        // Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(itemService, Mockito.times(1))
                .searchInNamesAndDescriptionsByText(1L, "text",0, 20);
    }

    @Test
    public void searchInItemsNamesAndDescriptionByText_whenGetBlankText_thenReturnEmptyListOfRestViewObject()
            throws Exception {
        mvc.perform(get("/items/search")
                        .header("X-Sharer-User-Id", 1L)
                        .param("text", "")
                        .param("from", "0")
                        .param("size", "20")
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", is(emptyIterable())));
    }

    @Test
    public void saveNewComment_whenGetCorrectParameters_thenReturnComment() throws Exception {
        Comment comment = Comment.builder()
                .id(1)
                .authorId(1)
                .authorName("name")
                .text("text")
                .created(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .lastModified(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
                .build();
        CommentRestCommand commentRestCommand = new CommentRestCommand();
        commentRestCommand.setText("text");
        when(itemService.addCommentToItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentRestCommand.class)))
                .thenReturn(comment);

        mvc.perform(post("/items/{item_id}/comment", "1")
                        .header("X-Sharer-User-Id", 1L)
                        .content(objectMapper.writeValueAsString(commentRestCommand))
                        .characterEncoding(StandardCharsets.UTF_8)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(comment.getId()), Long.class))
                .andExpect(jsonPath("$.authorId", is(comment.getAuthorId()), Long.class))
                .andExpect(jsonPath("$.authorName", is(comment.getAuthorName())))
                .andExpect(jsonPath("$.text", is(comment.getText())))
                .andExpect(jsonPath("$.created", is(comment.getCreated().toString())))
                .andExpect(jsonPath("$.lastModified", is(comment.getLastModified().toString())));
// Выглядит довольно странно, но пока нашел только такой способ приведения списков объектов к проверяемому варианту
        verify(itemService, Mockito.times(1))
                .addCommentToItem(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(CommentRestCommand.class));
    }

}