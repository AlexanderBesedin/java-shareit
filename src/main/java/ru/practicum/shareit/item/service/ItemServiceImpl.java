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

        Item findItem = itemStorage.getItemByUser(id, owner);
        itemStorage.getAllByUser(owner).remove(findItem);
        //Проверяем поля полученного itemDto на null
        if (itemDto.getId() != null) findItem.setId(itemDto.getId());
        if (itemDto.getName() != null) findItem.setName(itemDto.getName());
        if (itemDto.getDescription() != null) findItem.setDescription(itemDto.getDescription());
        if (itemDto.getAvailable() != null) findItem.setAvailable(itemDto.getAvailable());
        if (itemDto.getRequest() != null) findItem.setRequest(itemDto.getRequest());

        log.info("Item {} has been UPDATED", findItem);
        return ItemMapper.toItemDto(itemStorage.update(owner, findItem));
    }

    @Override
    public ItemDto get(Long id, Long owner) {
        if (owner == null || !userStorage.checkExist(owner)) {
            throw new NotFoundException("Cannot get items with non-existent user");
        }
        log.info("Get item id = {}", id);
        return ItemMapper.toItemDto(itemStorage.get(id));
    }

    @Override
    public Set<ItemDto> getAllByUser(Long owner) {
        if (owner == null || !itemStorage.isOwnerAdded(owner)) {
            throw new NotFoundException("Cannot get items with non-added user");
        }
        log.info("Get all items by owner id = {}", owner);
        return itemStorage.getAllByUser(owner)
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

        return itemStorage.getAllByText(text)
                .stream()
                .map(ItemMapper::toItemDto)
                .collect(Collectors.toList());
    }
}
