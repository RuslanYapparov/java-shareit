package ru.practicum.shareit_server.item.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ItemRepository extends JpaRepository<ItemEntity, Long> {

    Page<ItemEntity> findAllByUserId(long ownerId, Pageable page);

    @Query("select ie " +
            "from ItemEntity as ie " +
            "where ie.available = true and " +
            "(lower(ie.name) like lower(concat('%', ?1, '%')) or " +
            "lower(ie.description) like lower(concat('%', ?1, '%')))")
    Page<ItemEntity> findAllAvailableBySearchInNamesAndDescriptions(String text, Pageable page);

    void deleteAllByUserId(long ownerId);

}