package ru.practicum.shareit.item;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.common.ShareItConstants;
import static org.junit.jupiter.api.Assertions.*;

import ru.practicum.shareit.exception.BadRequestBodyException;


public class ItemValidatorTest {
    private ItemValidator itemValidator = new ItemValidator();

    @Test
    public void validateAndAssignNullFields_whenGetItemWithNulls_thenReturnItemWithDefaultFields() {
        Item item = Item.builder().build();
        item = itemValidator.validateAndAssignNullFields(item);

        assertEquals(ShareItConstants.NOT_ASSIGNED, item.getName());
        assertEquals(ShareItConstants.NOT_ASSIGNED, item.getDescription());
        assertEquals(ShareItConstants.DEFAULT_URI, item.getItemPhotoUri());
        assertNull(item.getAvailable());
        assertNull(item.getNextBooking());
        assertNull(item.getCreated());
    }

    @Test
    public void validateAndAssignNullFields_whenGetItemWithOneNAfield_thenThrowException() {
        Item item = Item.builder()
                .itemPhotoUri(ShareItConstants.DEFAULT_URI)
                .build();

        BadRequestBodyException exception = assertThrows(BadRequestBodyException.class, () ->
                itemValidator.validateAndAssignNullFields(item));
        assertEquals("Невозможно сохранить вещь со значением параметра photo_uri 'N/A'. " +
                "Данное обозначение зарезервино системой", exception.getMessage());
    }

}