package ru.practicum.shareit.exception;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.booking.service.BookingServiceImpl;
import ru.practicum.shareit.user.service.UserServiceImpl;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ExceptionHandlersUnitTest {
    @InjectMocks
    private ExceptionHandlers handler;
    @Mock
    private UserServiceImpl userServiceimpl;
    @Mock
    private BookingServiceImpl bookingServiceImpl;

    @Test
    void handleBadRequestTest() {
        when(bookingServiceImpl.add(any(), anyLong()))
                .thenThrow(new BookingException("The item for booking is not available"));
        BookingException e = assertThrows(BookingException.class, () -> bookingServiceImpl.add(any(), anyLong()));

        assertEquals("The item for booking is not available", handler.handleBadRequest(e).get("error"));
    }

    @Test
    void handleNotFoundTest() {
        when(userServiceimpl.update(anyLong(), any()))
                .thenThrow(new NotFoundException("Cannot update a non-existent user"));
        NotFoundException e = assertThrows(NotFoundException.class, () -> userServiceimpl.update(anyLong(), any()));
        assertEquals("Cannot update a non-existent user", handler.handleNotFound(e).get("error"));
    }

    @Test
    void handleDuplicateEmailTest() {
        when(userServiceimpl.update(anyLong(), any()))
                .thenThrow(new DuplicateEmailException("Email cannot be duplicated"));
        DuplicateEmailException e = assertThrows(DuplicateEmailException.class,
                () -> userServiceimpl.update(anyLong(), any()));
        assertEquals("Email cannot be duplicated", handler.handleDuplicateEmail(e).get("error"));
    }
}