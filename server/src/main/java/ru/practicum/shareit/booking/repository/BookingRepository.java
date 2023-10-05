package ru.practicum.shareit.booking.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.item.model.Item;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByBookerIdOrderByStartDesc(Long bookerId, Pageable pageRequest);

    List<Booking> findByBookerIdAndStatusOrderByStartDesc(Long bookerId, Status status, Pageable pageRequest);

    List<Booking> findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStart(
            Long bookerId, LocalDateTime start, LocalDateTime end, Pageable pageRequest);

    List<Booking> findByBookerIdAndStartIsAfterOrderByStartDesc(Long bookerId, LocalDateTime start, Pageable pageRequest);

    List<Booking> findByBookerIdAndEndIsBeforeOrderByStartDesc(Long bookerId, LocalDateTime end, Pageable pageRequest);

    List<Booking> findByItemOwnerIdOrderByStartDesc(Long owner, Pageable pageRequest);

    List<Booking> findByItemOwnerIdAndStartIsAfterOrderByStartDesc(Long owner, LocalDateTime start, Pageable pageRequest);

    List<Booking> findByItemOwnerIdAndStatusOrderByStartDesc(Long owner, Status bookingStatus, Pageable pageRequest);

    List<Booking> findByItemOwnerIdAndStartBeforeAndEndAfterOrderByStartDesc(
            Long owner, LocalDateTime start, LocalDateTime end, Pageable pageRequest);

    List<Booking> findByItemOwnerIdAndEndBeforeOrderByStartDesc(Long owner, LocalDateTime end, Pageable pageRequest);

    List<Booking> findByItem(Item item);

    List<Booking> findByItemOwnerId(Long ownerId);

    List<Booking> findByBookerIdAndItemIdAndEndBefore(Long bookerId, Long itemId, LocalDateTime time);
}
