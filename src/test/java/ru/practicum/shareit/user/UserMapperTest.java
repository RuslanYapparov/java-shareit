package ru.practicum.shareit.user;

import org.junit.jupiter.api.Test;

import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.user.dao.UserAddressEntity;
import ru.practicum.shareit.user.dao.UserEntity;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperTest {
    private final UserMapper userMapper = new UserMapperImpl();
    private final User user = User.builder()
            .id(1)
            .name("Valera")
            .email("valera@user.ru")
            .address(initializeUserAddress())
            .avatarUri(ShareItConstants.DEFAULT_URI)
            .telephoneNumber(ShareItConstants.NOT_ASSIGNED)
            .telephoneVisible(false)
            .userRating(0.0F)
            .created(LocalDateTime.of(2023, 7, 7, 7, 7, 7))
            .lastModified(LocalDateTime.of(2023, 8, 8, 8, 8, 8))
            .build();
    private final UserRestCommand userRestCommand = UserRestCommand.builder()
            .name("Valera")
            .email("valera@user.ru")
            .build();
    private final UserRestView userRestView = UserRestView.builder()
            .id(1)
            .name("Valera")
            .email("valera@user.ru")
            .address(initializeUserAddress())
            .avatarUri(ShareItConstants.DEFAULT_URI)
            .telephoneNumber(ShareItConstants.NOT_ASSIGNED)
            .telephoneVisible(false)
            .userRating(0.0F)
            .created(LocalDateTime.of(2023, 7, 7, 7, 7, 7))
            .lastModified(LocalDateTime.of(2023, 8, 8, 8, 8, 8))
            .build();
    private final UserEntity userEntity = initializeNewUserEntity();

    @Test
    public void fromRestCommand_whenGetRestCommand_thenReturnUserFromRestCommand() {
        User userFromRestCommand = user.toBuilder()
                .id(0)
                .address(null)
                .telephoneNumber(null)
                .avatarUri(null)
                .created(null)
                .lastModified(null)
                .build();
        assertNotNull(userMapper.fromRestCommand(userRestCommand));
        assertNotEquals(User.builder().build(), userMapper.fromRestCommand(userRestCommand));
        assertEquals(User.builder().build(), userMapper.fromRestCommand(UserRestCommand.builder().build()));
        assertEquals(userFromRestCommand, userMapper.fromRestCommand(userRestCommand));
    }

    @Test
    public void toRestView_whenGetUser_thenReturnUserRestView() {
        assertNotNull(userMapper.toRestView(user));
        assertNotEquals(userRestView, userMapper.toRestView(User.builder().build()));
        assertEquals(UserRestView.builder().build(), userMapper.toRestView(User.builder().build()));
        assertEquals(userRestView, userMapper.toRestView(user));
    }

    @Test
    public void fromDbEntity_whenGetUserEntity_thenReturnUser() {
        assertNotNull(userMapper.fromDbEntity(userEntity));
        assertNotEquals(User.builder().build(), userMapper.fromDbEntity(userEntity));
        assertEquals(user, userMapper.fromDbEntity(userEntity));
    }

    @Test
    public void toDbEntity_whenGetUser_thenReturnUserEntity() {
        assertNotNull(userMapper.toDbEntity(user));
        assertNotEquals(new UserEntity(), userMapper.toDbEntity(user));
        assertEquals(userEntity, userMapper.toDbEntity(user));
    }

    private UserEntity initializeNewUserEntity() {
        UserEntity userEntity = new UserEntity();
        userEntity.setId(1L);
        userEntity.setName("Valera");
        userEntity.setEmail("valera@user.ru");
        userEntity.setAvatarUri(ShareItConstants.DEFAULT_URI.getPath());
        userEntity.setAddress(initializeUserAddressEntity());
        userEntity.setCreated(LocalDateTime.of(2023, 7, 7, 7, 7, 7));
        userEntity.setLastModified(LocalDateTime.of(2023, 8, 8, 8, 8, 8));
        userEntity.setTelephoneNumber(ShareItConstants.NOT_ASSIGNED);
        userEntity.setTelephoneVisible(false);
        userEntity.setUserRating(0.0F);
        return userEntity;
    }

    private UserAddressEntity initializeUserAddressEntity() {
        UserAddressEntity userAddressEntity = new UserAddressEntity();
        userAddressEntity.setCountry(ShareItConstants.NOT_ASSIGNED);
        userAddressEntity.setRegion(ShareItConstants.NOT_ASSIGNED);
        userAddressEntity.setCityOrSettlement(ShareItConstants.NOT_ASSIGNED);
        userAddressEntity.setCityDistrict(ShareItConstants.NOT_ASSIGNED);
        userAddressEntity.setStreet(ShareItConstants.NOT_ASSIGNED);
        userAddressEntity.setStreet(ShareItConstants.NOT_ASSIGNED);
        userAddressEntity.setHouseNumber(0);
        return userAddressEntity;
    }

    private UserAddress initializeUserAddress() {
        return UserAddress.builder()
                .country(ShareItConstants.NOT_ASSIGNED)
                .region(ShareItConstants.NOT_ASSIGNED)
                .cityOrSettlement(ShareItConstants.NOT_ASSIGNED)
                .cityDistrict(ShareItConstants.NOT_ASSIGNED)
                .street(ShareItConstants.NOT_ASSIGNED)
                .houseNumber(0)
                .build();
    }

}