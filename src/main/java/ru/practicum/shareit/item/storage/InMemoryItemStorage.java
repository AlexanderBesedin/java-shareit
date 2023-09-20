package ru.practicum.shareit.item.storage;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.model.Item;

import java.util.*;
import java.util.stream.Collectors;

@Component
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Set<Item>> userItems = new HashMap<>(); //Таблица списков вещей по пользователям
    private Long id = 1L;//id вещи

    @Override
    public Item add(Long owner, Item item) {
        item.setId(id);
        if (!isOwnerAdded(owner)) {
            Set<Item> items = new HashSet<>();
            items.add(item);
            userItems.put(owner, items);
        } else {
            userItems.get(owner).add(item);
        }
        id++;
        return item;
    }

    @Override
    public Item update(Long owner, Item item) {
        userItems.get(owner).add(item);
        return item;
    }

    @Override
    public Item get(Long id) throws NotFoundException {
        return userItems.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException("Cannot find item with id = " + id));
    }

    @Override
    public Item getItemByUser(Long id, Long owner) throws NotFoundException {
        return userItems.get(owner)
                .stream()
                .filter(item -> id.equals(item.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Item id = %d does not belong to the owner id = %d", id, owner)
                        )
                );
    }

    @Override
    public Set<Item> getAllByUser(Long owner) {
        return userItems.get(owner);
    }

    @Override
    public List<Item> getAllByText(String text) {
        return userItems.values()
                .stream()
                .flatMap(Collection::stream)
                .filter(item -> item.getAvailable()
                                && (item.getName().toLowerCase().contains(text.toLowerCase())
                                || item.getDescription().toLowerCase().contains(text.toLowerCase())
                        )
                )
                .collect(Collectors.toList());
    }

    @Override
    public boolean isOwnerAdded(Long owner) {
        return userItems.containsKey(owner) || userItems.get(owner) != null;
    }

    @Override
    public void deleteUserWithItems(Long id) {
        userItems.remove(id);
    }
}
