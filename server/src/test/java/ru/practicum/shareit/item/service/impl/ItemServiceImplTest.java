package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.checker.ItemChecker;
import ru.practicum.shareit.checker.UserChecker;
import ru.practicum.shareit.exception.NotFoundBookingException;
import ru.practicum.shareit.exception.NotFoundCommentException;
import ru.practicum.shareit.exception.NotFoundItemException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.item.dto.CommentRequestDto;
import ru.practicum.shareit.item.dto.CommentResponseDto;
import ru.practicum.shareit.item.dto.ItemDtoRequest;
import ru.practicum.shareit.item.dto.ItemDtoResponse;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemSearchParams;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.model.ItemResponse;
import ru.practicum.shareit.request.repository.ItemResponseRepository;
import ru.practicum.shareit.user.model.User;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 26.05.2024
 */
class ItemServiceImplTest {
    private ItemService itemService;
    private ItemRepository itemRepository;
    private BookingRepository bookingRepository;
    private CommentRepository commentRepository;
    private ItemResponseRepository itemResponseRepository;
    private ItemChecker itemChecker;
    private UserChecker userChecker;
    private User owner;
    private Item item;

    @BeforeEach
    void setUp() {
        itemRepository = Mockito.mock(ItemRepository.class);
        bookingRepository = Mockito.mock(BookingRepository.class);
        commentRepository = Mockito.mock(CommentRepository.class);
        itemResponseRepository = Mockito.mock(ItemResponseRepository.class);
        itemChecker = Mockito.mock(ItemChecker.class);
        userChecker = Mockito.mock(UserChecker.class);
        itemService = new ItemServiceImpl(itemRepository, bookingRepository, commentRepository, itemResponseRepository, itemChecker, userChecker);

        owner = User.builder()
                .id(3L)
                .name("testOwner")
                .email("testBookerEmail@mail.com")
                .build();

        item = Item.builder()
                .id(1L)
                .owner(this.owner)
                .name("testItem")
                .description("testItemDescription")
                .available(true)
                .build();
    }

