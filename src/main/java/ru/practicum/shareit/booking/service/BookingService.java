package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    BookingDto add(BookingDto bookingDto, Long userId);

    BookingDto update(Long bookingId, Long userId, boolean isApproved);

    BookingDto get(Long bookingId, Long userId);

    List<BookingDto> getAllByUser(String state, Long userId);

    List<BookingDto> getAllByOwner(String state, Long userId);
}
