package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserServiceImplIntegrationTest {
    private final UserService userService;
    private UserDto userDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "user", "user@user.com");
    }

    @Test
    void shouldAdd() {
        UserDto user = userService.add(userDto);

        assertNotNull(user.getId());
        assertEquals(userDto.getEmail(), user.getEmail());
        assertEquals(userDto.getName(), user.getName());
    }

    @Test
    void shouldUpdate() {
        userService.add(userDto);
        UserDto updateUser = new UserDto(null, "update", "update@user.com");
        Long userId = 1L;
        UserDto actualUser = userService.update(userId, updateUser);

        assertEquals(updateUser.getName(), actualUser.getName());
        assertEquals(updateUser.getEmail(), actualUser.getEmail());
        assertEquals(userId, actualUser.getId());
    }

    @Test
    void shouldGetById() {
        UserDto user = userService.add(userDto);
        assertNotNull(user.getId());
        UserDto actualUser = userService.get(user.getId());

        assertEquals(user, actualUser);
    }

    @Test
    void shouldGetAll() {
        UserDto userDto2 = new UserDto(null,"user2", "user2@user.com");
        UserDto user = userService.add(userDto);
        UserDto user2 = userService.add(userDto2);
        List<UserDto> expectedUsers = List.of(user, user2);
        List<UserDto> actualUsers = userService.getAll();

        assertEquals(expectedUsers, actualUsers);
    }

    @Test
    void shouldDeleteById() {
        UserDto user = userService.add(userDto);
        userService.delete(user.getId());

        assertEquals(new ArrayList<>(), userService.getAll());
    }
}