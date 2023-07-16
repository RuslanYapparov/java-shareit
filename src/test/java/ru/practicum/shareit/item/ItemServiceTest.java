package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit.booking.BookingService;
import ru.practicum.shareit.booking.BookingStatus;
import ru.practicum.shareit.booking.dto.BookingRestCommand;
import ru.practicum.shareit.booking.dto.BookingRestView;
import ru.practicum.shareit.common.ShareItConstants;
import ru.practicum.shareit.item.comment.Comment;
import ru.practicum.shareit.item.comment.dto.CommentRestCommand;
import ru.practicum.shareit.item.dto.ItemRestCommand;
import ru.practicum.shareit.item.dto.ItemRestView;
import ru.practicum.shareit.request.ItemRequestService;
import ru.practicum.shareit.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit.request.dto.ItemRequestRestView;
import ru.practicum.shareit.user.UserService;
import ru.practicum.shareit.user.dto.UserRestCommand;
import ru.practicum.shareit.user.dto.UserRestView;

import java.time.LocalDateTime;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemServiceTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingService bookingService;
    private final ItemRequestService itemRequestService;

    private UserRestView firstUser;
    private UserRestView secondUser;
    private ItemRequestRestView firstRequest;
    private ItemRestView item;

    @BeforeEach
    public void prepareDbForTest() {
        UserRestCommand firstUserRestCommand = UserRestCommand.builder()
                .name("Валера")
                .email("valera@user.ru")
                .build();
        UserRestCommand secondUserRestCommand = UserRestCommand.builder()
                .name("Илюша")
                .email("iliusha@user.ru")
                .build();
        firstUser = userService.save(firstUserRestCommand);
        secondUser = userService.save(secondUserRestCommand);

        ItemRequestRestCommand firstRequestRestCommand = new ItemRequestRestCommand();
        firstRequestRestCommand.setDescription("Очень нужна мотыга");
        firstRequest = itemRequestService.save(secondUser.getId(), firstRequestRestCommand);

        ItemRestCommand firstItemRestCommand = ItemRestCommand.builder()
                .requestId(firstRequest.getId())
                .name("Мотыга")
                .description("Тупая тяжелая мотыга для проблемной работы")
                .available(true)
                .build();
        item = itemService.save(firstUser.getId(), firstItemRestCommand);
    }

    @Test
    public void saveFromBeforeEachAndGetById_whenGetCorrectData_thenReturnCorrectRequestRestView() {
        ItemRestView itemRestView = itemService.getById(secondUser.getId(), item.getId());
        assertThat(itemRestView, notNullValue());
        assertThat(itemRestView.getId(), equalTo(item.getId()));
        assertThat(itemRestView.getOwnerId(), equalTo(firstUser.getId()));
        assertThat(itemRestView.getDescription(), equalTo("Тупая тяжелая мотыга для проблемной работы"));
        assertThat(itemRestView.isAvailable(), equalTo(true));
        assertThat(itemRestView.getItemPhotoUri(),equalTo(ShareItConstants.DEFAULT_URI));
        assertThat(itemRestView.getCreated(), notNullValue());
        assertThat(itemRestView.getLastModified(), notNullValue());
        assertThat(itemRestView.getNextBooking(), nullValue());
        assertThat(itemRestView.getLastBooking(), nullValue());
        assertThat(itemRestView.getComments(), iterableWithSize(0));
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenReturnPageOfAllItems() {
        Page<ItemRestView> allItemsPage = itemService.getAll(firstUser.getId(), 0, 10);
        assertThat(allItemsPage, iterableWithSize(1));
        List<ItemRestView> allItemsList = allItemsPage.getContent();
        assertThat(allItemsList.get(0), equalTo(item));
        assertThat(allItemsList.get(0).getRequestId(), equalTo(firstRequest.getId()));
    }

    @Test
    public void getById_whenGetCorrectParameters_thenReturnItemRestViewWithOrWithoutBookings() {
        BookingRestView futureBooking = bookingService.save(secondUser.getId(), BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .build());
        BookingRestView pastBooking = bookingService.save(secondUser.getId(), BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(200))
                .build());
        bookingService.changeBookingStatus(firstUser.getId(), pastBooking.getId(), true);
        bookingService.changeBookingStatus(firstUser.getId(), futureBooking.getId(), true);
        ItemRestView itemRestView = itemService.getById(firstUser.getId(), item.getId());
        assertThat(itemRestView, notNullValue());
        assertThat(itemRestView.getNextBooking().getId(), equalTo(futureBooking.getId()));
        assertThat(itemRestView.getNextBooking().getBookerId(), equalTo(futureBooking.getBooker().getId()));
        assertThat(itemRestView.getNextBooking().getStart(), equalTo(futureBooking.getStart()));
        assertThat(itemRestView.getNextBooking().getEnd(), equalTo(futureBooking.getEnd()));
        assertThat(itemRestView.getNextBooking().getStatus(), equalTo(BookingStatus.APPROVED.name()));
        assertThat(itemRestView.getLastBooking().getId(), equalTo(pastBooking.getId()));
        assertThat(itemRestView.getLastBooking().getBookerId(), equalTo(pastBooking.getBooker().getId()));
        assertThat(itemRestView.getLastBooking().getStart(), equalTo(pastBooking.getStart()));
        assertThat(itemRestView.getLastBooking().getEnd(), equalTo(pastBooking.getEnd()));
        assertThat(itemRestView.getLastBooking().getStatus(), equalTo(BookingStatus.APPROVED.name()));

        itemRestView = itemService.getById(secondUser.getId(), item.getId());
        assertThat(itemRestView, notNullValue());
        assertThat(itemRestView.getNextBooking(), nullValue());
        assertThat(itemRestView.getLastBooking(), nullValue());
    }

    @Test
    public void update_whenGetRestCommandWithOneField_thenReturnUpdatedItemRestView() {
        ItemRestCommand itemRestCommand = ItemRestCommand.builder()
                .name("item_name")
                .build();
        LocalDateTime created = item.getCreated();
        LocalDateTime lastModified = item.getLastModified();
        itemService.update(firstUser.getId(), item.getId(), itemRestCommand);
        item = itemService.getById(firstUser.getId(), item.getId());
        assertThat(item.getName(), equalTo("item_name"));
        assertThat(item.getDescription(), equalTo("Тупая тяжелая мотыга для проблемной работы"));
        assertThat(item.getCreated(), equalTo(created));
        assertThat(item.getLastModified(), not(lastModified));

        itemRestCommand = ItemRestCommand.builder()
                .description("item_description")
                .build();
        lastModified = item.getLastModified();
        itemService.update(firstUser.getId(), item.getId(), itemRestCommand);
        item = itemService.getById(firstUser.getId(), item.getId());
        assertThat(item.getName(), equalTo("item_name"));
        assertThat(item.getDescription(), equalTo("item_description"));
        assertThat(item.getCreated(), equalTo(created));
        assertThat(item.getLastModified(), not(equalTo(lastModified)));

        itemRestCommand = ItemRestCommand.builder()
                .available(false)
                .build();
        lastModified = item.getLastModified();
        itemService.update(firstUser.getId(), item.getId(), itemRestCommand);
        item = itemService.getById(firstUser.getId(), item.getId());
        assertThat(item.getName(), equalTo("item_name"));
        assertThat(item.getDescription(), equalTo("item_description"));
        assertThat(item.isAvailable(), equalTo(false));
        assertThat(item.getCreated(), equalTo(created));
        assertThat(item.getLastModified(), not(lastModified));
    }

    @Test
    public void deleteAll_whenCalled_deleteAllItems() {
        itemService.deleteAll(firstUser.getId());
        Page<ItemRestView> items = itemService.getAll(firstUser.getId(), 0, 10);
        assertThat(items, emptyIterable());
    }

    @Test
    public void deleteById_whenGetCorrectParameters_thenDeleteUser() {
        ItemRestView deletedItem = itemService.deleteById(firstUser.getId(), item.getId());
        assertThat(deletedItem, equalTo(item));
        Page<ItemRestView> allItems = itemService.getAll(firstUser.getId(), 0, 10);
        assertThat(allItems, emptyIterable());
    }

    @Test
    public void addCommentToItem_whenGetCorrectParameters_thenReturnComment() {
        BookingRestView pastBooking = bookingService.save(secondUser.getId(), BookingRestCommand.builder()
                .itemId(item.getId())
                .start(LocalDateTime.now())
                .end(LocalDateTime.now().plusNanos(20))
                .build());
        bookingService.changeBookingStatus(firstUser.getId(), pastBooking.getId(), true);

        CommentRestCommand newComment = new CommentRestCommand();
        newComment.setText("Отличная мотыга!");
        Comment savedComment = itemService.addCommentToItem(secondUser.getId(), item.getId(), newComment);
        assertThat(savedComment, notNullValue());
        assertThat(savedComment.getAuthorId(), equalTo(secondUser.getId()));
        assertThat(savedComment.getAuthorName(), equalTo(secondUser.getName()));
        assertThat(savedComment.getText(), equalTo(newComment.getText()));

        ItemRestView itemRestView = itemService.getById(firstUser.getId(), item.getId());
        assertThat(itemRestView.getComments(), notNullValue());
        assertThat(itemRestView.getComments().get(0), equalTo(savedComment));
    }

    @Test
    public void searchInNamesAndDescriptionsByText_whenGetCorrectParameters_thenReturnPageOfAvailableItemRestViews() {
        Page<ItemRestView> itemRestViews = itemService
                .searchInNamesAndDescriptionsByText(secondUser.getId(), "тупая", 0, 10);
        assertThat(itemRestViews, iterableWithSize(1));
        assertThat(item, equalTo(itemRestViews.toList().get(0)));

        ItemRestCommand newItemRestCommand = ItemRestCommand.builder()
                .name("Иголка для сим-лотка смартфона")
                .description("Не вставлять в отверстие микрофона")
                .available(false)
                .build();
        ItemRestView itemRestView = itemService.save(secondUser.getId(), newItemRestCommand);
        itemRestViews = itemService.searchInNamesAndDescriptionsByText(firstUser.getId(), "Т", 0, 10);
        assertThat(itemRestViews, iterableWithSize(1));
        assertThat(item, equalTo(itemRestViews.toList().get(0)));

        newItemRestCommand = ItemRestCommand.builder()
                .available(true)
                .build();
        itemRestView = itemService.update(secondUser.getId(), itemRestView.getId(), newItemRestCommand);
        itemRestViews = itemService.searchInNamesAndDescriptionsByText(firstUser.getId(), "Т", 0, 10);
        assertThat(itemRestViews, iterableWithSize(2));
        assertThat(item, equalTo(itemRestViews.toList().get(0)));
        assertThat(itemRestView, equalTo(itemRestViews.toList().get(1)));
    }

}