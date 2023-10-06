package ru.practicum.shareit.booking.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplUnitTest {
    @InjectMocks
    private BookingServiceImpl bookingService;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserService userService;
    private BookingDto bookingDto;
    private User user1;
    private User user2;
    private Item item;

    @BeforeEach
    void setUp() {
        user1 = new User(1L, "user", "user@user.com");
        user2 = new User(2L, "user2", "user2@user.com");
        item = new Item(1L, "Дрель", "Ударная дрель", true, user1, null);
        bookingDto = new BookingDto(null, LocalDateTime.now().plusDays(1), LocalDateTime.now().plusDays(2),
                1L, UserMapper.toUserDto(user2), ItemMapper.toItemDto(item), Status.WAITING);
    }

    @Test
    void shouldThrowIfItemIsNotExist() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());
        bookingDto.setItem(null);
        bookingDto.setItemId(8L);
        assertThrows(NotFoundException.class, () -> bookingService.add(bookingDto, 1L));
    }

    @Test
    void shouldThrowIfItemIsNotAvailable() {
        item.setAvailable(false);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2L));
    }

    @Test
    void shouldThrowIfCreatorIsOwner() {
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.get(1L)).thenReturn(UserMapper.toUserDto(user1));
        assertThrows(NotOwnerException.class, () -> bookingService.add(bookingDto, 1L));
    }

    @Test
    void shouldThrowIfCreateWithEmptyStart() {
        bookingDto.setStart(null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.get(2L)).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2L));
    }

    @Test
    void shouldThrowIfCreateWithEmptyEnd() {
        bookingDto.setEnd(null);
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.get(2L)).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2L));
    }

    @Test
    void shouldThrowIfCreateWithStartAfterEnd() {
        bookingDto.setStart(LocalDateTime.now().plusDays(2));
        bookingDto.setEnd(LocalDateTime.now().plusDays(1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.get(2L)).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2L));
    }

    @Test
    void shouldThrowIfCreateWithStartBeforeNow() {
        bookingDto.setStart(LocalDateTime.now().minusDays(1));
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.get(2L)).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2L));
    }

    @Test
    void shouldThrowIfCreateWithStartEqualsEnd() {
        bookingDto.setStart(LocalDateTime.now());
        bookingDto.setEnd(LocalDateTime.now());
        when(itemRepository.findById(anyLong())).thenReturn(Optional.ofNullable(item));
        when(userService.get(2L)).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(BookingException.class, () -> bookingService.add(bookingDto, 2L));
    }

    @Test
    void shouldThrowIfUpdateNotExistBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(BookingException.class, () -> bookingService.update(3L, 2L, true));
    }

    @Test
    void shouldThrowIfUpdateNotOwner() {
        bookingDto.setId(1L);
        Booking booking = BookingMapper.toBooking(bookingDto, user2, item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user1));

        assertThrows(NotOwnerException.class, () -> bookingService.update(1L, 2L, true));
    }

    @Test
    void shouldThrowIfUpdateWithStatusApproved() {
        bookingDto.setId(1L);
        bookingDto.setStatus(Status.APPROVED);
        Booking booking = BookingMapper.toBooking(bookingDto, user2, item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(ValidationException.class, () -> bookingService.update(1L, 1L, true));
    }

    @Test
    void shouldUpdateWithApprovedIsFalse() {
        bookingDto.setId(1L);
        Booking booking = BookingMapper.toBooking(bookingDto, user2, item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user2));
        when(bookingRepository.save(any())).thenReturn(BookingMapper.toBooking(bookingDto, user2, item));

        assertDoesNotThrow(() -> bookingService.update(1L, 1L, false));
    }

    @Test
    void shouldGetByOwner() {
        bookingDto.setId(1L);
        Booking booking = BookingMapper.toBooking(bookingDto, user2, item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user1));

        assertDoesNotThrow(() -> bookingService.get(1L, 1L));
    }

    @Test
    void shouldThrowIfAskNotExistBooking() {
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> bookingService.get(3L, 2L));
    }

    @Test
    void shouldThrowIfAskBookingWithNotOwnerId() {
        bookingDto.setId(1L);
        Booking booking = BookingMapper.toBooking(bookingDto, user2, item);
        when(bookingRepository.findById(anyLong())).thenReturn(Optional.ofNullable(booking));
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user2));

        assertThrows(NotOwnerException.class, () -> bookingService.get(1L, 3L));
    }

    @Test
    void shouldThrowIfAskAllBookingsForUserWithUnknownState() {
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user1));
        assertThrows(ValidationException.class,
                () -> bookingService.getAllByUser("Unknown", 1L, 2, 1));
    }

    @Test
    void shouldThrowIfAskAllBookingsForOwnerWithUnknownState() {
        when(userService.get(anyLong())).thenReturn(UserMapper.toUserDto(user2));
        assertThrows(ValidationException.class,
                () -> bookingService.getAllByOwner("Unknown", 2L, 2, 1));
    }
}