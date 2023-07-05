package ru.practicum.shareit.user.dao;

import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.net.URI;
import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;
    @Column(name = "user_name", nullable = false)
    private String name;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    // Ниже поля, которые отсутствуют в задании и тестах, но которые, как мне кажется, должны быть в логике приложения
    @Embedded
    private UserAddressEntity address;
    @Column(name = "telephone_number")
    private String telephoneNumber;
    @Column(name = "telephone_visible")
    private boolean telephoneVisible;
    @Column(name = "avatar_uri")
    private URI avatarUri;
    @Column(name = "created")
    @CreationTimestamp
    private LocalDateTime created;
    @Column(name = "last_modified")
    @UpdateTimestamp
    private LocalDateTime lastModified;
    @Column(name = "user_rating")
    private float userRating;  // Наверное, пользователей (владельцев и арендаторов) нкжно оценивать другим пользовтелям

}