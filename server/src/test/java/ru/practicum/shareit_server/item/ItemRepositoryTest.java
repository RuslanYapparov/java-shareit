package ru.practicum.shareit_server.item;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit_server.item.dao.ItemEntity;
import ru.practicum.shareit_server.item.dao.ItemRepository;
import ru.practicum.shareit_server.user.dao.UserEntity;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ItemRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private ItemRepository itemRepository;

    private final UserEntity userEntity = initializeUserEntity();
    private ItemEntity firstItemEntity;
    private ItemEntity secondItemEntity;

    @BeforeEach
    public void verifyBootstrappingByPersistingUserEntity() {
        assertNotNull(entityManager);
        assertEquals(0L, userEntity.getId());
        entityManager.persist(userEntity);
        assertNotEquals(0L, userEntity.getId());
    }

    @Test
    public void save_whenGetItemEntity_thenManagingThisEntity() {
        firstItemEntity = initializeMinimalItemEntity();
        secondItemEntity = initializeMinimalItemEntity();
        assertEquals(0L, firstItemEntity.getId());
        assertEquals(0L, secondItemEntity.getId());
        itemRepository.save(firstItemEntity);
        itemRepository.save(secondItemEntity);
        assertNotEquals(0L, firstItemEntity.getId());
        assertNotEquals(0L, secondItemEntity.getId());
        assertTrue(secondItemEntity.getCreated().isAfter(firstItemEntity.getCreated()));
    }

    @Test
    public void save_whenGetItemEntityWithNullUserId_thenThrowException() {
        firstItemEntity = initializeMinimalItemEntity();
        firstItemEntity.setUserId(0);
        assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(firstItemEntity));
    }

    @Test
    public void save_whenGetItemEntityWithNullName_thenThrowException() {
        firstItemEntity = initializeMinimalItemEntity();
        firstItemEntity.setName(null);
        assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(firstItemEntity));
    }

    @Test
    public void save_whenGetItemEntityWithNullDescription_thenThrowException() {
        firstItemEntity = initializeMinimalItemEntity();
        firstItemEntity.setDescription(null);
        assertThrows(DataIntegrityViolationException.class, () -> itemRepository.save(firstItemEntity));
    }

    @Test
    public void findAllByUserId_whenGetUserAndAnotherUserId_thenReturnListOfItemEntities() {
        firstItemEntity = initializeMinimalItemEntity();
        secondItemEntity = initializeMinimalItemEntity();
        itemRepository.save(firstItemEntity);
        itemRepository.save(secondItemEntity);
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<ItemEntity> itemEntitiesForUser =
                itemRepository.findAllByUserId(userEntity.getId(), page).toList();
        List<ItemEntity> itemEntitiesForAnotherUser =
                itemRepository.findAllByUserId(userEntity.getId() + 1, page).toList();

        assertEquals(2, itemEntitiesForUser.size());
        assertEquals(firstItemEntity, itemEntitiesForUser.get(0));
        assertEquals(secondItemEntity, itemEntitiesForUser.get(1));
        assertTrue(itemEntitiesForAnotherUser.isEmpty());
    }

    @Test
    public void findAllAvailableBySearchInNamesAndDescriptions_whenGetUserId_thenReturnListOfItemEntities() {
        firstItemEntity = initializeMinimalItemEntity();
        secondItemEntity = initializeMinimalItemEntity();
        itemRepository.save(firstItemEntity);
        itemRepository.save(secondItemEntity);
        Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.ASC, "id"));

        List<ItemEntity> itemEntities =
                itemRepository.findAllAvailableBySearchInNamesAndDescriptions("item", page).toList();

        assertEquals(2, itemEntities.size());
        assertEquals(firstItemEntity, itemEntities.get(0));
        assertEquals(secondItemEntity, itemEntities.get(1));

        secondItemEntity.setAvailable(false);
        itemEntities = itemRepository.findAllAvailableBySearchInNamesAndDescriptions("Item", page).toList();
        assertFalse(itemEntities.isEmpty());
        assertEquals(1, itemEntities.size());
        assertEquals("item_description", itemEntities.get(0).getDescription());
    }

    @Test
    public void deleteAllByUserId_whenGetExistingAndNotExistingUserId_thenDeleteOrNotDeleteItemEntities() {
        firstItemEntity = initializeMinimalItemEntity();
        secondItemEntity = initializeMinimalItemEntity();
        itemRepository.save(firstItemEntity);
        itemRepository.save(secondItemEntity);

        itemRepository.deleteAllByUserId(userEntity.getId() + 1);
        List<ItemEntity> itemEntities = itemRepository.findAll();
        assertFalse(itemEntities.isEmpty());
        assertEquals(2, itemEntities.size());

        itemRepository.deleteAllByUserId(userEntity.getId());
        itemEntities = itemRepository.findAll();
        assertTrue(itemEntities.isEmpty());
    }

    private UserEntity initializeUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user_name");
        userEntity.setEmail("email");
        return userEntity;
    }

    private ItemEntity initializeMinimalItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setUserId(userEntity.getId());
        itemEntity.setName("item_name");
        itemEntity.setDescription("item_description");
        itemEntity.setAvailable(true);
        return itemEntity;
    }

}