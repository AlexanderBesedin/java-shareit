package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.CommentRepository;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class ItemServiceImplIntegrationTest {
    private final ItemService itemService;
    private final UserService userService;
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final CommentRepository commentRepository;
    private UserDto userDto;
    private ItemDto itemDto;

    @BeforeEach
    void setUp() {
        userDto = new UserDto(null, "user", "user@user.com");
        itemDto = new ItemDto(null, "item", "some item", true, null);
    }

    @Test
    void shouldAdd() {
        UserDto user = userService.add(userDto);
        assertNotNull(user.getId());

        ItemDto item = itemService.add(user.getId(), itemDto);
        assertNotNull(item.getId());
        itemDto.setId(item.getId());
        assertEquals(item, itemDto);
    }

    @Test
    void shouldUpdate() {
        UserDto user = userService.add(userDto);
        ItemDto item = itemService.add(user.getId(), itemDto);
        assertNotNull(item.getId());

        ItemDto updateItem =
                new ItemDto(null, "item", "new some item", false, null);

        ItemDto actualItem = itemService.update(item.getId(), user.getId(), updateItem);
        assertEquals(updateItem.getName(), actualItem.getName());
        assertEquals(updateItem.getDescription(), actualItem.getDescription());
        assertEquals(updateItem.getAvailable(), actualItem.getAvailable());
    }

    @Test
    void shouldGetById() {
        UserDto user = userService.add(userDto);
        ItemDto item = itemService.add(user.getId(), itemDto);
        assertNotNull(item.getId());
        assertNotNull(itemService.get(1L, 1L));
    }

    @Test
    void shouldGetItemsByOwner() {
        UserDto user = userService.add(userDto);
        itemService.add(user.getId(), itemDto);
        itemService.add(user.getId(),
                new ItemDto(null, "item2", "about item", true, null));
        assertEquals(2, itemService.getAllByUser(user.getId()).size());
    }

    @Test
    void shouldGetItemsBySearch() {
        UserDto user = userService.add(userDto);
        UserDto renter = userService.add(new UserDto(null, "renter", "renter@renter.com"));

        itemService.add(user.getId(), itemDto);
        ItemDto item1 = itemService.update(1L, user.getId(),
                new ItemDto(null, null, "есть ударный режим", true, null));
        ItemDto item2 = itemService.add(user.getId(),
                new ItemDto(null, "Дрель", "ударная дрель", true, null));

        assertEquals(List.of(item1, item2), itemService.getAllByText("удАРн", renter.getId()));
    }

    @Test
    void shouldThrowWhenNotBookerAddComment() {
        UserDto user = userService.add(userDto);
        ItemDto item = itemService.add(user.getId(), itemDto);
        CommentDto comment = new CommentDto(null, "some text", item, "user", LocalDateTime.now());

        assertThrows(BookingException.class, () -> itemService.addComment(user.getId(), item.getId(), comment));
    }

    @Test
    void shouldAddComment() {
        UserDto user = userService.add(userDto);
        UserDto renter = userService.add(new UserDto(null, "renter", "renter@renter.com"));
        Item item = itemRepository.save(
                new Item(null, "item", "some item", true, UserMapper.toUser(user), null));

        Booking booking = new Booking(1L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, UserMapper.toUser(renter), Status.APPROVED);
        bookingRepository.save(booking);
        itemService.addComment(renter.getId(), item.getId(),
                new CommentDto(null, "some text", ItemMapper.toItemDto(item),
                        "renter", null));
        assertFalse(commentRepository.findByItemId(item.getId()).isEmpty());
    }
}