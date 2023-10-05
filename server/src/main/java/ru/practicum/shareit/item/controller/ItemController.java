package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemWithBookingDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.add(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@PathVariable("itemId") Long id, @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.update(id, owner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemWithBookingDto get(@RequestHeader("X-Sharer-User-Id") Long owner, @PathVariable("itemId") Long id) {
        return itemService.get(id, owner);
    }

    @GetMapping
    public List<ItemWithBookingDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.getAllByUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllByText(@RequestHeader("X-Sharer-User-Id") Long owner,
                                      @RequestParam(value = "text") String text) {
        return itemService.getAllByText(text, owner);
    }

    @PostMapping("/{itemId}/comment")
    public Comment addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                              @RequestBody CommentDto commentDto,
                              @PathVariable("itemId") Long itemId) {
        return itemService.addComment(userId, itemId, commentDto);
    }
}
