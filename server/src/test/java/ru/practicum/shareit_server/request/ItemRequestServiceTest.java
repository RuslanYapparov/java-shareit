package ru.practicum.shareit_server.request;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.shareit_server.item.ItemService;
import ru.practicum.shareit_server.item.dto.ItemRestCommand;
import ru.practicum.shareit_server.item.dto.ItemRestView;
import ru.practicum.shareit_server.request.dto.ItemRequestRestCommand;
import ru.practicum.shareit_server.request.dto.ItemRequestRestView;
import ru.practicum.shareit_server.request.dto.RequestedItem;
import ru.practicum.shareit_server.user.UserService;
import ru.practicum.shareit_server.user.dto.UserRestCommand;
import ru.practicum.shareit_server.user.dto.UserRestView;

import java.util.List;

@Transactional
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ItemRequestServiceTest {
    private final ItemRequestService itemRequestService;
    private final UserService userService;
    private final ItemService itemService;

    private UserRestView firstUser;
    private UserRestView secondUser;
    private ItemRequestRestView firstRequest;
    private ItemRequestRestView secondRequest;
    private ItemRestView firstItem;
    private ItemRestView secondItem;

    @BeforeEach
    public void prepareDbForTest() {
        UserRestCommand firstUserRestCommand = UserRestCommand.builder()
                .name("Валера")
                .email("valera@ru.practicum.shareit.user.ru")
                .build();
        UserRestCommand secondUserRestCommand = UserRestCommand.builder()
                .name("Илюша")
                .email("iliusha@ru.practicum.shareit.user.ru")
                .build();
        firstUser = userService.save(firstUserRestCommand);
        secondUser = userService.save(secondUserRestCommand);

        ItemRequestRestCommand firstRequestRestCommand = new ItemRequestRestCommand();
        firstRequestRestCommand.setDescription("Очень нужна мотыга");
        ItemRequestRestCommand secondRequestRestCommand = new ItemRequestRestCommand();
        secondRequestRestCommand.setDescription("Ищу держатель для подбородка");
        firstRequest = itemRequestService.save(secondUser.getId(), firstRequestRestCommand);
        secondRequest = itemRequestService.save(secondUser.getId(), secondRequestRestCommand);

        ItemRestCommand firstItemRestCommand = ItemRestCommand.builder()
                .requestId(firstRequest.getId())
                .name("Мотыга")
                .description("Тупая тяжелая мотыга для проблемной работы")
                .available(true)
                .build();
        ItemRestCommand secondItemRestCommand = ItemRestCommand.builder()
                .name("Иголка для сим-лотка смартфона")
                .description("Не вставлять в отверстие микрофона")
                .available(true)
                .build();
        firstItem = itemService.save(firstUser.getId(), firstItemRestCommand);
        secondItem = itemService.save(secondUser.getId(), secondItemRestCommand);
    }

    @Test
    public void saveInBeforeEach_whenGetCorrectData_thenReturnCorrectRequestRestView() {
        ItemRequestRestView itemRequestRestView = itemRequestService.getById(secondUser.getId(), firstRequest.getId());
        assertThat(itemRequestRestView, notNullValue());
        assertThat(itemRequestRestView.getId(), equalTo(firstRequest.getId()));
        assertThat(itemRequestRestView.getRequesterId(), equalTo(secondUser.getId()));
        assertThat(itemRequestRestView.getDescription(), equalTo("Очень нужна мотыга"));
        assertThat(itemRequestRestView.getCreated(), notNullValue());
        assertThat(itemRequestRestView.getLastModified(), notNullValue());
        assertThat(itemRequestRestView.getItems(), iterableWithSize(1));
        assertThat(itemRequestRestView.getItems().get(0).getRequestId(), equalTo(firstRequest.getId()));
    }

    @Test
    public void getAllRequestsOfRequester_whenGetCorrectRequesterId_thenReturnListOfRequests() {
        List<ItemRequestRestView> requests = itemRequestService.getAllRequestsOfRequester(secondUser.getId());
        assertThat(requests, notNullValue());
        assertThat(requests.size(), equalTo(2));
        assertThat(requests.get(0), instanceOf(ItemRequestRestView.class));

        ItemRequestRestView itemRequestRestView = requests.get(1);
        assertThat(itemRequestRestView.getRequesterId(), equalTo(secondUser.getId()));
        assertThat(itemRequestRestView.getDescription(), equalTo("Очень нужна мотыга"));
        assertThat(itemRequestRestView.getItems().size(), equalTo(1));

        RequestedItem requestedItem = itemRequestRestView.getItems().get(0);
        assertThat(requestedItem.getRequestId(), equalTo(itemRequestRestView.getId()));
        assertThat(requestedItem.getName(), equalTo(firstItem.getName()));
        assertThat(requestedItem.getDescription(), equalTo(firstItem.getDescription()));
        assertThat(requestedItem.isAvailable(), equalTo(firstItem.isAvailable()));

        ItemRequestRestView anotherItemRequestRestView = requests.get(0);
        assertThat(anotherItemRequestRestView, equalTo(secondRequest));
        assertThat(anotherItemRequestRestView.getItems(), nullValue());
    }

    @Test
    public void getItems_whenItWasMadeForRequestAndNot_thenRequestIdsMustBeConsistent() {
        ItemRestView firstItemForCheck = itemService.getById(firstUser.getId(), firstItem.getId());
        assertThat(firstItemForCheck.getRequestId(), equalTo(firstRequest.getId()));

        ItemRestView secondItemForCheck = itemService.getById(firstUser.getId(), secondItem.getId());
        assertThat(secondItemForCheck.getRequestId(), equalTo(0L));
    }

    @Test
    public void getAll_whenGetCorrectParameters_thenReturnPageOfAllRequestsWithoutOwn() {
        UserRestCommand thirdUserRestCommand = UserRestCommand.builder()
                .name("Армен")
                .email("armen@ru.practicum.shareit.user.ru")
                .build();
        UserRestView thirdUser = userService.save(thirdUserRestCommand);

        ItemRequestRestCommand thirdItemRequestRestCommand = new ItemRequestRestCommand();
        thirdItemRequestRestCommand.setDescription("Может быть у кого-нибудь есть дешифратор кошачей речи?");
        ItemRequestRestCommand fourthItemRequestRestCommand = new ItemRequestRestCommand();
        fourthItemRequestRestCommand.setDescription("Нужен на день блокиратор психотронного оружия");
        ItemRequestRestView thirdRequest = itemRequestService.save(thirdUser.getId(), thirdItemRequestRestCommand);
        ItemRequestRestView fourthRequest = itemRequestService.save(firstUser.getId(), fourthItemRequestRestCommand);

        Page<ItemRequestRestView> allRequestsForFirstUserPage = itemRequestService.getAll(firstUser.getId(), 0, 10);
        assertThat(allRequestsForFirstUserPage, iterableWithSize(3));
        List<ItemRequestRestView> allRequestForFirstUserList = allRequestsForFirstUserPage.getContent();
        assertThat(allRequestForFirstUserList.get(0), equalTo(thirdRequest));
        assertThat(allRequestForFirstUserList.get(1), equalTo(secondRequest));
        assertThat(allRequestForFirstUserList.get(2).getId(), equalTo(firstRequest.getId()));

        ItemRequestRestView itemRequestForItemExistCheck = allRequestForFirstUserList.get(2);
        assertThat(itemRequestForItemExistCheck.getRequesterId(), equalTo(secondUser.getId()));
        assertThat(itemRequestForItemExistCheck.getDescription(), equalTo("Очень нужна мотыга"));
        assertThat(itemRequestForItemExistCheck.getItems().size(), equalTo(1));

        RequestedItem requestedItem = itemRequestForItemExistCheck.getItems().get(0);
        assertThat(requestedItem.getRequestId(), equalTo(itemRequestForItemExistCheck.getId()));
        assertThat(requestedItem.getName(), equalTo(firstItem.getName()));
        assertThat(requestedItem.getDescription(), equalTo(firstItem.getDescription()));
        assertThat(requestedItem.isAvailable(), equalTo(firstItem.isAvailable()));

        Page<ItemRequestRestView> allRequestsForSecondUserPage = itemRequestService.getAll(secondUser.getId(), 0, 10);
        assertThat(allRequestsForSecondUserPage, iterableWithSize(2));
        List<ItemRequestRestView> allRequestForSecondUserList = allRequestsForSecondUserPage.getContent();
        assertThat(allRequestForSecondUserList.get(0), equalTo(fourthRequest));
        assertThat(allRequestForSecondUserList.get(1), equalTo(thirdRequest));

        Page<ItemRequestRestView> allRequestsForThirdUserPage = itemRequestService.getAll(thirdUser.getId(), 0, 10);
        assertThat(allRequestsForThirdUserPage, iterableWithSize(3));
        List<ItemRequestRestView> allRequestForThirdUserList = allRequestsForThirdUserPage.getContent();
        assertThat(allRequestForThirdUserList.get(0), equalTo(fourthRequest));
        assertThat(allRequestForThirdUserList.get(1), equalTo(secondRequest));
        assertThat(allRequestForThirdUserList.get(2).getId(), equalTo(firstRequest.getId()));
    }

}