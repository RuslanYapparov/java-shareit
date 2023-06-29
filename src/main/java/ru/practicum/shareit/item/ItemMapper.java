package ru.practicum.shareit.item;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;
import ru.practicum.shareit.common.ObjectMapper;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dao.CommentEntity;
import ru.practicum.shareit.item.dao.ItemEntity;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ItemMapper extends ObjectMapper<ItemEntity, Item, ItemRestCommand, ItemRestView> {

    @Override
    @Mapping(target = "isAvailable", expression = "java(itemRestCommand.getAvailable())")
    @Mapping(target = "userId", expression = "java(itemRestCommand.getOwnerId())")
    Item fromRestCommand(ItemRestCommand itemRestCommand);

    @Override
    @Mapping(target = "ownerId", expression = "java(item.getUserId())")
    ItemRestView toRestView(Item item);

    @Override
    @Mapping(target = "available", expression = "java(item.getIsAvailable())")
    ItemEntity toDbEntity(Item item);

    @Override
    @Mapping(target = "isAvailable", expression = "java(itemEntity.isAvailable())")
    @Mapping(target = "comments", source = "itemComments", qualifiedByName = "mapListOfComments")
    Item fromDbEntity(ItemEntity itemEntity);

    @Named("mapListOfComments")
    default List<Comment> mapListOfComments(List<CommentEntity> commentEntities) {
        if (commentEntities == null || commentEntities.isEmpty()) {
            return new ArrayList<>();
        }
        return commentEntities.stream()
                .map(commentEntity -> new Comment(commentEntity.getId(),
                        commentEntity.getUserId(),
                        commentEntity.getAuthorName(),
                        commentEntity.getText(),
                        commentEntity.getCreated(),
                        commentEntity.getLastModified()))
                .collect(Collectors.toList());
    }

}