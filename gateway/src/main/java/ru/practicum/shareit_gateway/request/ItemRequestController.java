package ru.practicum.shareit_gateway.request;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit_gateway.request.dto.ItemRequestRestCommand;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Valid @RequestBody ItemRequestRestCommand itemRequestRestCommand) {
        log.debug("Gateway received request to create an itemRequest, userId={}, itemRequest={}",
                userId, itemRequestRestCommand);
        return itemRequestClient.save(userId, itemRequestRestCommand);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsOfRequester(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        log.debug("Gateway received request to get all requests of user, userId={}", userId);
        return itemRequestClient.getAllRequestsOfRequester(userId);
    }

    @GetMapping("{request_id}")
    public ResponseEntity<Object> getById(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "request_id") long requestId) {
        log.debug("Gateway received request to get request by id, userId={}, itemRequestId={}", userId, requestId);
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @Positive @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PositiveOrZero @RequestParam(name = "from", defaultValue = "0") int from,
            @Positive @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Gateway received request to get all requests, userId={}, from={}, size={}", userId, from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

}