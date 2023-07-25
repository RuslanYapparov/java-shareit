package ru.practicum.shareit_server.request;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import static org.junit.jupiter.api.Assertions.*;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.request.dao.ItemRequestEntity;
import ru.practicum.shareit_server.request.dao.ItemRequestRepository;
import ru.practicum.shareit_server.user.dao.UserEntity;

import java.util.List;

@DataJpaTest
public class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRequestRepository itemRequestRepository;

    private final UserEntity userEntity = initializeUserEntity("user_name", "user_email");
    private ItemRequestEntity firstItemRequestEntity;
    private ItemRequestEntity secondItemRequestEntity;

    @BeforeEach
    public void verifyBootstrappingByPersistingUserEntity() {
        assertNotNull(entityManager);
        assertEquals(0L, userEntity.getId());
        entityManager.persist(userEntity);
        assertNotEquals(0L, userEntity.getId());
    }

    @Test
    public void save_whenGetItemRequestEntityWithDescription_thenManagingThisEntity() {
        firstItemRequestEntity = initializeItemRequestEntity();
        secondItemRequestEntity = initializeItemRequestEntity();
        assertEquals(0L, firstItemRequestEntity.getId());
        assertEquals(0L, secondItemRequestEntity.getId());
        itemRequestRepository.save(firstItemRequestEntity);
        itemRequestRepository.save(secondItemRequestEntity);
        assertNotEquals(0L, firstItemRequestEntity.getId());
        assertNotEquals(0L, secondItemRequestEntity.getId());
        assertTrue(secondItemRequestEntity.getCreated().isAfter(firstItemRequestEntity.getCreated()));
    }

    @Test
    public void save_whenGetItemRequestEntityWithNullUserId_thenThrowException() {
        firstItemRequestEntity = initializeItemRequestEntity();
        firstItemRequestEntity.setUserId(0);
        assertThrows(DataIntegrityViolationException.class,
                () -> itemRequestRepository.save(firstItemRequestEntity));
    }

    @Test
    public void save_whenGetItemRequestEntityWithNullDescription_thenThrowException() {
        firstItemRequestEntity = initializeItemRequestEntity();
        firstItemRequestEntity.setDescription(null);
        assertThrows(DataIntegrityViolationException.class,
                () -> itemRequestRepository.save(firstItemRequestEntity));
    }

    @Test
    public void findAllByUserIdOrderByCreatedDesc_whenGetUserAndAnotherUserId_thenReturnListOfItemRequestEntities() {
        firstItemRequestEntity = initializeItemRequestEntity();
        secondItemRequestEntity = initializeItemRequestEntity();
        itemRequestRepository.save(firstItemRequestEntity);
        itemRequestRepository.save(secondItemRequestEntity);

        List<ItemRequestEntity> itemRequestEntitiesForUser =
                itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userEntity.getId());
        List<ItemRequestEntity> itemRequestEntitiesForAnotherUser =
                itemRequestRepository.findAllByUserIdOrderByCreatedDesc(userEntity.getId() + 1);

        assertEquals(2, itemRequestEntitiesForUser.size());
        assertEquals(secondItemRequestEntity, itemRequestEntitiesForUser.get(0));
        assertEquals(firstItemRequestEntity, itemRequestEntitiesForUser.get(1));
        assertTrue(itemRequestEntitiesForAnotherUser.isEmpty());
    }

    @Test
    public void findAllWithoutUsersRequests_whenGetUserAndAnotherUserId_thenReturnListOfItemRequestEntities() {
        firstItemRequestEntity = initializeItemRequestEntity();
        secondItemRequestEntity = initializeItemRequestEntity();
        itemRequestRepository.save(firstItemRequestEntity);
        itemRequestRepository.save(secondItemRequestEntity);
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "created"));

        List<ItemRequestEntity> itemRequestEntitiesForUser =
                itemRequestRepository.findAllWithoutUsersRequests(userEntity.getId(), page).toList();
        List<ItemRequestEntity> itemRequestEntitiesForAnotherUser =
                itemRequestRepository.findAllWithoutUsersRequests(userEntity.getId() + 1, page).toList();

        assertTrue(itemRequestEntitiesForUser.isEmpty());
        assertEquals(2, itemRequestEntitiesForAnotherUser.size());
        assertEquals(secondItemRequestEntity, itemRequestEntitiesForAnotherUser.get(0));
        assertEquals(firstItemRequestEntity, itemRequestEntitiesForAnotherUser.get(1));

        UserEntity anotherUserEntity = entityManager.persist(
                initializeUserEntity("another_user_name", "another_user_email"));
        ItemRequestEntity anotherItemRequest = initializeItemRequestEntity();
        anotherItemRequest.setUserId(anotherUserEntity.getId());
        anotherItemRequest.setDescription("another description");

        itemRequestRepository.save(anotherItemRequest);
        itemRequestEntitiesForUser = itemRequestRepository
                .findAllWithoutUsersRequests(userEntity.getId(), page).toList();
        assertFalse(itemRequestEntitiesForUser.isEmpty());
        assertEquals(1, itemRequestEntitiesForUser.size());
        assertEquals("another description", itemRequestEntitiesForUser.get(0).getDescription());
    }

    private ItemRequestEntity initializeItemRequestEntity() {
        ItemRequestEntity itemRequestEntity = new ItemRequestEntity();
        itemRequestEntity.setUserId(userEntity.getId());
        itemRequestEntity.setDescription("description");
        itemRequestEntity.setItems(List.of(new ItemEntity()));
        return itemRequestEntity;
    }

    private UserEntity initializeUserEntity(String name, String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(name);
        userEntity.setEmail(email);
        return userEntity;
    }

}