package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;
import java.util.Set;

public interface ItemService {
    ItemDto add(Long owner, ItemDto itemDto);

    ItemDto update(Long id, Long owner, ItemDto itemDto);

    ItemDto get(Long id, Long owner);

    Set<ItemDto> getAllByUser(Long owner);

    List<ItemDto> getAllByText(String text, Long renter);
}