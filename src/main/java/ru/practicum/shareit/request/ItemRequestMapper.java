package ru.practicum.shareit.request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import ru.practicum.shareit.common.ObjectMapper;
import ru.practicum.shareit.item.dao.ItemEntity;
import ru.practicum.shareit.request.dao.ItemRequestEntity;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;
import ru.practicum.shareit.request.dto.RequestedItem;

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