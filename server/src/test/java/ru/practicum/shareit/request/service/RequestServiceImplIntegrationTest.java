package ru.practicum.shareit.request.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RequestServiceImplIntegrationTest {
    private final RequestService requestService;
    private final UserService userService;
    private ItemRequestDto itemRequestDto;
    private ItemRequestShortDto itemRequestShortDto;

    @BeforeEach
    void setUp() {
        UserDto userDto = new UserDto(null, "user", "user@user.com");
        userService.add(userDto);
        itemRequestShortDto = new ItemRequestShortDto("Дрель");
        itemRequestDto = requestService.add(itemRequestShortDto, 1L);
    }

    @Test
    void shouldAdd() {
        assertNotNull(itemRequestDto.getId());
        assertEquals(itemRequestShortDto.getDescription(), itemRequestDto.getDescription());
    }

    @Test
    void shouldGetByUserId() {
        List<ItemRequestDto> itemRequestDtoList = requestService.getByUserId(1L);
        assertNotNull(itemRequestDtoList);
        assertEquals(1, itemRequestDtoList.size());
    }

    @Test
    void shouldGetAllRequests() {
        userService.add(new UserDto(null, "user2", "user2@user.com"));
        requestService.add(new ItemRequestShortDto("Дрель2"), 1L);
        List<ItemRequestDto> itemRequestDtoList = requestService.getAll(2L, 0, 10);
        requestService.getAll(2L, 1, 10);

        assertNotNull(itemRequestDtoList);
        assertEquals(2, itemRequestDtoList.size());
    }

    @Test
    void shouldGetRequestById() {
        ItemRequestDto actualItemRequestDto = requestService.get(1L, 1L);
        assertNotNull(itemRequestDto);
        assertEquals(itemRequestDto.getId(), actualItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), actualItemRequestDto.getDescription());
        assertEquals(itemRequestDto.getItems(), actualItemRequestDto.getItems());
    }
}