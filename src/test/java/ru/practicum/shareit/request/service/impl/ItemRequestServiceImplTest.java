package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import ru.practicum.shareit.checker.UserChecker;
import ru.practicum.shareit.exception.NotFoundItemRequestException;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.request.dto.ItemRequestDtoCreated;
import ru.practicum.shareit.request.dto.ItemRequestDtoRequest;
import ru.practicum.shareit.request.dto.ItemRequestDtoResponse;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Optional;
import java.util.TimeZone;

/**
 * @author Nikolay Radzivon
 * @Date 26.05.2024
 */
class ItemRequestServiceImplTest {
    private ItemRequestService itemRequestService;
    private UserChecker userChecker;
    private ItemRequestRepository itemRequestRepository;

    @BeforeEach
    void setUp() {
        userChecker = Mockito.mock(UserChecker.class);
        itemRequestRepository = Mockito.mock(ItemRequestRepository.class);
        itemRequestService = new ItemRequestServiceImpl(itemRequestRepository, userChecker);
    }

    @Test
    void addNewRequestTestValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testUser")
                        .email("testUserEmail@example.com")
                        .build());

        Mockito.when(itemRequestRepository.save(Mockito.any(ItemRequest.class)))
                .thenReturn(ItemRequest.builder()
                        .id(1L)
                        .created(ZonedDateTime.now())
                        .description("testDescription")
                        .requestor(User.builder()
                                .id(1L)
                                .name("testUser")
                                .email("testUserEmail@example.com")
                                .build())
                        .build());

        ItemRequestDtoCreated testDescription = itemRequestService.addNewRequest(ItemRequestDtoRequest.builder()
                .description("testDescription")
                .build(), 1L, TimeZone.getDefault());

        Assertions.assertNotNull(testDescription);
        Assertions.assertEquals(1, testDescription.getId());
        Assertions.assertEquals("testDescription", testDescription.getDescription());
        Assertions.assertNotNull(testDescription.getCreated());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void addNewRequestTestNotValidNotFoundUser() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя создать новый запрос для не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemRequestService.addNewRequest(ItemRequestDtoRequest.builder()
                .description("testDescription")
                .build(), 1L, TimeZone.getDefault()));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя создать новый запрос для не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(0)).save(Mockito.any(ItemRequest.class));
    }

    @Test
    void getUserRequestsValid() {
        Mockito.when(itemRequestRepository.findAllByRequestorId(Mockito.anyLong(), Mockito.any(Sort.class)))
                .thenReturn(List.of(
                        ItemRequest.builder()
                                .id(1L)
                                .requestor(User.builder()
                                        .id(1L)
                                        .name("testUser")
                                        .email("testUserEmail@example.com")
                                        .build())
                                .description("testDescription1")
                                .created(ZonedDateTime.of(2024, 5, 20, 10, 10, 15, 0, ZoneId.systemDefault()))
                                .build(),
                        ItemRequest.builder()
                                .id(2L)
                                .requestor(User.builder()
                                        .id(1L)
                                        .name("testUser")
                                        .email("testUserEmail@example.com")
                                        .build())
                                .description("testDescription2")
                                .created(ZonedDateTime.of(2024, 5, 15, 10, 10, 15, 0, ZoneId.systemDefault()))
                                .build()
                ));

        List<ItemRequestDtoResponse> userRequests = itemRequestService.getUserRequests(1L);

        Assertions.assertNotNull(userRequests);
        Assertions.assertEquals(2, userRequests.size());
        Assertions.assertEquals(1, userRequests.get(0).getId());
        Assertions.assertNull(userRequests.get(0).getItems());
        Assertions.assertEquals("testDescription1", userRequests.get(0).getDescription());
        Assertions.assertEquals(ZonedDateTime.of(2024, 5, 20, 10, 10, 15, 0, ZoneId.systemDefault()).toLocalDateTime(), userRequests.get(0).getCreated());
        Assertions.assertNull(userRequests.get(1).getItems());
        Assertions.assertEquals(ZonedDateTime.of(2024, 5, 15, 10, 10, 15, 0, ZoneId.systemDefault()).toLocalDateTime(), userRequests.get(1).getCreated());
        Assertions.assertNull(userRequests.get(1).getItems());
        Assertions.assertEquals("testDescription2", userRequests.get(1).getDescription());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByRequestorId(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    void getUserRequestsNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя запросить список запросов для не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemRequestService.getUserRequests(1L));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя запросить список запросов для не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(0)).findAllByRequestorId(Mockito.anyLong(), Mockito.any(Sort.class));
    }

    @Test
    void getAllRequestsValid() {
        Mockito.when(itemRequestRepository.findAllByRequestorIdNot(Mockito.anyLong(), Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<ItemRequest>(List.of(
                        ItemRequest.builder()
                                .id(1L)
                                .requestor(User.builder()
                                        .id(1L)
                                        .name("testUser")
                                        .email("testUserEmail@example.com")
                                        .build())
                                .description("testDescription1")
                                .created(ZonedDateTime.of(2024, 5, 20, 10, 10, 15, 0, ZoneId.systemDefault()))
                                .build(),
                        ItemRequest.builder()
                                .id(2L)
                                .requestor(User.builder()
                                        .id(1L)
                                        .name("testUser")
                                        .email("testUserEmail@example.com")
                                        .build())
                                .description("testDescription2")
                                .created(ZonedDateTime.of(2024, 5, 15, 10, 10, 15, 0, ZoneId.systemDefault()))
                                .build()
                )));

        List<ItemRequestDtoResponse> allRequests = itemRequestService.getAllRequests(2L, 0, 2);

        Assertions.assertNotNull(allRequests);
        Assertions.assertEquals(2, allRequests.size());
        Assertions.assertEquals(1, allRequests.get(0).getId());
        Assertions.assertNull(allRequests.get(0).getItems());
        Assertions.assertEquals("testDescription1", allRequests.get(0).getDescription());
        Assertions.assertEquals(ZonedDateTime.of(2024, 5, 20, 10, 10, 15, 0, ZoneId.systemDefault()).toLocalDateTime(), allRequests.get(0).getCreated());
        Assertions.assertNull(allRequests.get(1).getItems());
        Assertions.assertEquals(ZonedDateTime.of(2024, 5, 15, 10, 10, 15, 0, ZoneId.systemDefault()).toLocalDateTime(), allRequests.get(1).getCreated());
        Assertions.assertNull(allRequests.get(1).getItems());
        Assertions.assertEquals("testDescription2", allRequests.get(1).getDescription());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findAllByRequestorIdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllRequestsNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя запросить список всех запросов от не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemRequestService.getAllRequests(1L, 0, 2));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя запросить список всех запросов от не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(0)).findAllByRequestorIdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllRequestsNotValidFromNull() {
        List<ItemRequestDtoResponse> allRequests = itemRequestService.getAllRequests(1L, null, 2);

        Assertions.assertNotNull(allRequests);
        Assertions.assertEquals(0, allRequests.size());

        Mockito.verify(userChecker, Mockito.times(0)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(0)).findAllByRequestorIdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getAllRequestsNotValidSizeNull() {
        List<ItemRequestDtoResponse> allRequests = itemRequestService.getAllRequests(1L, 0, null);

        Assertions.assertNotNull(allRequests);
        Assertions.assertEquals(0, allRequests.size());

        Mockito.verify(userChecker, Mockito.times(0)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(0)).findAllByRequestorIdNot(Mockito.anyLong(), Mockito.any(Pageable.class));
    }

    @Test
    void getRequestByIdTestValid() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.of(ItemRequest.builder()
                        .id(1L)
                        .requestor(User.builder()
                                .id(1L)
                                .name("testUser")
                                .email("testUserEmail@example.com")
                                .build())
                        .description("testDescription1")
                        .created(ZonedDateTime.of(2024, 5, 20, 10, 10, 15, 0, ZoneId.systemDefault()))
                        .build()));

        ItemRequestDtoResponse requestById = itemRequestService.getRequestById(1L, 1L);

        Assertions.assertNotNull(requestById);
        Assertions.assertEquals(1, requestById.getId());
        Assertions.assertEquals("testDescription1", requestById.getDescription());
        Assertions.assertEquals(ZonedDateTime.of(2024, 5, 20, 10, 10, 15, 0, ZoneId.systemDefault()).toLocalDateTime(), requestById.getCreated());
        Assertions.assertNull(requestById.getItems());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }

    @Test
    void getRequestByIdTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя получить запрос c id 1 не существующим пользователем с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> itemRequestService.getRequestById(1L, 1L));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя получить запрос c id 1 не существующим пользователем с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(0)).findById(Mockito.anyLong());
    }

    @Test
    void getRequestByIdTestNotValidRequestNotFound() {
        Mockito.when(itemRequestRepository.findById(Mockito.anyLong()))
                .thenReturn(Optional.empty());

        Throwable throwable = Assertions.assertThrows(NotFoundItemRequestException.class, () -> itemRequestService.getRequestById(1L, 1L));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя получить не существующий запрос с id 1 пользователем с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(Mockito.anyLong());
    }
}