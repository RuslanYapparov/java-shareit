package ru.practicum.shareit.common;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import javax.persistence.*;
import java.time.LocalDateTime;

@MappedSuperclass
@EqualsAndHashCode
@Getter
@Setter
public abstract class UpdatableUserDependedEntity {
    @Column(name = "user_id")
    @JoinColumn(referencedColumnName = "users.user_id")
    protected long userId;
    @Column(name = "created")
    @CreationTimestamp
    protected LocalDateTime created;
    @Column(name = "last_modified")
    @CreationTimestamp
    protected LocalDateTime lastModified;

    protected abstract long getId();

}