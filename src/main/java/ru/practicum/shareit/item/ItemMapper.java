package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.RestCommandObjectValidator;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

@Component
@RequiredArgsConstructor
public class ItemMapper {
    // В задании было указано написать мэпперы для Item и User самостоятельно, поэтому не использую mapstruct
    private final RestCommandObjectValidator validator;

    public Item fromRestCommand(ItemRestCommand itemRestCommand) {
        itemRestCommand = validator.validate(itemRestCommand);
        Item item = Item.builder()
                .ownerId(itemRestCommand.getOwnerId())
                .name(itemRestCommand.getName())
                .description(itemRestCommand.getDescription())
                .isAvailable(itemRestCommand.getAvailable())
                .rent(itemRestCommand.getRent())
                .itemRating(itemRestCommand.getItemRating())
                .itemPhotoUri(itemRestCommand.getItemPhotoUri())
                .postDate(itemRestCommand.getItemPostDate())
                .requestsWithUseIds(itemRestCommand.getRequestsWithUseIds())
                .build();
        item.setId(itemRestCommand.getId());
        return item;
    }

    public ItemRestView toRestView(Item item) {
        return ItemRestView.builder()
                .id(item.getId())
                .ownerId(item.getOwnerId())
                .name(item.getName())
                .description(item.getDescription())
                .isAvailable(item.getIsAvailable())
                .rent(item.getRent())
                .itemRating(item.getItemRating())
                .itemPhotoUri(item.getItemPhotoUri())
                .postDate(item.getPostDate())
                .requestsWithUseIds(item.getRequestsWithUseIds())
                .build();
    }

}