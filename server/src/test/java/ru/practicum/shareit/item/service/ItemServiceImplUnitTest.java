package ru.practicum.shareit.item.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplUnitTest {
    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private RequestRepository requestRepository;
    private Item item;
    private User user;
    private ItemRequest request;

    @BeforeEach
    void setUp() {
        user = new User(1L, "user", "user@user.com");
        item = new Item(1L, "item", "some item", true, user, null);
        request = new ItemRequest(
                1L,
                "some item",
                new User(2L, "user1", "user1@user.com"),
                LocalDateTime.now()
        );
    }

    @Test
    void shouldAddItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(requestRepository.getReferenceById(anyLong())).thenReturn(request);
        when(itemRepository.save(any())).thenReturn(item);
        ItemDto itemDto = itemService.add(1L,
                new ItemDto(null, "item", "some item", true, request.getId()));
        assertEquals(itemDto, ItemMapper.toItemDto(item));
    }

    @Test
    void shouldThrowsIfAddItemWithNotExistUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.add(2L, ItemMapper.toItemDto(item)));
    }

    @Test
    void shouldThrowsIfAddItemWithUserIdIsNull() {
        assertThrows(NotFoundException.class, () -> itemService.add(null, ItemMapper.toItemDto(item)));
    }

    @Test
    void shouldUpdateItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerIdAndId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(item));
        when(requestRepository.getReferenceById(anyLong())).thenReturn(request);
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = itemService.update(1L, 1L,
                new ItemDto(1L, "item", "new some item", false, request.getId()));
        assertEquals(itemDto, ItemMapper.toItemDto(item));
    }

    @Test
    void shouldThrowsIfUpdateNotExistItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> itemService.update(2L, 1L,
                        new ItemDto(null, "item", "new some item", true, null)));
    }

    @Test
    void shouldThrowsIfUpdateItemWithNotExistUser() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class,
                () -> itemService.update(2L, 1L,
                        new ItemDto(null, "item", "new some item", true, null)));
    }

    @Test
    void shouldThrowsIfUpdateItemByNotOwner() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerIdAndId(anyLong(), anyLong())).thenReturn(Optional.empty());
        assertThrows(NotOwnerException.class,
                () -> itemService.update(2L, 1L,
                        new ItemDto(null, "item", "new some item", true, null)));
    }

    @Test
    void shouldNotUpdateNullFieldsOfItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.existsById(anyLong())).thenReturn(true);
        when(itemRepository.findByOwnerIdAndId(anyLong(), anyLong())).thenReturn(Optional.ofNullable(item));
        when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDto = new ItemDto(null, null, null, false, null);
        ItemDto updated = itemService.update(1L, 1L, itemDto);
        assertNotEquals(updated, itemDto);
    }

    @Test
    void shouldThrowIfAskNotExistItem() {
        when(itemRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.get(2L, 1L));
    }

    @Test
    void shouldThrowIfAskItemWithNotExistOwner() {
        when(itemRepository.existsById(anyLong())).thenReturn(true);
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.get(2L, 3L));
    }

    @Test
    void shouldThrowIfAskAllItemsWithNotExistOwner() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.getAllByUser(3L));
    }

    @Test
    void shouldGetEmptyListItemsIfTextIsBlank() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        List<ItemDto> itemsDto = itemService.getAllByText("", 1L);
        assertEquals(new ArrayList<>(), itemsDto);
    }

    @Test
    void shouldGetEmptyListItemsIfTextIsNull() {
        when(userRepository.existsById(anyLong())).thenReturn(true);
        List<ItemDto> itemsDto = itemService.getAllByText(null, 1L);
        assertEquals(new ArrayList<>(), itemsDto);
    }

    @Test
    void shouldThrowIfAskAllItemsByNotExistUser() {
        when(userRepository.existsById(anyLong())).thenReturn(false);
        assertThrows(NotFoundException.class, () -> itemService.getAllByText("item", 3L));
    }

    @Test
    void shouldThrowIfAddCommentByNotExistUser() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addComment(3L, 1L, new CommentDto()));
    }

    @Test
    void shouldThrowIfAddCommentForNotExistItem() {
        when(userRepository.findById(anyLong())).thenReturn(Optional.ofNullable(user));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> itemService.addComment(3L, 2L, new CommentDto()));
    }
}