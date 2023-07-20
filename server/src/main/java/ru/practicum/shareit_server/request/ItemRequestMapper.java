package ru.practicum.shareit_server.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.shareit_server.common.ObjectMapper;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.request.dao.ItemRequestEntity;
import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;
import ru.practicum.shareit_server.request.dto.RequestedItem;

@Mapper(componentModel = "spring")
public interface ItemRequestMapper
        extends ObjectMapper<ItemRequestEntity, ItemRequest, ItemRequestRestCommand, ItemRequestRestView> {

    @Override
    @Mapping(target = "userId", source = "requesterId")
    ItemRequestEntity toDbEntity(ItemRequest itemRequest);

    @Override
    @Mapping(target = "requesterId", source = "userId")
    ItemRequest fromDbEntity(ItemRequestEntity itemRequestEntity);

    @Mapping(target = "requestId", expression = "java(itemEntity.getRequest().getId())")
    RequestedItem itemEntityToRequestedItem(ItemEntity itemEntity);

}