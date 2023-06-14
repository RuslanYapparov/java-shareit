package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import ru.practicum.shareit.common.Receivable;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.BadRequestBodyException;
import ru.practicum.shareit.exception.BadRequestHeaderException;
import ru.practicum.shareit.exception.BadRequestParameterException;

import java.net.URI;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@NoArgsConstructor
@Getter
public class ItemRestCommand implements Receivable<ItemRestCommand> {
    @PositiveOrZero
    private long id;
    @PositiveOrZero
    @Setter
    private long ownerId;
    @NotNull
    @NotBlank
    @Size(max = 100)
    private String name;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    private String description;
    @NotNull
    private Boolean available;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    float rent;
    float itemRating;
    URI itemPhotoUri;
    LocalDate itemPostDate;
    Set<Long> requestsWithUseIds;

    @Override
    public ItemRestCommand insertDefaultValuesInNullFields()
            throws BadRequestParameterException, BadRequestHeaderException, BadRequestBodyException {
        checkFieldsForDefaultAndIncorrectValues();
        name = (name == null) ? ShareItConstants.NOT_ASSIGNED : name;
        description = (description == null) ? ShareItConstants.NOT_ASSIGNED : description;
        itemPhotoUri = (itemPhotoUri == null) ? ShareItConstants.DEFAULT_URI : itemPhotoUri;
        itemPostDate = (itemPostDate == null) ? ShareItConstants.DEFAULT_DATE : itemPostDate;
        requestsWithUseIds = (requestsWithUseIds == null) ? new HashSet<>() : requestsWithUseIds;
        return this;
    }

    @Override
    public void checkFieldsForDefaultAndIncorrectValues()
            throws BadRequestParameterException, BadRequestHeaderException, BadRequestBodyException {
        if (id < 0) {
            throw new BadRequestParameterException("Указан неверный идентификатор вещи " +
                    "в HTTP-запросе - идентификатор должен быть положительным числом");
        }
        if (ownerId < 0) {
            throw new BadRequestHeaderException("Указан неверный идентификатор хозяина вещи " +
                    "в HTTP-запросе - идентификатор должен быть положительным числом");
        }
        if (name != null && name.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить вещь с именем '%s'. " +
                    "Данное обозначение зарезервино системой", name));
        }
        if (description != null && description.equals(ShareItConstants.NOT_ASSIGNED)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить вещь с описанием " +
                    "'%s'. Данное обозначение зарезервино системой", description));
        }
        if (itemPhotoUri != null && itemPhotoUri.equals(ShareItConstants.DEFAULT_URI)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить вещь с указанным URI " +
                    "фотографии '%s'. Данное обозначение зарезервино системой", itemPhotoUri));
        }
        if (itemPostDate != null && itemPostDate.equals(ShareItConstants.DEFAULT_DATE)) {
            throw new BadRequestBodyException(String.format("Невозможно сохранить вещь с указанной датой " +
                            "регистрации '%s'. Данное обозначение зарезервино системой",
                    itemPostDate.format(ShareItConstants.DATE_FORMATTER)));
        }
    }

}