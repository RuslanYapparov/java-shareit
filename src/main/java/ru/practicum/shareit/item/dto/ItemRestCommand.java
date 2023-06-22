package ru.practicum.shareit.item.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@Value
@Builder(toBuilder = true)
public class ItemRestCommand {
    @PositiveOrZero
    long id;
    @PositiveOrZero
    long ownerId;
    @NotNull
    @NotBlank
    @Size(max = 100)
    String name;
    @NotNull
    @NotBlank
    @Size(max = 1000)
    String description;
    @NotNull
    Boolean available;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    float rent;
    float itemRating;
    URI itemPhotoUri;
    LocalDate itemPostDate;
    Set<Long> requestsWithUseIds;

}