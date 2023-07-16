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
                .ownerId(checkLongField(type, "owner_id", item.getOwnerId()))
                .requestId(checkLongField(type, "request_id", item.getRequestId()))
                .name(checkStringField(type, "name", item.getName()))
                .description(checkStringField(type, "description", item.getDescription()))
                .available(item.getAvailable())
                .rent(checkFloatField(type, "rent", item.getRent()))
                .itemRating(item.getItemRating())
                .itemPhotoUri(checkUriField(type, "photo_uri", item.getItemPhotoUri()))
                .build();
    }

}
