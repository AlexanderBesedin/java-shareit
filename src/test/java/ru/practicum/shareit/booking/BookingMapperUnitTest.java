package ru.practicum.shareit.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BookingMapperUnitTest {
    private User booker;
    private User owner;
    private Item item;
    private Booking booking;
    BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        booker = new User(1L, "user", "user@user.com");
        owner = new User(2L, "user1", "user1@user.com");
        item = new Item(1L, "item", "some item", true, owner, null);
        booking = new Booking(1L, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusDays(1),
                item, booker, Status.WAITING);
        bookingDto = new BookingDto(1L, LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusDays(1),
                item.getId(), UserMapper.toUserDto(booker), ItemMapper.toItemDto(item), Status.WAITING);
    }

    @Test
    void shouldToBookingDto() {
        BookingDto newBookingDto = BookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), newBookingDto.getId());
        assertEquals(booking.getStart(), newBookingDto.getStart());
        assertEquals(booking.getEnd(), newBookingDto.getEnd());
        assertEquals(ItemMapper.toItemDto(booking.getItem()), newBookingDto.getItem());
        assertEquals(UserMapper.toUserDto(booking.getBooker()), newBookingDto.getBooker());
        assertEquals(booking.getStatus(), newBookingDto.getStatus());
    }

    @Test
    void shouldToBooking() {
        Booking newBooking = BookingMapper.toBooking(bookingDto, booker, item);

        assertEquals(bookingDto.getId(), newBooking.getId());
        assertEquals(bookingDto.getStart(), newBooking.getStart());
        assertEquals(bookingDto.getEnd(), newBooking.getEnd());
        assertEquals(ItemMapper.toItem(owner, bookingDto.getItem(), null), newBooking.getItem());
        assertEquals(UserMapper.toUser(bookingDto.getBooker()), newBooking.getBooker());
        assertEquals(booking.getStatus(), newBooking.getStatus());
    }

    @Test
    void shouldToBookingForItemDto() {
        BookingForItemDto bookingForItemDto = BookingMapper.toBookingForItemDto(booking);

        assertEquals(booking.getId(), bookingForItemDto.getId());
        assertEquals(booking.getStart(), bookingForItemDto.getStart());
        assertEquals(booking.getEnd(), bookingForItemDto.getEnd());
        assertEquals(booking.getBooker().getId(), bookingForItemDto.getBookerId());
        assertEquals(booking.getStatus(), bookingForItemDto.getBookingStatus());
    }

}