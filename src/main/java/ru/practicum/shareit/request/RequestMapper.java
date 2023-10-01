package ru.practicum.shareit.request;

import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class RequestMapper {
    public static ItemRequest toItemRequest(ItemRequestShortDto itemRequestShortDto, User user) {
        return ItemRequest
                .builder()
                .description(itemRequestShortDto.getDescription())
                .created(LocalDateTime.now())
                .requestor(user)
                .build();
    }

    public static ItemRequestDto toItemRequestDto(ItemRequest itemRequest, List<Item> items) {
        return new ItemRequestDto(
                itemRequest.getId(),
                itemRequest.getDescription(),
                itemRequest.getCreated(),
                items.stream().map(ItemMapper::toItemDto).collect(Collectors.toList())
        );
    }
}
