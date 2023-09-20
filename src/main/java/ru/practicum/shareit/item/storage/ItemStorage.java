package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemStorage {
    Item add(Long owner, Item item);

    Item update(Long owner, Item item);

    Item get(Long id);

    Item getItemByUser(Long id, Long owner);

    Set<Item> getAllByUser(Long owner);

    List<Item> getAllByText(String text);

    boolean isOwnerAdded(Long owner);

    void deleteUserWithItems(Long id);
}
