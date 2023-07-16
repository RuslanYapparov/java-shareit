package ru.practicum.shareit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import ru.practicum.shareit.user.dao.UserEntity;
import ru.practicum.shareit.user.dao.UserRepository;
import ru.practicum.shareit.user.dao.UserShort;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class UserRepositoryTest {
    @Autowired
    private TestEntityManager entityManager;
    @Autowired
    private UserRepository userRepository;

    private final UserEntity firstUserEntity = initializeUserEntity("user_name", "user_email");
    private final UserEntity secondUserEntity = initializeUserEntity("user_name", "email_user");


    @BeforeEach
    public void verifyBootstrappingByPersistingUserEntity() {
        assertNotNull(entityManager);
        assertEquals(0L, firstUserEntity.getId());
        entityManager.persist(firstUserEntity);
        assertNotEquals(0L, firstUserEntity.getId());
    }

    @Test
    public void save_whenGetUserEntity_thenManagingThisEntity() {
        assertEquals(0L, secondUserEntity.getId());
        userRepository.save(secondUserEntity);
        assertNotEquals(0L, firstUserEntity.getId());
        assertNotEquals(0L, secondUserEntity.getId());
        assertTrue(secondUserEntity.getCreated().isAfter(firstUserEntity.getCreated()));
    }

    @Test
    public void save_whenGetUserEntityWithNullDescriptionOrName_thenThrowException() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(initializeUserEntity(null, null)));
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(initializeUserEntity("user_name", null)));
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(initializeUserEntity(null, "user_email.ru")));
    }

    @Test
    public void save_whenGetUserEntityWithSameEmail_thenThrowException() {
        assertThrows(DataIntegrityViolationException.class,
                () -> userRepository.save(initializeUserEntity("user", "user_email")));
    }

    @Test
    public void getReferenceByEmail_whenGetExistingAndNotExistingEmail_thenReturnOptionalOfUserEntity() {
        assertNotNull(userRepository.getReferenceByEmail("user_email").get());
        assertTrue(userRepository.getReferenceByEmail("email_user").isEmpty());
    }

    @Test
    public void findUserShortById_whenGetExistingAndNotExistingId_thenReturnOptionalOfUserShort() {
        UserShort userShort = userRepository.findUserShortById(firstUserEntity.getId()).get();
        assertNotNull(userShort);
        assertEquals(firstUserEntity.getName(), userShort.getName());
        assertEquals(firstUserEntity.getEmail(), userShort.getEmail());
        assertEquals(firstUserEntity.getCreated(), userShort.getCreated());
        assertTrue(userRepository.findUserShortById(secondUserEntity.getId()).isEmpty());
    }

    private UserEntity initializeUserEntity(String name, String email) {
        UserEntity userEntity = new UserEntity();
        userEntity.setName(name);
        userEntity.setEmail(email);
        return userEntity;
    }

}