package ru.practicum.shareit.request.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * TODO Sprint add-item-requests.
 */
@Data
@Entity
@Table(name = "requests")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ItemRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; //уникальный идентификатор запроса
    @Column(name = "description")
    private String description; //текст запроса, содержащий описание требуемой вещи
    @Column(name = "requestor_id")
    private Long requestorId; //пользователь, создавший запрос
    @Column(name = "created")
    private LocalDateTime created; //дата и время создания запроса
}
