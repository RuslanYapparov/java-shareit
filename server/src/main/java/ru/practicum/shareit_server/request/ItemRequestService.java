package ru.practicum.shareit_server.request;

import ru.practicum.shareit_server.common.CrudService;
import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;

import java.util.List;

public interface ItemRequestService extends CrudService<ItemRequestRestCommand, ItemRequestRestView> {

    List<ItemRequestRestView> getAllRequestsOfRequester(long requesterId);

}