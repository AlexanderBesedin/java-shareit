package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Booking {
    private Long id; //уникальный идентификатор бронирования
    private LocalDateTime start; //дата и время начала бронирования
    private LocalDateTime end; //дата и время конца бронирования
    private Long itemId; //вещь, которую пользователь бронирует
    private Long booker; //пользователь, который осуществляет бронирование
    private Status status; //Статус бронирования. Может принимать одно из следующих значений:
    // WAITING — новое бронирование, ожидает одобрения
    // APPROVED — бронирование подтверждено владельцем
    // REJECTED — бронирование отклонено владельцем
    // CANCELED — бронирование отменено создателем
}
