package ru.practicum.shareit.common;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.exception.BadRequestBodyException;

import static org.junit.jupiter.api.Assertions.*;

import java.net.URI;

public class DomainObjectValidatorTest {
    private final DomainObjectValidator<Foo.Domain> domainObjectValidator = new DomainObjectValidator<>() {
        @Override
        public Foo.Domain validateAndAssignNullFields(Foo.Domain domainObject) {
            long fooId = checkLongField("foo", "fooId", domainObject.getFooId());
            float fooFloat = checkFloatField("foo", "fooFloat", domainObject.getFooFloat());
            String fooString = checkStringField("foo", "fooString", domainObject.getFooString());
            URI fooUri = checkUriField("foo", "fooUri", domainObject.getFooUri());
            return new Foo.Domain(fooId, fooFloat, fooString, fooUri, domainObject.getFooDateTime());
        }
    };
    private final Foo.Domain correctFoo = Foo.Domain.builder()
            .fooId(1)
            .fooFloat(2.0F)
            .fooString("fooString")
            .fooUri(URI.create("http://fooUri.com"))
            .fooDateTime(ShareItConstants.DEFAULT_LOCAL_DATE_TIME)
            .build();

    @Test
    public void validateAndAssignNullFields_whenGetCorrectFooDomain_thenReturnThisFooDomain() {
        Foo.Domain validatedFoo = domainObjectValidator.validateAndAssignNullFields(correctFoo);

        assertNotNull(validatedFoo);
        assertEquals(correctFoo, validatedFoo);
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNulls_thenReturnDefaultFooDomain() {
        Foo.Domain fooWithNulls = Foo.Domain.builder().build();
        Foo.Domain validatedFoo = domainObjectValidator.validateAndAssignNullFields(fooWithNulls);

        assertNotNull(validatedFoo);
        assertEquals(Foo.DOMAIN.toBuilder().fooDateTime(null).build(), validatedFoo);
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNegativeLong_thenThrowException() {
        Foo.Domain fooWithNegativeLong = correctFoo.toBuilder()
                .fooId(-1L)
                .build();

        BadRequestBodyException exception = assertThrows(BadRequestBodyException.class, () ->
        domainObjectValidator.validateAndAssignNullFields(fooWithNegativeLong));
        assertEquals("Невозможно сохранить foo со значением параметра fooId '-1'. " +
                "Значение должно быть положительным числом", exception.getMessage());
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNegativeFloat_thenThrowException() {
        Foo.Domain fooWithNegativeFloat = correctFoo.toBuilder()
                .fooFloat(-0.1F)
                .build();

        BadRequestBodyException exception = assertThrows(BadRequestBodyException.class, () ->
                domainObjectValidator.validateAndAssignNullFields(fooWithNegativeFloat));
        assertEquals(String.format("Невозможно сохранить foo со значением параметра fooFloat '%f'. " +
                "Значение должно быть положительным числом", -0.1F), exception.getMessage());
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNAString_thenThrowException() {
        Foo.Domain fooWithNAString = correctFoo.toBuilder()
                .fooString("N/A")
                .build();

        BadRequestBodyException exception = assertThrows(BadRequestBodyException.class, () ->
                domainObjectValidator.validateAndAssignNullFields(fooWithNAString));
        assertEquals("Невозможно сохранить foo со значением параметра fooString 'N/A'. " +
                "Данное обозначение зарезервино системой", exception.getMessage());
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNAUri_thenThrowException() {
        Foo.Domain fooWithNAUri = correctFoo.toBuilder()
                .fooUri(URI.create("N/A"))
                .build();

        BadRequestBodyException exception = assertThrows(BadRequestBodyException.class, () ->
                domainObjectValidator.validateAndAssignNullFields(fooWithNAUri));
        assertEquals("Невозможно сохранить foo со значением параметра fooUri 'N/A'. " +
                "Данное обозначение зарезервино системой", exception.getMessage());
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNullString_thenReturnFooWithNAString() {
        Foo.Domain fooWithNullString = correctFoo.toBuilder()
                .fooString(null)
                .build();

        Foo.Domain validatedFoo = domainObjectValidator.validateAndAssignNullFields(fooWithNullString);

        assertNotNull(validatedFoo);
        assertEquals("N/A", validatedFoo.getFooString());
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNullUri_thenReturnFooWithNAUri() {
        Foo.Domain fooWithNullUri = correctFoo.toBuilder()
                .fooUri(null)
                .build();

        Foo.Domain validatedFoo = domainObjectValidator.validateAndAssignNullFields(fooWithNullUri);

        assertNotNull(validatedFoo);
        assertEquals("N/A", validatedFoo.getFooUri().getPath());
    }

    @Test
    public void validateAndAssignNullFields_whenGetFooDomainWithNullDateTime_thenReturnFooWithNullDateTime() {
        Foo.Domain fooWithNullDateTime = correctFoo.toBuilder()
                .fooDateTime(null)
                .build();

        Foo.Domain validatedFoo = domainObjectValidator.validateAndAssignNullFields(fooWithNullDateTime);

        assertNotNull(validatedFoo);
        assertNull(validatedFoo.getFooDateTime());
    }

}