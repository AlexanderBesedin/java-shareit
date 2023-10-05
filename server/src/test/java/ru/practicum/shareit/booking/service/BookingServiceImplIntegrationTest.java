package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@RequiredArgsConstructor(onConstructor_ = @Autowired)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class BookingServiceImplIntegrationTest {
    private final BookingService bookingService;
    private final UserService userService;
    private final ItemService itemService;
    private BookingDto bookingDto;

    @BeforeEach
    void setUp() {
        userService.add(new UserDto(null, "user", "user@user.com"));
        UserDto booker = userService.add(new UserDto(null, "user2", "user2@user.com"));
        ItemDto item = itemService.add(1L,
                new ItemDto(null, "Дрель", "Ударная дрель", true, null));
        bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                item.getId(), booker, item, Status.WAITING);
    }

    @Test
    void shouldCreate() {
        BookingDto createdBooking = bookingService.add(bookingDto, 2L);
        assertNotNull(bookingService.get(1L, 2L));
        assertEquals(bookingService.get(1L, 2L).getStatus(), createdBooking.getStatus());
        assertEquals(bookingService.get(1L, 2L).getBooker(), createdBooking.getBooker());
        assertEquals(bookingService.get(1L, 2L).getItem(), createdBooking.getItem());
    }

    @Test
    void shouldUpdate() {
        bookingService.add(bookingDto, 2L);
        assertEquals(Status.APPROVED, bookingService.update(1L, 1L, true).getStatus());
    }

    @Test
    void getById() {
        bookingService.add(bookingDto, 2L);
        BookingDto actualBooking = bookingService.get(1L, 2L);

        assertEquals(bookingDto.getItem(), actualBooking.getItem());
        assertEquals(bookingDto.getBooker(), actualBooking.getBooker());
        assertEquals(bookingDto.getStatus(), actualBooking.getStatus());
        assertEquals(bookingDto.getItemId(), actualBooking.getItemId());
    }

    @Test
    void getAllBookingsForUser() {
        bookingService.add(bookingDto, 2L);

        List<BookingDto> bookingDtoList = bookingService.getAllByUser("ALL", 2L, 0, 1);
        assertEquals(1, bookingDtoList.size());

        List<BookingDto> bookingDtoList2 = bookingService.getAllByUser("FUTURE", 2L, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList2);

        List<BookingDto> bookingDtoList3 = bookingService.getAllByUser("REJECTED", 2L, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList3);

        List<BookingDto> bookingDtoList4 = bookingService.getAllByUser("CURRENT", 2L, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList4);

        List<BookingDto> bookingDtoList5 = bookingService.getAllByUser("PAST", 2L, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList5);

        List<BookingDto> bookingDtoList6 = bookingService.getAllByUser("WAITING", 2L, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList6);
    }

    @Test
    void getAllBookingsForOwner() {
        bookingService.add(bookingDto, 2L);

        List<BookingDto> bookingDtoList = bookingService.getAllByOwner("ALL", 1L, 0, 1);
        assertEquals(1, bookingDtoList.size());

        List<BookingDto> bookingDtoList2 = bookingService.getAllByOwner("FUTURE", 1L, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList2);

        List<BookingDto> bookingDtoList3 = bookingService.getAllByOwner("REJECTED", 1L, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList3);

        List<BookingDto> bookingDtoList4 = bookingService.getAllByOwner("CURRENT", 1L, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList4);

        List<BookingDto> bookingDtoList5 = bookingService.getAllByOwner("PAST", 1L, 0, 1);
        assertEquals(new ArrayList<>(), bookingDtoList5);

        List<BookingDto> bookingDtoList6 = bookingService.getAllByOwner("WAITING", 1L, 0, 1);
        assertEquals(bookingDtoList, bookingDtoList6);
    }
}