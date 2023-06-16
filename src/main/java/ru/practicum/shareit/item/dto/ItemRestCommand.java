package ru.practicum.shareit.item.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.PositiveOrZero;
import javax.validation.constraints.Size;

import java.net.URI;
import java.time.LocalDate;
import java.util.Set;

@NoArgsConstructor
@Getter
@Setter
public class ItemRestCommand {
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
    private float rent;
    private float itemRating;
    private URI itemPhotoUri;
    private LocalDate itemPostDate;
    private Set<Long> requestsWithUseIds;

}