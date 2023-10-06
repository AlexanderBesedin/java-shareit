package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;

/**
 * TODO Sprint add-controllers.
 */
@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemClient itemClient;

    @PostMapping
    public ResponseEntity<Object> add(@Valid @RequestBody ItemDto itemDto,
                                      @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Creating item {}, ownerId={}", itemDto, ownerId);
        return itemClient.add(ownerId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@Valid @PathVariable("itemId") Long id,
                                         @RequestBody ItemDto itemDto,
                                         @RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Updating item {}, ownerId={}", itemDto, ownerId);
        return itemClient.update(id, ownerId, itemDto);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> get(@RequestHeader("X-Sharer-User-Id") Long userId, @PathVariable("itemId") Long id) {
        log.info("Get bookingId {}, userId={}", id, userId);
        return itemClient.get(id, userId);
    }

    @GetMapping
    public ResponseEntity<Object> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long ownerId) {
        log.info("Get all items by ownerId={}", ownerId);
        return itemClient.getAllByUser(ownerId);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> getAllByText(@RequestHeader("X-Sharer-User-Id") Long ownerId,
                                               @RequestParam(value = "text") String text) {
        log.info("Get all items contains text={}", text);
        return itemClient.getAllByText(text, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@RequestHeader("X-Sharer-User-Id") Long userId,
                                             @Valid @RequestBody CommentDto commentDto,
                                             @PathVariable("itemId") Long itemId) {
        log.info("Added a comment {} to the itemId = {}", commentDto, itemId);
        return itemClient.addComment(userId, commentDto, itemId);
    }
}
