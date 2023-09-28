package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;

import java.util.List;

public interface ItemService {
    ItemDto add(Long owner, ItemDto itemDto);

    ItemDto update(Long id, Long owner, ItemDto itemDto);

    ItemWithBookingDto get(Long id, Long userId);

    List<ItemWithBookingDto> getAllByUser(Long owner);

    List<ItemDto> getAllByText(String text, Long renter);

    Comment addComment(Long userId, Long itemId, CommentDto commentDto);
}