package ru.practicum.shareit_server.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.practicum.shareit_server.common.ObjectMapper;
import ru.practicum.shareit_server.item.comment.Comment;
import ru.practicum.shareit_server.item.comment.dao.CommentEntity;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper extends ObjectMapper<ItemEntity, Item, ItemRestCommand, ItemRestView> {

    @Override
    @Mapping(target = "userId", source = "ownerId")
    @Mapping(target = "itemPhotoUri", expression = "java(item.getItemPhotoUri().getPath())")
    ItemEntity toDbEntity(Item item);

    @Override
    @Mapping(target = "comments", source = "comments", qualifiedByName = "mapListOfComments")
    @Mapping(target = "itemPhotoUri", expression = "java(java.net.URI.create(itemEntity.getItemPhotoUri()))")
    @Mapping(target = "ownerId", source = "userId")
    @Mapping(target = "requestId",
            expression = "java(itemEntity.getRequest() == null ? 0L : itemEntity.getRequest().getId())")
    Item fromDbEntity(ItemEntity itemEntity);

    @Named("mapListOfComments")
    default List<Comment> mapListOfComments(List<CommentEntity> commentEntities) {
        if (commentEntities == null || commentEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return commentEntities.stream()
                .map(commentEntity -> Comment.builder()
                        .id(commentEntity.getId())
                        .authorId(commentEntity.getUserId())
                        .authorName(commentEntity.getAuthorName())
                        .text(commentEntity.getText())
                        .created(commentEntity.getCreated())
                        .lastModified(commentEntity.getLastModified())
                        .build())
                .collect(Collectors.toList());
    }

}