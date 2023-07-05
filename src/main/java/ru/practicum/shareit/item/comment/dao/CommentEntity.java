package ru.practicum.shareit.item.comment.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.practicum.shareit.common.UpdatableUserDependedEntity;
import ru.practicum.shareit.item.dao.ItemEntity;

import javax.persistence.*;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentEntity extends UpdatableUserDependedEntity {
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    ItemEntity item;
    @Column(name = "user_name")
    @JoinColumn(referencedColumnName = "users.user_name")
    String authorName;
    @Column(name = "text")
    String text;

}