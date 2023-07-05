package ru.practicum.shareit.common;

import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@Getter
@Setter
public abstract class UpdatableUserDependedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    protected long id;
    @Column(name = "user_id")
    @JoinColumn(referencedColumnName = "users.user_id")
    protected long userId;
    @Column(name = "created")
    @CreationTimestamp
    protected LocalDateTime created;
    @Column(name = "last_modified")
    @UpdateTimestamp
    protected LocalDateTime lastModified;

}