package ru.practicum.shareit.booking.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.BookingException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.exception.NotOwnerException;
import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final ItemRepository itemRepository;
    private final UserService userService;

    @Override
    @Transactional
    public BookingDto add(BookingDto bookingDto, Long userId) {
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(
                        () -> new NotFoundException("Unable to find item with id = " + bookingDto.getItemId())
                );
        if (!item.getAvailable()) {
            throw new BookingException(item.getName() + " for booking is not available");
        }
        User booker = UserMapper.toUser(userService.get(userId));
        if (userId.equals(item.getOwner())) {
            throw new NotOwnerException("Impossible to book your own item");
        }
        if (bookingDto.getStart() == null || bookingDto.getEnd() == null
                || bookingDto.getStart().equals(bookingDto.getEnd())
                || bookingDto.getStart().isAfter(bookingDto.getEnd())
                || bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new BookingException("Incorrect booking range");
        }
        bookingDto.setStatus(Status.WAITING);
        bookingDto.setItem(item);
        bookingDto.setBooker(booker);

        Booking booking = bookingRepository.save(BookingMapper.toBooking(bookingDto, userId));
        log.info("Booking id = {} of itemId = {} has been added ", booking.getId(), booking.getItem());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long userId, boolean isApproved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new BookingException(String.format("Booking ID = %d does not exist", bookingId))
                );
        userService.get(userId); // проверка существования юзера через метод userService

        if (!userId.equals(booking.getItem().getOwner())) {
            throw new NotOwnerException("Only the owner of the item can confirm the booking");
        }
        if (booking.getStatus().equals(Status.APPROVED)) {
            throw new ValidationException("The approved status has already been set.");
        }
        booking.setStatus(isApproved ? Status.APPROVED : Status.REJECTED);
        log.info("Booking id = {} has been updated with status = {}", booking.getId(), booking.getStatus());
        return BookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    public BookingDto get(Long bookingId, Long userId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(
                        () -> new NotFoundException(String.format("Booking ID = %d does not exist", bookingId))
                );
        userService.get(userId); // проверка существования юзера через метод userService
        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().equals(userId)) {
            throw new NotOwnerException("The requester is not the owner of the item or the booking");
        }
        log.info("Get booking id = {}", booking.getId());
        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public List<BookingDto> getAllByUser(String state, Long bookerId) {
        userService.get(bookerId); // проверка существования юзера через метод userService
        List<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByBookerIdOrderByStartDesc(bookerId);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(bookerId,
                        start, end);
                break;
            case "PAST":
                bookings = bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(bookerId, end);
                break;
            case "FUTURE":
                bookings = bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(bookerId, start);
                break;
            case "WAITING":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository.findByBookerIdAndStatusOrderByStartDesc(bookerId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getAllByOwner(String state, Long ownerId) {
        userService.get(ownerId); // проверка существования юзера через метод userService
        List<Booking> bookings;
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now();
        switch (state) {
            case "ALL":
                bookings = bookingRepository.findByItemOwnerOrderByStartDesc(ownerId);
                break;
            case "FUTURE":
                bookings = bookingRepository
                        .findByItemOwnerAndStartIsAfterOrderByStartDesc(ownerId, start);
                break;
            case "CURRENT":
                bookings = bookingRepository.findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(ownerId,
                        start, end);
                break;
            case "PAST":
                bookings = bookingRepository.findByItemOwnerAndEndBeforeOrderByStartDesc(ownerId, end);
                break;
            case "WAITING":
                bookings = bookingRepository
                        .findByItemOwnerAndStatusOrderByStartDesc(ownerId, Status.WAITING);
                break;
            case "REJECTED":
                bookings = bookingRepository
                        .findByItemOwnerAndStatusOrderByStartDesc(ownerId, Status.REJECTED);
                break;
            default:
                throw new ValidationException("Unknown state: UNSUPPORTED_STATUS");
        }
        return bookings
                .stream()
                .map(BookingMapper::toBookingDto)
                .collect(Collectors.toList());
    }
}
