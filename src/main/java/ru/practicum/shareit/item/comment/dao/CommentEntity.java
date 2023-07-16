package ru.practicum.shareit.item.comment.dao;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import javax.persistence.*;

import ru.practicum.shareit.common.UpdatableUserDependedEntity;
import ru.practicum.shareit.item.dao.ItemEntity;

@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "comments")
public class CommentEntity extends UpdatableUserDependedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comment_id")
    protected long id;
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    ItemEntity item;
    @Column(name = "user_name")
    @JoinColumn(referencedColumnName = "users.user_name")
    String authorName;
    @Column(name = "text")
    String text;

}