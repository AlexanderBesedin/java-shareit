package ru.practicum.shareit.request.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestShortDto;
import ru.practicum.shareit.request.service.RequestService;

import java.util.List;

/**
 * TODO Sprint add-item-requests.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/requests")
public class ItemRequestController {
    private final RequestService requestService;

    @PostMapping
    public ItemRequestDto create(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                 @RequestBody ItemRequestShortDto itemRequestShortDto) {
        return requestService.add(itemRequestShortDto, userId);
    }

    @GetMapping("/{requestId}")
    public ItemRequestDto getRequestById(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                         @PathVariable(name = "requestId") Long requestId) {
        return requestService.get(userId, requestId);
    }

    @GetMapping("/all")
    public List<ItemRequestDto> getAllRequests(@RequestHeader(name = "X-Sharer-User-Id") Long userId,
                                               @RequestParam(name = "from", defaultValue = "0") Integer from,
                                               @RequestParam(name = "size", defaultValue = "10") Integer size) {
        return requestService.getAll(userId, from, size);
    }

    @GetMapping
    public List<ItemRequestDto> getByUserId(@RequestHeader(name = "X-Sharer-User-Id") Long userId) {
        return requestService.getByUserId(userId);
    }
}
