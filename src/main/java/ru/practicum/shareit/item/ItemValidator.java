package ru.practicum.shareit.item;

import org.springframework.stereotype.Component;

import ru.practicum.shareit.common.DomainObjectValidator;

@Component
public class ItemValidator implements DomainObjectValidator<Item> {

    @Override
    public Item validateAndAssignNullFields(Item item) {
        String type = "вещь";
        return Item.builder()
                .id(checkLongField(type, "id", item.getId()))
                .userId(checkLongField(type, "owner_id", item.getUserId()))
                .name(checkStringField(type, "name", item.getName()))
                .description(checkStringField(type, "description", item.getDescription()))
                .isAvailable(item.getIsAvailable())
                .rent(checkFloatField(type, "rent", item.getRent()))
                .itemRating(item.getItemRating())
                .itemPhotoUri(checkUriField(type, "photo_uri", item.getItemPhotoUri()))
                .build();
    }

}
