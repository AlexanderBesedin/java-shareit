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
    public Item find(Long id) throws NotFoundException {
        Item findItem = null;
        for (Set<Item> items : userItems.values()) {
            findItem = items
                    .stream()
                    .filter(item -> id.equals(item.getId()))
                    .findFirst()
                    .orElseThrow(
                            () -> new NotFoundException("Cannot find item with id = " + id));
        }
        return findItem;
    }

    @Override
    public Set<Item> findAllByUser(Long owner) {
        return userItems.get(owner);
    }

    @Override
    public List<Item> findAllByText(String text) {
        String lowerCaseText = text.toLowerCase();
        List<Item> matchItems = new ArrayList<>();

        for (Set<Item> items : userItems.values()) {
            matchItems.addAll(items);
        }

        matchItems = matchItems.stream()
                .filter(item -> item.getAvailable()
                                && (item.getName().toLowerCase().contains(lowerCaseText)
                                || item.getDescription().toLowerCase().contains(lowerCaseText)
                        )
                )
                .collect(Collectors.toList());

        return matchItems;
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
