package ru.practicum.shareit.request;

import ru.practicum.shareit.common.CrudService;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;

import java.util.List;

public interface ItemRequestService extends CrudService<ItemRequestRestCommand, ItemRequestRestView> {

    List<ItemRequestRestView> getAllRequestsOfRequester(long requesterId);

}