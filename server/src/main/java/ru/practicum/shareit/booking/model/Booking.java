package ru.practicum.shareit.booking.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-bookings.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bookings")
@Builder
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //уникальный идентификатор бронирования
    @Column(name = "start_date", nullable = false)
    private LocalDateTime start; //дата и время начала бронирования
    @Column(name = "end_date", nullable = false)
    private LocalDateTime end; //дата и время конца бронирования
    @ManyToOne
    @JoinColumn(name = "item_id", referencedColumnName = "id")
    private Item item; //вещь, которую пользователь бронирует
    @ManyToOne
    @JoinColumn(name = "booker_id", referencedColumnName = "id")
    private User booker; //пользователь, который осуществляет бронирование
    @Enumerated(EnumType.STRING)
    private Status status; //Статус бронирования. Может принимать одно из следующих значений:
    // WAITING — новое бронирование, ожидает одобрения
    // APPROVED — бронирование подтверждено владельцем
    // REJECTED — бронирование отклонено владельцем
    // CANCELED — бронирование отменено создателем
}
