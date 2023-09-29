package ru.practicum.shareit.request.service;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;

import java.util.List;

public interface RequestService {
    ItemRequestDto add(ItemRequestShortDto itemRequestShortDto, Long userId);

    ItemRequestDto get(Long userId, Long requestId);

    List<ItemRequestDto> getByUserId(Long userId);

    List<ItemRequestDto> getAll(Long userId, Integer from, Integer size);
}
