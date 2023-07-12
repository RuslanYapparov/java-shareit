package ru.practicum.shareit.user;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.exception.BadRequestBodyException;

public class UserValidatorTest {
    private UserValidator userValidator = new UserValidator();

    @Test
    public void validateAndAssignNullFields_whenGetUserWithNulls_thenReturnUserWithDefaultFields() {
        User user = User.builder().build();
        user = userValidator.validateAndAssignNullFields(user);

        assertEquals(ShareItConstants.NOT_ASSIGNED, user.getName());
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

}