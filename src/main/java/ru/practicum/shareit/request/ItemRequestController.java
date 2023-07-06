package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestParameterException;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;

import java.util.List;

@Validated
@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
public class ItemRequestController {
    private final ItemRequestService itemRequestService;

    @PostMapping
    public ItemRequestRestView save(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @RequestBody @Valid ItemRequestRestCommand itemRequestRestCommand) {
        checkUserIdForNullValue(userId, "сохранение");
        return itemRequestService.save(userId, itemRequestRestCommand);
    }

    @GetMapping
    public List<ItemRequestRestView> getAllRequestsOfRequester(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId) {
        checkUserIdForNullValue(userId, "получение всех запросов, оформленных пользователем");
        return itemRequestService.getAllRequestsOfRequester(userId);
    }

    @GetMapping("{request_id}")
    public ItemRequestRestView getById(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @PathVariable(name = "request_id") @Positive long requestId) {
        checkUserIdForNullValue(userId, "получение по идентификатору");
        return itemRequestService.getById(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestRestView> getAll(
            @RequestHeader(value = "X-Sharer-User-Id", defaultValue = "0") @PositiveOrZero long userId,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size) {
        checkUserIdForNullValue(userId, "получение всех запросов от пользователей");
        checkPaginationParameters(from, size);
        return itemRequestService.getAll(userId, from, size).toList();
    }

    private void checkUserIdForNullValue(long userId, String operation) {
        if (userId == 0L) {
            throw new BadRequestHeaderException(String.format("В заголовке запроса на проведение операции '%s' " +
                    "с данными объекта 'запрос' не передан идентификатор пользователя, либо указан 0", operation));
        }
    }

    private void checkPaginationParameters(int from, int size) {
        if (from < 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение порядкового номера " +
                    "первого отображаемого элемента '" + from + "'. Значение данного параметра не должно быть меньше 0");
        }
        if (size <= 0) {
            throw new BadRequestParameterException("В параметре запроса указано неверное значение количества " +
                    "отображаемых элементов, равное '" + size + "'. Значение данного параметра не должно быть меньше, чем 1");
        }
    }

}