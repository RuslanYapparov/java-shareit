package ru.practicum.shareit.item.dao;

import lombok.*;
import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.common.UpdatableUserDependedEntity;
import ru.practicum.shareit.item.comment.dao.CommentEntity;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "items")
public class ItemEntity extends UpdatableUserDependedEntity {
    @Column(name = "item_name", nullable = false)
    private String name;
    @Column(name = "description", nullable = false)
    private String description;
    private boolean available;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    private float rent;
    @Column(name = "item_rating")
    private float itemRating;
    @Column(name = "item_photo_uri")
    private URI itemPhotoUri;
    @OneToMany(mappedBy = "item")
    private List<BookingEntity> itemBookings;
    @OneToMany(mappedBy = "item")
    private List<CommentEntity> itemComments;

}