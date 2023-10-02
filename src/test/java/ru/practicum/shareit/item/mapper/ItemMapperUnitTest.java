package ru.practicum.shareit.item.mapper;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertNotNull;

class ItemMapperUnitTest {
    @Test
    void shouldGetItemWithBookingDto() {
        User owner = new User(1L, "user", "user@user.com");
        User booker = new User(1L, "booker", "booker@user.com");
        Item item = new Item(1L, "Дрель", "Ударная дрель", true, owner, null);
        Booking booking = new Booking(1L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.WAITING);
        Booking booking2 = new Booking(2L, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item, booker, Status.APPROVED);
        Booking booking3 = new Booking(2L, LocalDateTime.now().minusDays(2), LocalDateTime.now().minusDays(1),
                item, booker, Status.APPROVED);
        Comment comment = new Comment(1L, "some text", item, "booker", LocalDateTime.now());

        List<Booking> bookings = List.of(booking, booking2, booking3);
        List<Comment> comments = List.of(comment);

        assertNotNull(ItemMapper.toItemDtoWithBooking(item, bookings, booker, comments));
    }
}