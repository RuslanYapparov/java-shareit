package ru.practicum.shareit.item.dao;

import lombok.*;
import javax.persistence.*;

import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.common.UpdatableUserDependedEntity;
import ru.practicum.shareit.item.comment.dao.CommentEntity;
import ru.practicum.shareit.request.dao.ItemRequestEntity;

import java.util.List;

@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@Getter
@Setter
@Entity
@Table(name = "items")
public class ItemEntity extends UpdatableUserDependedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private long id;
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
    private String itemPhotoUri;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "request_id")
    private ItemRequestEntity request;
    @OneToMany(mappedBy = "item")
    private List<BookingEntity> itemBookings;
    @OneToMany(mappedBy = "item")
    private List<CommentEntity> comments;

}