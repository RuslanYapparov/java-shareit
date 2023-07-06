package ru.practicum.shareit.request.dao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ItemRequestRepository extends JpaRepository<ItemRequestEntity, Long> {

    List<ItemRequestEntity> findAllByUserIdOrderByCreatedDesc(long userId);

    @Query("select ire " +
            "from ItemRequestEntity as ire " +
            "where ire.userId != ?1")
    Page<ItemRequestEntity> findAllWithoutUsersRequests(long userId, Pageable page);

}