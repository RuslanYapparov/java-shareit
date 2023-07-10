package ru.practicum.shareit.request.dao;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import ru.practicum.shareit.common.UpdatableUserDependedEntity;
import ru.practicum.shareit.item.dao.ItemEntity;

import javax.persistence.*;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "requests")
public class ItemRequestEntity extends UpdatableUserDependedEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "request_id")
    private long id;
    @Column(name = "description")
    String description;
    @OneToMany(mappedBy = "request")
    List<ItemEntity> items;

}
