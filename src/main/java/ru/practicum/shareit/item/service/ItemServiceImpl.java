package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.item.ItemMapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;

    @Override
    public ItemDto add(Long owner, ItemDto itemDto) {
        if (owner == null || !userStorage.checkExist(owner)) {
            throw new NotFoundException("Cannot create item with non-existent user");
        }
        Item item = itemStorage.add(owner, ItemMapper.toItem(owner, itemDto));
        log.info("Item added: id = {}, name = {}, ownerId = {}", item.getId(), item.getName(), owner);
        return ItemMapper.toItemDto(item);
    }

    @Override
    public ItemDto update(Long id, Long owner, ItemDto itemDto) {
        if (owner == null || !userStorage.checkExist(owner)) {
            throw new NotFoundException("Cannot update item with non-existent user");
        }
        if (!itemStorage.isOwnerAdded(owner)) {
            throw new NotFoundException("Cannot get items with non-added user");
        }

        Set<Item> items = itemStorage.findAllByUser(owner);
        Item findItem = items
                .stream()
                .filter(i -> id.equals(i.getId()))
                .findFirst()
                .orElseThrow(
                        () -> new NotFoundException(
                                String.format("Item id = %d does not belong to the owner id = %d", id, owner)
                        )
                );

        items.remove(findItem);
        Item item = ItemMapper.toItem(owner, itemDto);
        //Проверяем поля полученного itemDto на null
        if (item.getId() == null) item.setId(id);
        if (item.getName() == null) item.setName(findItem.getName());
        if (item.getDescription() == null) item.setDescription(findItem.getDescription());
        if (item.getAvailable() == null) item.setAvailable(findItem.getAvailable());
        if (item.getRequest() == null) item.setRequest(findItem.getRequest());

        log.info("Item {} has been UPDATED", item);
        return ItemMapper.toItemDto(itemStorage.update(owner, item));
    }

    @Override
    public ItemDto get(Long id, Long owner) {
        if (owner == null || !userStorage.checkExist(owner)) {
            throw new NotFoundException("Cannot get items with non-existent user");
        }
        log.info("Get item id = {}", id);
        return ItemMapper.toItemDto(itemStorage.find(id));
    }

    @Override
    public Set<ItemDto> getAllByUser(Long owner) {
        if (owner == null || !itemStorage.isOwnerAdded(owner)) {
            throw new NotFoundException("Cannot get items with non-added user");
        }
        log.info("Get all items by owner id = {}", owner);
        return itemStorage.findAllByUser(owner)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toSet());
    }

    @Override
    public List<ItemDto> getAllByText(String text, Long renter) {
        if (renter == null || !itemStorage.isOwnerAdded(renter)) {
            throw new NotFoundException("Cannot get items by non-existent user");
        }
        if (text == null || text.isEmpty()) return List.of();

        return itemStorage.findAllByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
