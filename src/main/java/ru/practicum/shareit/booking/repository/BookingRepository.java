package ru.practicum.shareit.booking.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(Long bookerId,
                                                                              LocalDateTime start, LocalDateTime end);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end);

    List<Booking> findByItemOwnerOrderByStartDesc(Long owner);

    List<Booking> findByItemOwnerAndStartIsAfterOrderByStartDesc(Long owner, LocalDateTime start);

    List<Booking> findByItemOwnerAndStatusOrderByStartDesc(Long owner, Status bookingStatus);

    List<Booking> findByItemOwnerAndStartBeforeAndEndAfterOrderByStartDesc(Long owner,
                                                                           LocalDateTime start, LocalDateTime end);

    List<Booking> findByItemOwnerAndEndBeforeOrderByStartDesc(Long owner, LocalDateTime end);

    List<Booking> findByItemOwner(Long ownerId);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime time);
}
