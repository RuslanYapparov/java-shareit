package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.common.ObjectMapper;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;

@Component
public class ItemMapper implements ObjectMapper<Item, ItemRestCommand, ItemRestView> {
    // В задании было указано написать мэпперы для Item и User самостоятельно, поэтому не использую mapstruct

    @Override
    public Item fromRestCommand(ItemRestCommand itemRestCommand) {
        itemRestCommand.insertDefaultValuesInNullFields();
        Boolean isItemAvailable;
        try {
            isItemAvailable = itemRestCommand.getAvailable();
        } catch (NullPointerException exception) {  // Приходится идти на дополнительные издержки, чтобы обеспечить
            isItemAvailable = null;                 // Возможность обновления полей способом patch
        }
        Item item = Item.builder()
                .ownerId(itemRestCommand.getOwnerId())
                .name(itemRestCommand.getName())
                .description(itemRestCommand.getDescription())
                .isAvailable(isItemAvailable)
                .rent(itemRestCommand.getRent())
                .itemRating(itemRestCommand.getItemRating())
                .itemPhotoUri(itemRestCommand.getItemPhotoUri())
                .postDate(itemRestCommand.getItemPostDate())
                .requestsWithUseIds(itemRestCommand.getRequestsWithUseIds())
                .build();
        item.setId(itemRestCommand.getId());
        return item;
    }

    @Override
    public ItemRestView toRestView(Item item) {
        ItemRestView itemRestView = new ItemRestView();
        itemRestView.setId(item.getId());
        itemRestView.setOwnerId(item.getOwnerId());
        itemRestView.setName(item.getName());
        itemRestView.setDescription(item.getDescription());
        itemRestView.setAvailable(item.getIsAvailable());
        itemRestView.setRent(item.getRent());
        itemRestView.setItemRating(item.getItemRating());
        itemRestView.setItemPhotoUri(item.getItemPhotoUri());
        itemRestView.setPostDate(item.getPostDate());
        itemRestView.setRequestsWithUseIds(item.getRequestsWithUseIds());
        return itemRestView;
    }

}