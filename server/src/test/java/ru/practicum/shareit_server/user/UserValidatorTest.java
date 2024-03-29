package ru.practicum.shareit_server.user;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit_server.common.ShareItConstants;
import ru.practicum.shareit_server.exception.BadRequestBodyException;

public class UserValidatorTest {
    private UserValidator userValidator = new UserValidator();

    @Test
    public void validateAndAssignNullFields_whenGetUserWithNulls_thenReturnUserWithDefaultFields() {
        User user = User.builder().build();
        user = userValidator.validateAndAssignNullFields(user);

        Assertions.assertEquals(ShareItConstants.NOT_ASSIGNED, user.getName());
        assertEquals(ShareItConstants.DEFAULT_URI, user.getAvatarUri());
        assertEquals(ShareItConstants.NOT_ASSIGNED, user.getAddress().getCountry());
        assertFalse(user.isTelephoneVisible());
        assertNull(user.getCreated());
    }

    @Test
    public void validateAndAssignNullFields_whenGetUserWithOneNAfield_thenThrowException() {
        User user = User.builder()
                .address(UserAddress.builder()
                        .country(ShareItConstants.NOT_ASSIGNED)
                        .build())
                .build();

        BadRequestBodyException exception = assertThrows(BadRequestBodyException.class, () ->
                userValidator.validateAndAssignNullFields(user));
        assertEquals("Невозможно сохранить адрес пользователя со значением параметра country 'N/A'. " +
                "Данное обозначение зарезервино системой", exception.getMessage());
    }

    @Test
    public void validateAndAssignNullFields_whenGetUserWithNotNullAddress_thenReturnUserWithThisAddress() {
        UserAddress userAddress = UserAddress.builder()
                .country("country")
                .region("region")
                .cityOrSettlement("city")
                .cityDistrict("district")
                .street("street")
                .houseNumber(1)
                .build();

        User user = User.builder()
                .address(userAddress)
                .build();

        user = userValidator.validateAndAssignNullFields(user);
        assertEquals(userAddress, user.getAddress());
    }

}