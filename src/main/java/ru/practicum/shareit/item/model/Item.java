package ru.practicum.shareit.item.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Item {
    private Long id; //уникальный идентификатор вещи
    private String name; //краткое название
    private String description; //развёрнутое описание
    private Boolean available; //статус о том, доступна или нет вещь для аренды
    private Long owner; //владелец вещи
    private Long request; //если вещь была создана по запросу другого пользователя, то в этом
    // поле будет храниться ссылка на соответствующий запрос.
}
