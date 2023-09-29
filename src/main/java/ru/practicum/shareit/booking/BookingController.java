package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.service.BookingService;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;

/**
 * TODO Sprint add-bookings.
 */
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
public class BookingController {
    private final BookingService bookingService;

    @PostMapping
    public BookingDto add(@RequestBody BookingDto bookingDto, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.add(bookingDto, userId);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId,
                             @RequestParam Boolean approved) {
        return bookingService.update(bookingId, userId, approved);
    }

    @GetMapping("/{bookingId}") // либо арендатор, либо владелец вещи
    public BookingDto get(@PathVariable Long bookingId, @RequestHeader("X-Sharer-User-Id") Long userId) {
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingDto> getAllByUser(@RequestParam(required = false, defaultValue = "ALL") String state,
                                         @RequestHeader("X-Sharer-User-Id") Long userId,
                                         @RequestParam(name = "from", defaultValue = "0")
                                         @PositiveOrZero Integer from,
                                         @RequestParam(name = "size", defaultValue = "10")
                                         @Positive Integer size) {
        return bookingService.getAllByUser(state, userId, from, size);
    }

    @GetMapping("/owner")
    public List<BookingDto> getAllByOwner(@RequestParam(required = false, defaultValue = "ALL") String state,
                                          @RequestHeader("X-Sharer-User-Id") Long ownerId,
                                          @RequestParam(name = "from", defaultValue = "0")
                                          @PositiveOrZero Integer from,
                                          @RequestParam(name = "size", defaultValue = "10")
                                          @Positive Integer size) {
        return bookingService.getAllByOwner(state, ownerId, from, size);
    }
}
