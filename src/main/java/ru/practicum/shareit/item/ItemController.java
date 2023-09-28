package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.Valid;
import java.util.List;
import java.util.Set;

/**
 * TODO Sprint add-controllers.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    @PostMapping
    public ItemDto add(@Valid @RequestBody ItemDto itemDto, @RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.add(owner, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ItemDto update(@Valid @PathVariable("itemId") Long id, @RequestBody ItemDto itemDto,
                          @RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.update(id, owner, itemDto);
    }

    @GetMapping("/{itemId}")
    public ItemDto get(@RequestHeader("X-Sharer-User-Id") Long owner, @PathVariable("itemId") Long id) {
        return itemService.get(id, owner);
    }

    @GetMapping
    public Set<ItemDto> getAllByUser(@RequestHeader("X-Sharer-User-Id") Long owner) {
        return itemService.getAllByUser(owner);
    }

    @GetMapping("/search")
    public List<ItemDto> getAllByText(@RequestHeader("X-Sharer-User-Id") Long owner,
                                      @RequestParam(value = "text") String text) {
        return itemService.getAllByText(text, owner);
    }
}
