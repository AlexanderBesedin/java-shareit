package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Set;

public interface ItemStorage {
    Item add(Long owner, Item item);

    Item update(Long owner, Item item);

    Item find(Long id);

    Set<Item> findAllByUser(Long owner);

    List<Item> findAllByText(String text);

    public boolean isOwnerAdded(Long owner);

    void deleteUserWithItems(Long id);
}
