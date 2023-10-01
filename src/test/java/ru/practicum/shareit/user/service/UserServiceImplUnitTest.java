package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.DuplicateEmailException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplUnitTest {
    @InjectMocks
    private UserServiceImpl userService;

    @Mock
    private UserRepository repository;

    @Test
    void shouldAddUser() {
        User user = new User(1L, "user", "user@user.com");
        when(repository.save(any())).thenReturn(user);
        UserDto createdUser = userService.add(UserMapper.toUserDto(user));
        assertEquals(user, UserMapper.toUser(createdUser));
    }

    @Test
    void shouldUpdateUser() {
        User user = new User(1L, "user", "user@user.com");
        UserDto userDto = new UserDto(1L, "Alex", "Alex@user.com");

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.getReferenceById(anyLong())).thenReturn(user);

        UserDto updatedUser = userService.update(1L, userDto);
        assertEquals(userDto, updatedUser);
    }

    @Test
    void shouldThrowsIfUpdateNotExistUser() {
        UserDto userDto = new UserDto(1L, "Alex", "Alex@user.com");
        when(repository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.update(1L, userDto));
    }

    @Test
    void shouldThrowsIfUpdateUserIdIsNull() {
        UserDto userDto = new UserDto(1L, "Alex", "Alex@user.com");
        assertThrows(NotFoundException.class, () -> userService.update(null, userDto));
    }

    @Test
    void shouldThrowsIfUpdatedUserWithDuplicatedEmail() {
        User user1 = new User(1L, "user", "user@user.com");
        User user2 = new User(2L, "Alex", "Alex@user.com");
        UserDto userDto = new UserDto(2L, "Alex", "user@user.com");

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findAll()).thenReturn(List.of(user1, user2));

        assertThrows(DuplicateEmailException.class, () -> userService.update(2L, userDto));
    }

    @Test
    void shouldNotThrowsIfUserWithDuplicatedEmailUpdateMyself() {
        User user1 = new User(1L, "user", "user@user.com");
        User user2 = new User(2L, "Alex", "Alex@user.com");
        UserDto userDto = new UserDto(1L, "Alex", "user@user.com");

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.findAll()).thenReturn(List.of(user1, user2));
        when(repository.getReferenceById(anyLong())).thenReturn(user1);

        assertDoesNotThrow(() -> userService.update(1L, userDto));
    }

    @Test
    void shouldNotIfNameAndEmailIsNull() {
        User user = new User(1L, "user", "user@user.com");
        UserDto userDto = new UserDto(1L, null, null);

        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.getReferenceById(anyLong())).thenReturn(user);

        UserDto actualUser = userService.update(1L, userDto);
        assertNotEquals(userDto, actualUser);
    }

    @Test
    void shouldGetUser() {
        User user = new User(1L, "user", "user@user.com");
        when(repository.existsById(anyLong())).thenReturn(true);
        when(repository.getReferenceById(anyLong())).thenReturn(user);

        assertEquals(user, UserMapper.toUser(userService.get(1L)));
    }

    @Test
    void shouldThrowsIfGetNotExistUser() {
        when(repository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.get(1L));
    }

    @Test
    void shouldThrowsIfGetUserIdIsNull() {
        assertThrows(NotFoundException.class, () -> userService.get(null));
    }

    @Test
    void shouldGetAllUsers() {
        User user1 = new User(1L, "user", "user@user.com");
        User user2 = new User(2L, "Alex", "Alex@user.com");
        when(repository.findAll()).thenReturn(List.of(user1, user2));

        assertEquals(userService.getAll(), List.of(UserMapper.toUserDto(user1), UserMapper.toUserDto(user2)));
    }

    @Test
    void shouldDeleteUser() {
        Long id = 1L;
        when(repository.existsById(anyLong())).thenReturn(true);
        userService.delete(id);
        verify(repository, Mockito.times(1)).deleteById(id);
    }

    @Test
    void shouldThrowsIfDeleteNotExistUser() {
        when(repository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> userService.delete(1L));
    }

    @Test
    void shouldThrowsIfDeletedUserIdIsNull() {
        assertThrows(NotFoundException.class, () -> userService.delete(null));
    }
}