    @Test
    void addNewItemTestValidRequestIdNull() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(owner);
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);

        var itemDtoResponse = itemService.addNewItem(ItemDtoRequest.builder()
                .name("testItem")
                .available(true)
                .description("testItemDescription")
                .build(), 3L);

        Assertions.assertNotNull(itemDtoResponse);
        Assertions.assertEquals(1, itemDtoResponse.getId());
        Assertions.assertNull(itemDtoResponse.getRequestId());
        Assertions.assertEquals("testItem", itemDtoResponse.getName());
        Assertions.assertEquals("testItemDescription", itemDtoResponse.getDescription());
        Assertions.assertEquals(true, itemDtoResponse.getAvailable());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));
    }

    @Test
    void addNewItemTestValidRequestId1() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(owner);
        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item);
        Mockito.when(itemResponseRepository.save(Mockito.any(ItemResponse.class)))
                .thenReturn(ItemResponse.builder()
                        .id(1L)
                        .item(item)
                        .request(ItemRequest.builder()
                                .id(1L)
                                .created(ZonedDateTime.now().minusDays(1))
                                .requestor(User.builder()
                                        .id(1L)
                                        .name("testRequestor")
                                        .email("testRequestor@email.com")
                                        .build())
                                .description("testDescription")
                                .build())
                        .build());

        var itemDtoResponse = itemService.addNewItem(ItemDtoRequest.builder()
                .name("testItem")
                .available(true)
                .description("testItemDescription")
                .requestId(1L)
                .build(), 3L);

        Assertions.assertNotNull(itemDtoResponse);
        Assertions.assertEquals(1, itemDtoResponse.getId());
        Assertions.assertEquals(1, itemDtoResponse.getRequestId());
        Assertions.assertEquals("testItem", itemDtoResponse.getName());
        Assertions.assertEquals("testItemDescription", itemDtoResponse.getDescription());
        Assertions.assertEquals(true, itemDtoResponse.getAvailable());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));
        Mockito.verify(itemResponseRepository, Mockito.times(1)).save(Mockito.any(ItemResponse.class));
    }

    @Test
    void addNewItemTestNotValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя создать новую вещь для не существующего пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.addNewItem(ItemDtoRequest.builder()
                .name("testItem")
                .available(true)
                .description("testItemDescription")
                .build(), 3L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя создать новую вещь для не существующего пользователя с id 3", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void updateItemTestValid() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);

        Mockito.when(itemRepository.save(Mockito.any(Item.class)))
                .thenReturn(item.toBuilder()
                        .name("updateTestItem")
                        .description("updateTestItemDescription")
                        .available(false)
                        .build());

        var itemDtoResponse = itemService.updateItem(ItemDtoRequest.builder()
                .name("updateTestItem")
                .description("updateTestItemDescription")
                .available(false)
                .build(), 3L, 1L);

        Assertions.assertNotNull(itemDtoResponse);
        Assertions.assertEquals("updateTestItemDescription", itemDtoResponse.getDescription());
        Assertions.assertEquals(false, itemDtoResponse.getAvailable());
        Assertions.assertEquals("updateTestItem", itemDtoResponse.getName());
        Assertions.assertEquals(1, itemDtoResponse.getId());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any(Item.class));
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void updateItemTestNotValidItemNotExists() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundItemException("Нельзя обновить не существующую вещь с id 1 для пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemService.updateItem(ItemDtoRequest.builder()
                .name("updateTestItem")
                .description("updateTestItemDescription")
                .available(false)
                .build(), 3L, 1L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить не существующую вещь с id 1 для пользователя с id 3", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void updateItemTestNotValidUserNotExists() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя обновить вещь c id 1 для не существующего пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.updateItem(ItemDtoRequest.builder()
                .name("updateTestItem")
                .description("updateTestItemDescription")
                .available(false)
                .build(), 3L, 1L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить вещь c id 1 для не существующего пользователя с id 3", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void updateItemTestNotValidUserNotOwner() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item.toBuilder()
                        .owner(owner.toBuilder()
                                .id(2L)
                                .build())
                        .build());

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemService.updateItem(ItemDtoRequest.builder()
                .name("updateTestItem")
                .description("updateTestItemDescription")
                .available(false)
                .build(), 3L, 1L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить вещь с id 1 пользователь с id 3 не является её владельцем", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getItemByItemIdTestValidByOwner() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);

        Mockito.when(bookingRepository.getBookings(Mockito.anyLong()))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .start(ZonedDateTime.now().minusDays(3))
                        .end(ZonedDateTime.now().minusDays(2))
                        .booker(User.builder()
                                .id(1L)
                                .name("testUser1")
                                .email("testEmail1@email.com")
                                .build())
                        .status(BookingStatus.APPROVED)
                        .item(item)
                        .build(), Booking.builder()
                        .id(2L)
                        .start(ZonedDateTime.now().plusDays(1))
                        .end(ZonedDateTime.now().plusDays(2))
                        .booker(User.builder()
                                .id(2L)
                                .name("testUser2")
                                .email("testEmail2@email.com")
                                .build())
                        .status(BookingStatus.APPROVED)
                        .item(item)
                        .build()));

        var itemByItemId = itemService.getItemByItemId(1L, 3L);

        Assertions.assertNotNull(itemByItemId);
        Assertions.assertEquals(1, itemByItemId.getId());
        Assertions.assertEquals(List.of(), itemByItemId.getComments());
        Assertions.assertEquals("testItem", itemByItemId.getName());
        Assertions.assertEquals(true, itemByItemId.getAvailable());
        Assertions.assertEquals("testItemDescription", itemByItemId.getDescription());
        Assertions.assertEquals(1, itemByItemId.getLastBooking().getId());
        Assertions.assertEquals(1, itemByItemId.getLastBooking().getBookerId());
        Assertions.assertEquals(2, itemByItemId.getNextBooking().getId());
        Assertions.assertEquals(2, itemByItemId.getNextBooking().getId());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(bookingRepository, Mockito.times(1)).getBookings(Mockito.anyLong());
    }

    @Test
    void getItemByItemIdTestValidByUser() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);

        var itemByItemId = itemService.getItemByItemId(1L, 2L);

        Assertions.assertNotNull(itemByItemId);
        Assertions.assertEquals(1, itemByItemId.getId());
        Assertions.assertEquals(List.of(), itemByItemId.getComments());
        Assertions.assertEquals("testItem", itemByItemId.getName());
        Assertions.assertEquals(true, itemByItemId.getAvailable());
        Assertions.assertEquals("testItemDescription", itemByItemId.getDescription());
        Assertions.assertNull(itemByItemId.getLastBooking());
        Assertions.assertNull(itemByItemId.getNextBooking());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getItemByItemIdTestNotValid() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundItemException("Нельзя получить не существующую вещь с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemService.getItemByItemId(1L, 2L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя получить не существующую вещь с id 1", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void deleteItemByItemIdTestValid() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);

        itemService.deleteItemByItemId(1L, 3L);

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteItemByItemIdTestNotValidUserNotExists() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя удалить вещь с id 1 для не существующего пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.deleteItemByItemId(1L, 3L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить вещь с id 1 для не существующего пользователя с id 3", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(0)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteItemByItemIdTestNotValidItemNotExists() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundItemException("Нельзя удалить не существующую вещь с id 1 для пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemService.deleteItemByItemId(1L, 3L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить не существующую вещь с id 1 для пользователя с id 3", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteItemByItemIdTestNotValidUserNotOwner() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemService.deleteItemByItemId(1L, 2L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить вещь с id 1 пользователь с id 2 не является её владельцем", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    void getAllItemByUserTestValid() {
        Mockito.when(itemRepository.findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        Item.builder()
                                .id(1L)
                                .available(true)
                                .owner(owner)
                                .description("testDescription1")
                                .name("testName1")
                                .build(),
                        Item.builder()
                                .id(2L)
                                .available(true)
                                .owner(owner)
                                .description("testDescription2")
                                .name("testName2")
                                .build()
                )));

        var allItemByUser = itemService.getAllItemByUser(3L, 0, 2);

        Assertions.assertNotNull(allItemByUser);
        Assertions.assertEquals(2, allItemByUser.size());

        Assertions.assertEquals(1, allItemByUser.get(0).getId());
        Assertions.assertEquals("testDescription1", allItemByUser.get(0).getDescription());
        Assertions.assertEquals(true, allItemByUser.get(0).getAvailable());
        Assertions.assertEquals("testName1", allItemByUser.get(0).getName());

        Assertions.assertEquals(2, allItemByUser.get(1).getId());
        Assertions.assertEquals("testDescription2", allItemByUser.get(1).getDescription());
        Assertions.assertEquals(true, allItemByUser.get(1).getAvailable());
        Assertions.assertEquals("testName2", allItemByUser.get(1).getName());


        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllItemByUserTestNotValidUserNotExists() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя получить список вещей для не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.getAllItemByUser(1L, 0, 2));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя получить список вещей для не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).findAllByOwnerId(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void searchItemByTextTestValid() {
        Mockito.when(itemRepository.searchItem(Mockito.anyString(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(
                        Item.builder()
                                .id(1L)
                                .available(true)
                                .owner(owner)
                                .description("testDescription1")
                                .name("testName1")
                                .build(),
                        Item.builder()
                                .id(2L)
                                .available(true)
                                .owner(owner)
                                .description("testDescription2")
                                .name("testName2")
                                .build()
                )));

        List<ItemDtoResponse> test = itemService.searchItemByText(ItemSearchParams.builder()
                .text("test")
                .from(0)
                .size(2)
                .userId(1L)
                .build());

        Assertions.assertNotNull(test);
        Assertions.assertEquals(2, test.size());
        Assertions.assertEquals(1, test.get(0).getId());
        Assertions.assertEquals("testDescription1", test.get(0).getDescription());
        Assertions.assertEquals(true, test.get(0).getAvailable());
        Assertions.assertEquals("testName1", test.get(0).getName());
        Assertions.assertNull(test.get(0).getRequestId());
        Assertions.assertEquals(2, test.get(1).getId());
        Assertions.assertEquals("testDescription2", test.get(1).getDescription());
        Assertions.assertEquals(true, test.get(1).getAvailable());
        Assertions.assertEquals("testName2", test.get(1).getName());
        Assertions.assertNull(test.get(1).getRequestId());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).searchItem(Mockito.anyString(), Mockito.any(Pageable.class));
    }

    @Test
    void searchItemByTextTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя найти список вещей для не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.searchItemByText(ItemSearchParams.builder()
                .text("test")
                .from(0)
                .size(2)
                .userId(1L)
                .build()));
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя найти список вещей для не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).searchItem(Mockito.anyString(), Mockito.any(Pageable.class));
    }

    @Test
    void searchItemByTextTestNotValidTextBlank() {
        List<ItemDtoResponse> itemDtoResponses = itemService.searchItemByText(ItemSearchParams.builder()
                .text("")
                .from(0)
                .size(2)
                .userId(1L)
                .build());

        Assertions.assertNotNull(itemDtoResponses);
        Assertions.assertEquals(0, itemDtoResponses.size());

        Mockito.verify(userChecker, Mockito.times(0)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).searchItem(Mockito.anyString(), Mockito.any(Pageable.class));
    }

    @Test
    void deleteAllItemByUserTestValid() {
        Assertions.assertDoesNotThrow(() -> itemService.deleteAllItemByUser(3L));

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(1)).deleteAllByOwner_Id(Mockito.anyLong());
    }

    @Test
    void deleteAllItemByUserTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя удалить все вещи для не существующего пользователя с id 3"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.deleteAllItemByUser(3L));

        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить все вещи для не существующего пользователя с id 3", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRepository, Mockito.times(0)).deleteAllByOwner_Id(Mockito.anyLong());
    }

    @Test
    void addCommentTestValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(owner);
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);

        Mockito.when(bookingRepository.findByItem_IdAndBooker_Id(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ZonedDateTime.class)))
                .thenReturn(List.of(Booking.builder()
                        .id(1L)
                        .item(item)
                        .status(BookingStatus.APPROVED)
                        .booker(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .end(ZonedDateTime.now().minusDays(1))
                        .start(ZonedDateTime.now().minusDays(2))
                        .build()));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(Comment.builder()
                        .item(item)
                        .text("testComment")
                        .created(ZonedDateTime.now())
                        .author(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .id(1L)
                        .build());

        CommentResponseDto testComment = itemService.addComment(1L, 2L, TimeZone.getDefault(), CommentRequestDto.builder()
                .text("testComment")
                .build());

        Assertions.assertNotNull(testComment);
        Assertions.assertEquals("testName2", testComment.getAuthorName());
        Assertions.assertNotNull(testComment.getCreated());
        Assertions.assertEquals(1, testComment.getId());
        Assertions.assertEquals("testComment", testComment.getText());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
        Mockito.verify(bookingRepository, Mockito.times(1)).findByItem_IdAndBooker_Id(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ZonedDateTime.class));
    }

    @Test
    void addCommentTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя оставить комментарий вещи с id 1 от не существующего пользователя с id 2"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.addComment(1L, 2L, TimeZone.getDefault(), CommentRequestDto.builder()
                .text("testComment")
                .build()));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя оставить комментарий вещи с id 1 от не существующего пользователя с id 2", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(0)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any(Comment.class));
        Mockito.verify(bookingRepository, Mockito.times(0)).findByItem_IdAndBooker_Id(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ZonedDateTime.class));
    }

    @Test
    void addCommentTestNotValidItemNotFound() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundItemException("Нельзя оставить комментарий не существующей вещи id 1 от пользователя с id 2"));

        Throwable throwable = Assertions.assertThrows(NotFoundItemException.class, () -> itemService.addComment(1L, 2L, TimeZone.getDefault(), CommentRequestDto.builder()
                .text("testComment")
                .build()));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя оставить комментарий не существующей вещи id 1 от пользователя с id 2", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any(Comment.class));
        Mockito.verify(bookingRepository, Mockito.times(0)).findByItem_IdAndBooker_Id(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ZonedDateTime.class));
    }

    @Test
    void addCommentTestNotValidBookingNotFound() {
        Mockito.when(itemChecker.checkItem(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(item);
        Mockito.when(bookingRepository.findByItem_IdAndBooker_Id(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ZonedDateTime.class)))
                .thenReturn(List.of());

        Throwable throwable = Assertions.assertThrows(NotFoundBookingException.class, () -> itemService.addComment(1L, 2L, TimeZone.getDefault(), CommentRequestDto.builder()
                .text("testComment")
                .build()));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя оставить комментарий. Пользователь с id 2 ещё не бронировал вещь с id 1", throwable.getMessage());

        Mockito.verify(itemChecker, Mockito.times(1)).checkItem(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any(Comment.class));
        Mockito.verify(bookingRepository, Mockito.times(1)).findByItem_IdAndBooker_Id(Mockito.anyLong(), Mockito.anyLong(), Mockito.any(ZonedDateTime.class));
    }

    @Test
    void updateCommentTestValid() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Comment.builder()
                        .item(item)
                        .text("testComment")
                        .created(ZonedDateTime.now())
                        .author(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .id(1L)
                        .build()));

        Mockito.when(commentRepository.save(Mockito.any(Comment.class)))
                .thenReturn(Comment.builder()
                        .item(item)
                        .text("UpdateTestComment")
                        .created(ZonedDateTime.now())
                        .author(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .id(1L)
                        .build());

        CommentResponseDto testComment = itemService.updateComment(CommentRequestDto.builder()
                .text("UpdateTestComment")
                .build(), 2L, 1L);

        Assertions.assertNotNull(testComment);
        Assertions.assertEquals("UpdateTestComment", testComment.getText());
        Assertions.assertNotNull(testComment.getCreated());
        Assertions.assertEquals("testName2", testComment.getAuthorName());
        Assertions.assertEquals(1, testComment.getId());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).save(Mockito.any(Comment.class));
    }

    @Test
    void updateCommentTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя обновить комментарий c id 1 для не существующего пользователя с id 2"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.updateComment(CommentRequestDto.builder()
                .text("UpdateTestComment")
                .build(), 2L, 1L));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить комментарий c id 1 для не существующего пользователя с id 2", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(0)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any(Comment.class));
    }

    @Test
    void updateCommentTestNotValidUserNotAuthor() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Comment.builder()
                        .item(item)
                        .text("testComment")
                        .created(ZonedDateTime.now())
                        .author(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .id(1L)
                        .build()));

        Throwable throwable = Assertions.assertThrows(NotFoundCommentException.class, () -> itemService.updateComment(CommentRequestDto.builder()
                .text("UpdateTestComment")
                .build(), 1L, 1L));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("У пользователя с id 1 нет комментария с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any(Comment.class));
    }

    @Test
    void updateCommentTestNotValidCommentNotFound() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.assertThrows(NotFoundCommentException.class, () -> itemService.updateComment(CommentRequestDto.builder()
                .text("UpdateTestComment")
                .build(), 2L, 1L));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить не существующий комментарий с id 1 для пользователя с id 2", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(0)).save(Mockito.any(Comment.class));
    }

    @Test
    void deleteCommentTestValid() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Comment.builder()
                        .item(item)
                        .text("testComment")
                        .created(ZonedDateTime.now())
                        .author(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .id(1L)
                        .build()));

        Assertions.assertDoesNotThrow(() -> itemService.deleteComment(1L, 2L));

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(1)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteCommentTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя удалить комментарий c id 1 для не существующего пользователя с id 2"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemService.deleteComment(1L, 2L));
        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить комментарий c id 1 для не существующего пользователя с id 2", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(0)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteCommentTestNotValidUserNotOwner() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(Comment.builder()
                        .item(item)
                        .text("testComment")
                        .created(ZonedDateTime.now())
                        .author(User.builder()
                                .id(2L)
                                .name("testName2")
                                .email("testEmail2@email.com")
                                .build())
                        .id(1L)
                        .build()));

        Throwable throwable = Assertions.assertThrows(NotFoundCommentException.class, () -> itemService.deleteComment(1L, 1L));
        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("У пользователя с id 1 нет комментария с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }

    @Test
    void deleteCommentTestNotValidCommentNotFound() {
        Mockito.when(commentRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.assertThrows(NotFoundCommentException.class, () -> itemService.deleteComment(1L, 1L));
        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить не существующий комментарий с id 1 для  пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(commentRepository, Mockito.times(1)).findById(Mockito.anyLong());
        Mockito.verify(commentRepository, Mockito.times(0)).deleteById(Mockito.anyLong());
    }
}