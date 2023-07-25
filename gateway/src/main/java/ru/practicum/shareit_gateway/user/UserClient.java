package ru.practicum.shareit_gateway.user;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit_gateway.client.BaseClient;
import ru.practicum.shareit_gateway.user.dto.UserRestCommand;

@Service
public class UserClient extends BaseClient {
    private static final String API_PREFIX = "/users";

    @Autowired
    public UserClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> save(UserRestCommand userRestCommand) {
        return post("", userRestCommand);
    }

    public ResponseEntity<Object> update(long userId, UserRestCommand userRestCommand) {
        return patch("/" + userId, userRestCommand);
    }

    public ResponseEntity<Object> getAll() {
        return get("");
    }

    public ResponseEntity<Object> getById(long userId) {
        return get("/" + userId);
    }

    public ResponseEntity<Object> deleteAll() {
        return delete("");
    }

    public ResponseEntity<Object> deleteById(long userId) {
        return delete("/" + userId);
    }

}