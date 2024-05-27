package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.checker.UserChecker;
import ru.practicum.shareit.exception.NotFoundUserException;
import ru.practicum.shareit.user.dto.UserRequestDto;
import ru.practicum.shareit.user.dto.UserResponseDto;
import ru.practicum.shareit.user.exeption.UserRepositoryException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.model.UserSort;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

/**
 * @author Nikolay Radzivon
 * @Date 26.05.2024
 */
class UserServiceImplTest {
    private UserService userService;

    private UserRepository userRepository;
    private UserChecker userChecker;

    @BeforeEach
    void setUp() {
        userRepository = Mockito.mock(UserRepository.class);
        userChecker = Mockito.mock(UserChecker.class);

        userService = new UserServiceImpl(userRepository, userChecker);
    }

    @Test
    void addNewUserTestValid() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build());

        UserResponseDto testUser = userService.addNewUser(UserRequestDto.builder()
                .name("testUser")
                .email("testUser@email.com")
                .build());

        Assertions.assertNotNull(testUser);
        Assertions.assertEquals(1, testUser.getId());
        Assertions.assertEquals("testUser", testUser.getName());
        Assertions.assertEquals("testUser@email.com", testUser.getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void addNewUserTestNotValidEmailExists() {
        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new RuntimeException());

        Throwable throwable = Assertions.assertThrows(UserRepositoryException.class, () -> userService.addNewUser(UserRequestDto.builder()
                .name("testUser")
                .email("testUser@email.com")
                .build()));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя добавить нового пользователя, Пользователь с email testUser@email.com уже существует", throwable.getMessage());

        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserTestValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build());

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("UpdateTestUser")
                        .email("UpdateTestUser@email.com")
                        .build());

        UserResponseDto testUser = userService.updateUser(UserRequestDto.builder()
                .name("UpdateTestUser")
                .email("UpdateTestUser@email.com")
                .build(), 1);

        Assertions.assertNotNull(testUser);
        Assertions.assertEquals("UpdateTestUser", testUser.getName());
        Assertions.assertEquals("UpdateTestUser@email.com", testUser.getEmail());
        Assertions.assertEquals(1, testUser.getId());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя обновить не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> userService.updateUser(UserRequestDto.builder()
                .name("testUser")
                .email("testUser@email.com")
                .build(), 1));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(0)).save(Mockito.any(User.class));
    }

    @Test
    void updateUserTestNotValidEmailExists() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build());

        Mockito.when(userRepository.save(Mockito.any(User.class)))
                .thenThrow(new RuntimeException());

        Throwable throwable = Assertions.assertThrows(UserRepositoryException.class, () -> userService.updateUser(UserRequestDto.builder()
                .name("testUser")
                .email("testUser@email.com")
                .build(), 1));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя обновить пользователя с id 1, Пользователь с email testUser@email.com уже существует", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1)).save(Mockito.any(User.class));
    }

    @Test
    void getUserTestValid() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenReturn(User.builder()
                        .id(1L)
                        .name("testUser")
                        .email("testUser@email.com")
                        .build());

        UserResponseDto user = userService.getUser(1);

        Assertions.assertNotNull(user);
        Assertions.assertEquals(1, user.getId());
        Assertions.assertEquals("testUser", user.getName());
        Assertions.assertEquals("testUser@email.com", user.getEmail());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getUserTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя получить не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> userService.getUser(1));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя получить не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void deleteUserTestValid() {
        userService.deleteUser(1);

        Mockito.verify(userChecker, Mockito.times(1))
                .checkUser(Mockito.anyLong(), Mockito.anyString());
        Mockito.verify(userRepository, Mockito.times(1))
                .deleteById(Mockito.anyLong());
    }

    @Test
    void deleteUserTestNotValidUserNotFound() {
        Mockito.when(userChecker.checkUser(Mockito.anyLong(), Mockito.anyString()))
                .thenThrow(new NotFoundUserException("Нельзя удалить не существующего пользователя с id 1"));

        Throwable throwable = Assertions.assertThrows(NotFoundUserException.class, () -> userService.deleteUser(1));

        Assertions.assertNotNull(throwable);
        Assertions.assertNotNull(throwable.getMessage());
        Assertions.assertEquals("Нельзя удалить не существующего пользователя с id 1", throwable.getMessage());

        Mockito.verify(userChecker, Mockito.times(1)).checkUser(Mockito.anyLong(), Mockito.anyString());
    }

    @Test
    void getAllUsersTestValidSortNone() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(1L)
                                .name("testUser1")
                                .email("testUser1@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("testUser2")
                                .email("testUser2@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("testUser3")
                                .email("testUser3@email.com")
                                .build(),
                        User.builder()
                                .id(4L)
                                .name("testUser4")
                                .email("testUser4@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.NONE);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());
        Assertions.assertEquals(1, allUsers.get(0).getId());
        Assertions.assertEquals("testUser1", allUsers.get(0).getName());
        Assertions.assertEquals("testUser1@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(2, allUsers.get(1).getId());
        Assertions.assertEquals("testUser2", allUsers.get(1).getName());
        Assertions.assertEquals("testUser2@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(3, allUsers.get(2).getId());
        Assertions.assertEquals("testUser3", allUsers.get(2).getName());
        Assertions.assertEquals("testUser3@email.com", allUsers.get(2).getEmail());


        Assertions.assertEquals(4, allUsers.get(3).getId());
        Assertions.assertEquals("testUser4", allUsers.get(3).getName());
        Assertions.assertEquals("testUser4@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllUsersTestValidSortID_ASC() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(1L)
                                .name("testUser1")
                                .email("testUser1@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("testUser2")
                                .email("testUser2@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("testUser3")
                                .email("testUser3@email.com")
                                .build(),
                        User.builder()
                                .id(4L)
                                .name("testUser4")
                                .email("testUser4@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.ID_ASC);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());
        Assertions.assertEquals(1, allUsers.get(0).getId());
        Assertions.assertEquals("testUser1", allUsers.get(0).getName());
        Assertions.assertEquals("testUser1@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(2, allUsers.get(1).getId());
        Assertions.assertEquals("testUser2", allUsers.get(1).getName());
        Assertions.assertEquals("testUser2@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(3, allUsers.get(2).getId());
        Assertions.assertEquals("testUser3", allUsers.get(2).getName());
        Assertions.assertEquals("testUser3@email.com", allUsers.get(2).getEmail());


        Assertions.assertEquals(4, allUsers.get(3).getId());
        Assertions.assertEquals("testUser4", allUsers.get(3).getName());
        Assertions.assertEquals("testUser4@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllUsersTestValidSortID_DESC() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(4L)
                                .name("testUser4")
                                .email("testUser4@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("testUser3")
                                .email("testUser3@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("testUser2")
                                .email("testUser2@email.com")
                                .build(),
                        User.builder()
                                .id(1L)
                                .name("testUser1")
                                .email("testUser1@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.ID_DESC);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());

        Assertions.assertEquals(4, allUsers.get(0).getId());
        Assertions.assertEquals("testUser4", allUsers.get(0).getName());
        Assertions.assertEquals("testUser4@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(3, allUsers.get(1).getId());
        Assertions.assertEquals("testUser3", allUsers.get(1).getName());
        Assertions.assertEquals("testUser3@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(2, allUsers.get(2).getId());
        Assertions.assertEquals("testUser2", allUsers.get(2).getName());
        Assertions.assertEquals("testUser2@email.com", allUsers.get(2).getEmail());

        Assertions.assertEquals(1, allUsers.get(3).getId());
        Assertions.assertEquals("testUser1", allUsers.get(3).getName());
        Assertions.assertEquals("testUser1@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }


    @Test
    void getAllUsersTestValidSortNAME_ASC() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(1L)
                                .name("aTestUser1")
                                .email("testUser1@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("bTestUser2")
                                .email("testUser2@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("cTestUser3")
                                .email("testUser3@email.com")
                                .build(),
                        User.builder()
                                .id(4L)
                                .name("dTestUser4")
                                .email("testUser4@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.NAME_ASC);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());

        Assertions.assertEquals(1, allUsers.get(0).getId());
        Assertions.assertEquals("aTestUser1", allUsers.get(0).getName());
        Assertions.assertEquals("testUser1@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(2, allUsers.get(1).getId());
        Assertions.assertEquals("bTestUser2", allUsers.get(1).getName());
        Assertions.assertEquals("testUser2@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(3, allUsers.get(2).getId());
        Assertions.assertEquals("cTestUser3", allUsers.get(2).getName());
        Assertions.assertEquals("testUser3@email.com", allUsers.get(2).getEmail());


        Assertions.assertEquals(4, allUsers.get(3).getId());
        Assertions.assertEquals("dTestUser4", allUsers.get(3).getName());
        Assertions.assertEquals("testUser4@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllUsersTestValidSortNAME_DESC() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(1L)
                                .name("dTestUser1")
                                .email("testUser1@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("cTestUser2")
                                .email("testUser2@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("bTestUser3")
                                .email("testUser3@email.com")
                                .build(),
                        User.builder()
                                .id(4L)
                                .name("aTestUser4")
                                .email("testUser4@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.NAME_DESC);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());

        Assertions.assertEquals(1, allUsers.get(0).getId());
        Assertions.assertEquals("dTestUser1", allUsers.get(0).getName());
        Assertions.assertEquals("testUser1@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(2, allUsers.get(1).getId());
        Assertions.assertEquals("cTestUser2", allUsers.get(1).getName());
        Assertions.assertEquals("testUser2@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(3, allUsers.get(2).getId());
        Assertions.assertEquals("bTestUser3", allUsers.get(2).getName());
        Assertions.assertEquals("testUser3@email.com", allUsers.get(2).getEmail());


        Assertions.assertEquals(4, allUsers.get(3).getId());
        Assertions.assertEquals("aTestUser4", allUsers.get(3).getName());
        Assertions.assertEquals("testUser4@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllUsersTestValidSortEMAIL_DESC() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(1L)
                                .name("testUser1")
                                .email("dTestUser1@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("testUser2")
                                .email("cTestUser2@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("testUser3")
                                .email("bTestUser3@email.com")
                                .build(),
                        User.builder()
                                .id(4L)
                                .name("testUser4")
                                .email("aTestUser4@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.EMAIL_DESC);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());

        Assertions.assertEquals(1, allUsers.get(0).getId());
        Assertions.assertEquals("testUser1", allUsers.get(0).getName());
        Assertions.assertEquals("dTestUser1@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(2, allUsers.get(1).getId());
        Assertions.assertEquals("testUser2", allUsers.get(1).getName());
        Assertions.assertEquals("cTestUser2@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(3, allUsers.get(2).getId());
        Assertions.assertEquals("testUser3", allUsers.get(2).getName());
        Assertions.assertEquals("bTestUser3@email.com", allUsers.get(2).getEmail());

        Assertions.assertEquals(4, allUsers.get(3).getId());
        Assertions.assertEquals("testUser4", allUsers.get(3).getName());
        Assertions.assertEquals("aTestUser4@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }

    @Test
    void getAllUsersTestValidSortEMAIL_ASC() {
        Mockito.when(userRepository.findAll(Mockito.any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(User.builder()
                                .id(1L)
                                .name("testUser1")
                                .email("aTestUser1@email.com")
                                .build(),
                        User.builder()
                                .id(2L)
                                .name("testUser2")
                                .email("bTestUser2@email.com")
                                .build(),
                        User.builder()
                                .id(3L)
                                .name("testUser3")
                                .email("cTestUser3@email.com")
                                .build(),
                        User.builder()
                                .id(4L)
                                .name("testUser4")
                                .email("dTestUser4@email.com")
                                .build()
                )));

        List<UserResponseDto> allUsers = userService.getAllUsers(1, 4, UserSort.EMAIL_ASC);

        Assertions.assertNotNull(allUsers);
        Assertions.assertEquals(4, allUsers.size());

        Assertions.assertEquals(1, allUsers.get(0).getId());
        Assertions.assertEquals("testUser1", allUsers.get(0).getName());
        Assertions.assertEquals("aTestUser1@email.com", allUsers.get(0).getEmail());

        Assertions.assertEquals(2, allUsers.get(1).getId());
        Assertions.assertEquals("testUser2", allUsers.get(1).getName());
        Assertions.assertEquals("bTestUser2@email.com", allUsers.get(1).getEmail());

        Assertions.assertEquals(3, allUsers.get(2).getId());
        Assertions.assertEquals("testUser3", allUsers.get(2).getName());
        Assertions.assertEquals("cTestUser3@email.com", allUsers.get(2).getEmail());

        Assertions.assertEquals(4, allUsers.get(3).getId());
        Assertions.assertEquals("testUser4", allUsers.get(3).getName());
        Assertions.assertEquals("dTestUser4@email.com", allUsers.get(3).getEmail());

        Mockito.verify(userRepository, Mockito.times(1)).findAll(Mockito.any(Pageable.class));
    }
}