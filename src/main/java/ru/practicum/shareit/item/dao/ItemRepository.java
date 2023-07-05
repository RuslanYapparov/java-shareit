package ru.practicum.shareit.item.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ItemRepository extends JpaRepository<ItemEntity, Long> {


    List<ItemEntity> findAllByUserId(long ownerId);

    void deleteAllByUserId(long ownerId);

    List<ItemEntity> findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseOrderByIdAsc(
            String nameSearch, String descriptionSearch);

}