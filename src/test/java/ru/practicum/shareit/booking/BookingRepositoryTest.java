package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import ru.practicum.shareit.booking.dao.BookingEntity;
import ru.practicum.shareit.booking.dao.BookingRepository;
import ru.practicum.shareit.item.dao.ItemEntity;
import ru.practicum.shareit.user.dao.UserEntity;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class BookingRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private BookingRepository bookingRepository;
    private final Pageable page = PageRequest.of(0, 10, Sort.by(Sort.Direction.DESC, "start"));
    private final UserEntity userEntity = initializeUserEntity();
    private ItemEntity itemEntity = initializeItemEntity();
    private BookingEntity firstBookingEntity;
    private BookingEntity secondBookingEntity;

    @BeforeEach
    public void verifyBootstrappingByPersistingUserEntity() {
        assertNotNull(entityManager);
        assertEquals(0L, userEntity.getId());
        assertEquals(0L, itemEntity.getId());
        entityManager.persist(userEntity);
        itemEntity.setUserId(userEntity.getId());
        entityManager.persist(itemEntity);
        assertNotEquals(0L, userEntity.getId());
        assertNotEquals(0L, itemEntity.getId());
    }

    @Test
    public void save_whenGetBookingEntity_thenManagingThisEntity() {
        firstBookingEntity = initializeBookingEntity();
        secondBookingEntity = initializeBookingEntity();
        assertEquals(0L, firstBookingEntity.getId());
        assertEquals(0L, secondBookingEntity.getId());
        bookingRepository.save(firstBookingEntity);
        bookingRepository.save(secondBookingEntity);
        assertNotEquals(0L, firstBookingEntity.getId());
        assertNotEquals(0L, secondBookingEntity.getId());
        assertTrue(secondBookingEntity.getCreated().isAfter(firstBookingEntity.getCreated()));
    }

    @Test
    public void save_whenGetBookingEntityWithNullUserId_thenThrowException() {
        firstBookingEntity = initializeBookingEntity();
        firstBookingEntity.setUserId(0);
        assertThrows(DataIntegrityViolationException.class, () -> bookingRepository.save(firstBookingEntity));
    }

    @Test
    public void findAllByUserIdAndFindAllByItemUserId_whenGetUserAndAnotherUserId_thenReturnPageOfBookingEntities() {
        firstBookingEntity = initializeBookingEntity();
        secondBookingEntity = initializeBookingEntity();
        bookingRepository.save(firstBookingEntity);
        secondBookingEntity.setStart(LocalDateTime.now().plusSeconds(10));
        secondBookingEntity.setEnd(LocalDateTime.now().plusSeconds(17));
        bookingRepository.save(secondBookingEntity);

        List<BookingEntity> bookingEntitiesForUser =
                bookingRepository.findAllByUserId(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForAnotherUser =
                bookingRepository.findAllByUserId(userEntity.getId() + 1, page).toList();

        assertEquals(2, bookingEntitiesForUser.size());
        assertEquals(firstBookingEntity, bookingEntitiesForUser.get(1));
        assertEquals(secondBookingEntity, bookingEntitiesForUser.get(0));
        assertTrue(bookingEntitiesForAnotherUser.isEmpty());

        List<BookingEntity> bookingEntitiesForOwner =
                bookingRepository.findAllByItemUserId(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForNotOwner =
                bookingRepository.findAllByItemUserId(userEntity.getId() + 1, page).toList();

        assertEquals(2, bookingEntitiesForOwner.size());
        assertEquals(firstBookingEntity, bookingEntitiesForOwner.get(1));
        assertEquals(secondBookingEntity, bookingEntitiesForOwner.get(0));
        assertTrue(bookingEntitiesForNotOwner.isEmpty());
    }

    @Test
    public void findAllCurrentBookingsForUserByIdAndForItemOwnerById_whenCalled_thenReturnPageOfBookingEntities() {
        firstBookingEntity = initializeBookingEntity();
        secondBookingEntity = initializeBookingEntity();
        firstBookingEntity.setStart(LocalDateTime.now().plusNanos(50));
        bookingRepository.save(firstBookingEntity);
        secondBookingEntity.setStart(LocalDateTime.now().plusSeconds(10));
        secondBookingEntity.setEnd(LocalDateTime.now().plusSeconds(17));
        bookingRepository.save(secondBookingEntity);

        List<BookingEntity> bookingEntitiesForUser =
                bookingRepository.findAllCurrentBookingsForUserById(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForAnotherUser =
                bookingRepository.findAllCurrentBookingsForUserById(userEntity.getId() + 1, page).toList();

        assertEquals(1, bookingEntitiesForUser.size());
        assertEquals(firstBookingEntity, bookingEntitiesForUser.get(0));
        assertTrue(bookingEntitiesForAnotherUser.isEmpty());

        List<BookingEntity> bookingEntitiesForOwner =
                bookingRepository.findAllCurrentBookingsForItemOwnerById(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForNotOwner =
                bookingRepository.findAllCurrentBookingsForItemOwnerById(userEntity.getId() + 1, page).toList();

        assertEquals(1, bookingEntitiesForOwner.size());
        assertEquals(firstBookingEntity, bookingEntitiesForOwner.get(0));
        assertTrue(bookingEntitiesForNotOwner.isEmpty());
    }

    @Test
    public void findAllPastBookingsForUserByIdAndForItemOwnerById_whenCalled_thenReturnPageOfBookingEntities() {
        firstBookingEntity = initializeBookingEntity();
        secondBookingEntity = initializeBookingEntity();
        firstBookingEntity.setStart(LocalDateTime.now());
        firstBookingEntity.setEnd(LocalDateTime.now());
        bookingRepository.save(firstBookingEntity);
        secondBookingEntity.setStart(LocalDateTime.now().plusSeconds(10));
        secondBookingEntity.setEnd(LocalDateTime.now().plusSeconds(17));
        bookingRepository.save(secondBookingEntity);

        List<BookingEntity> bookingEntitiesForUser =
                bookingRepository.findAllPastBookingsForUserById(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForAnotherUser =
                bookingRepository.findAllPastBookingsForUserById(userEntity.getId() + 1, page).toList();

        assertEquals(1, bookingEntitiesForUser.size());
        assertEquals(firstBookingEntity, bookingEntitiesForUser.get(0));
        assertTrue(bookingEntitiesForAnotherUser.isEmpty());

        List<BookingEntity> bookingEntitiesForOwner =
                bookingRepository.findAllPastBookingsForItemOwnerById(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForNotOwner =
                bookingRepository.findAllPastBookingsForItemOwnerById(userEntity.getId() + 1, page).toList();

        assertEquals(1, bookingEntitiesForOwner.size());
        assertEquals(firstBookingEntity, bookingEntitiesForOwner.get(0));
        assertTrue(bookingEntitiesForNotOwner.isEmpty());
    }

    @Test
    public void findAllFutureBookingsForUserByIdAndForItemOwnerById_whenCalled_thenReturnPageOfBookingEntities() {
        firstBookingEntity = initializeBookingEntity();
        secondBookingEntity = initializeBookingEntity();
        firstBookingEntity.setStart(LocalDateTime.now());
        bookingRepository.save(firstBookingEntity);
        secondBookingEntity.setStart(LocalDateTime.now().plusSeconds(10));
        secondBookingEntity.setEnd(LocalDateTime.now().plusSeconds(17));
        bookingRepository.save(secondBookingEntity);

        List<BookingEntity> bookingEntitiesForUser =
                bookingRepository.findAllFutureBookingsForUserById(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForAnotherUser =
                bookingRepository.findAllFutureBookingsForUserById(userEntity.getId() + 1, page).toList();

        assertEquals(1, bookingEntitiesForUser.size());
        assertEquals(secondBookingEntity, bookingEntitiesForUser.get(0));
        assertTrue(bookingEntitiesForAnotherUser.isEmpty());

        List<BookingEntity> bookingEntitiesForOwner =
                bookingRepository.findAllFutureBookingsForItemOwnerById(userEntity.getId(), page).toList();
        List<BookingEntity> bookingEntitiesForNotOwner =
                bookingRepository.findAllFutureBookingsForItemOwnerById(userEntity.getId() + 1, page).toList();

        assertEquals(1, bookingEntitiesForOwner.size());
        assertEquals(secondBookingEntity, bookingEntitiesForOwner.get(0));
        assertTrue(bookingEntitiesForNotOwner.isEmpty());
    }

    @Test
    public void findAllByUserAndByItemOwnerIdAndStatus_whenCalled_thenReturnPageOfBookingEntities() {
        firstBookingEntity = initializeBookingEntity();
        secondBookingEntity = initializeBookingEntity();
        bookingRepository.save(firstBookingEntity);
        bookingRepository.save(secondBookingEntity);

        List<BookingEntity> bookingEntitiesForUser =
                bookingRepository.findAllByUserIdAndStatus(userEntity.getId(), "WAITING", page).toList();
        List<BookingEntity> bookingEntitiesForAnotherUser =
                bookingRepository.findAllByUserIdAndStatus(userEntity.getId() + 1, "WAITING", page).toList();

        assertEquals(2, bookingEntitiesForUser.size());
        assertEquals(secondBookingEntity, bookingEntitiesForUser.get(0));
        assertEquals(firstBookingEntity, bookingEntitiesForUser.get(1));
        assertTrue(bookingEntitiesForAnotherUser.isEmpty());

        List<BookingEntity> bookingEntitiesForOwner =
                bookingRepository.findAllByItemUserIdAndStatus(userEntity.getId(), "WAITING", page).toList();
        List<BookingEntity> bookingEntitiesForNotOwner =
                bookingRepository.findAllByItemUserIdAndStatus(userEntity.getId() + 1, "WAITING", page).toList();

        assertEquals(2, bookingEntitiesForOwner.size());
        assertEquals(secondBookingEntity, bookingEntitiesForOwner.get(0));
        assertEquals(firstBookingEntity, bookingEntitiesForOwner.get(1));
        assertTrue(bookingEntitiesForNotOwner.isEmpty());

        firstBookingEntity.setStatus("APPROVED");
        bookingRepository.save(firstBookingEntity);

        bookingEntitiesForUser = bookingRepository
                .findAllByUserIdAndStatus(userEntity.getId(), "WAITING", page).toList();
        bookingEntitiesForAnotherUser = bookingRepository
                .findAllByUserIdAndStatus(userEntity.getId() + 1, "WAITING", page).toList();

        assertEquals(1, bookingEntitiesForUser.size());
        assertEquals(secondBookingEntity, bookingEntitiesForUser.get(0));
        assertTrue(bookingEntitiesForAnotherUser.isEmpty());

        bookingEntitiesForOwner = bookingRepository
                .findAllByItemUserIdAndStatus(userEntity.getId(), "WAITING", page).toList();
        bookingEntitiesForNotOwner = bookingRepository
                .findAllByItemUserIdAndStatus(userEntity.getId() + 1, "WAITING", page).toList();

        assertEquals(1, bookingEntitiesForOwner.size());
        assertEquals(secondBookingEntity, bookingEntitiesForOwner.get(0));
        assertTrue(bookingEntitiesForNotOwner.isEmpty());
    }


    private UserEntity initializeUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setName("user_name");
        userEntity.setEmail("email");
        return userEntity;
    }

    private ItemEntity initializeItemEntity() {
        ItemEntity itemEntity = new ItemEntity();
        itemEntity.setName("item_name");
        itemEntity.setDescription("item_description");
        itemEntity.setAvailable(true);
        return itemEntity;
    }

    private BookingEntity initializeBookingEntity() {
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setUserId(userEntity.getId());
        bookingEntity.setStatus("WAITING");
        bookingEntity.setStart(LocalDateTime.now().plusSeconds(2));
        bookingEntity.setEnd(LocalDateTime.now().plusSeconds(5));
        bookingEntity.setItem(itemEntity);
        return bookingEntity;
    }

}