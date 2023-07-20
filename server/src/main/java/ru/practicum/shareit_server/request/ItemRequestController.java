package ru.practicum.shareit_server.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestRestView save(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestBody ItemRequestRestCommand itemRequestRestCommand) {
        return itemRequestService.save(userId, itemRequestRestCommand);
    }

    @GetMapping
    public List<ItemRequestRestView> getAllRequestsOfRequester(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId) {
        return itemRequestService.getAllRequestsOfRequester(userId);
    }

    @GetMapping("{request_id}")
    public ItemRequestRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @PathVariable(name = "request_id") long requestId) {
        return itemRequestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        return itemRequestService.getAll(userId, from, size).toList();
    }

}