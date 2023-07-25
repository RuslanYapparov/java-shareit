package ru.practicum.shareit_gateway.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit_gateway.client.BaseClient;
import ru.practicum.shareit_gateway.item.dto.CommentRestCommand;
import ru.practicum.shareit_gateway.item.dto.ItemRestCommand;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(long userId, ItemRestCommand itemRestCommand) {
        return post("", userId, itemRestCommand);
    }

    public ResponseEntity<Object> update(long userId, long itemId, ItemRestCommand itemRestCommand) {
        return patch("/" + itemId, userId, itemRestCommand);
    }

    public ResponseEntity<Object> getAll(long userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getById(long userId, Long itemId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> deleteAll(long userId) {
        return delete("", userId);
    }

    public ResponseEntity<Object> deleteById(long userId, long itemId) {
        return delete("/" + itemId, userId);
    }

    public ResponseEntity<Object> searchInNamesAndDescriptionsByText(
            long userId, String text, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}&from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> addCommentToItem(long userId, long itemId, CommentRestCommand commentRestCommand) {
        return post("/" + itemId + "/comment", userId, commentRestCommand);
    }

}