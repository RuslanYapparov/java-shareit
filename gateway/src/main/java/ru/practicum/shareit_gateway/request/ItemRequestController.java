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
import ru.practicum.shareit_gateway.util.EndpointObjectsValidator;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Slf4j
public class ItemRequestController {
    private final ItemRequestClient itemRequestClient;

    @PostMapping
    public ResponseEntity<Object> save(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Valid @RequestBody ItemRequestRestCommand itemRequestRestCommand) {
        log.debug("Gateway received ru.practicum.shareit.request to create an itemRequest, userId={}, itemRequest={}",
                userId, itemRequestRestCommand);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "сохранение");
        return itemRequestClient.save(userId, itemRequestRestCommand);
    }

    @GetMapping
    public ResponseEntity<Object> getAllRequestsOfRequester(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        log.debug("Gateway received ru.practicum.shareit.request to get all requests of ru.practicum.shareit.user, userId={}", userId);
        EndpointObjectsValidator.checkUserIdForNullValue(
                userId, "получение всех запросов, оформленных пользователем");
        return itemRequestClient.getAllRequestsOfRequester(userId);
    }

    @GetMapping("{request_id}")
    public ResponseEntity<Object> getById(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @Positive @PathVariable(name = "request_id") long requestId) {
        log.debug("Gateway received ru.practicum.shareit.request to get ru.practicum.shareit.request by id, userId={}, itemRequestId={}", userId, requestId);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "получение по идентификатору");
        return itemRequestClient.getById(userId, requestId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(
            @PositiveOrZero @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        log.debug("Gateway received ru.practicum.shareit.request to get all requests, userId={}, from={}, size={}", userId, from, size);
        EndpointObjectsValidator.checkUserIdForNullValue(userId, "получение всех запросов от пользователей");
        EndpointObjectsValidator.checkPaginationParameters(from, size);
        return itemRequestClient.getAll(userId, from, size);
    }

}