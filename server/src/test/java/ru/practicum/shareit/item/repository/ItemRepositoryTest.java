package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.RequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RequestRepository requestRepository;

    @Test
    void shouldFindByOwnerIdAndId() {
        User owner = createUser();
        Item item = createItem(owner);
        Assertions.assertEquals(Optional.of(item), itemRepository.findByOwnerIdAndId(owner.getId(), item.getId()));
    }

    @Test
    void shouldFindAllByOwnerId() {
        User owner = createUser();
        Item item1 = createItem(owner);
        Item item2 = createItem(owner);
        Assertions.assertEquals(List.of(item1, item2), itemRepository.findAllByOwnerIdOrderByIdAsc(owner.getId()));
    }

    @Test
    void shouldFindByRequestIdOrderByIdDesc() {
        User owner = createUser();
        User requestor = userRepository.save(new User(null, "newUser", "newUser@user.com"));
        ItemRequest request = createItemRequest(requestor);
        Item item1 = createItem(owner, null, request);
        Item item2 = createItem(owner, null, request);
        Assertions.assertEquals(List.of(item2, item1), itemRepository.findByRequestIdOrderByIdDesc(request.getId()));
    }

    @Test
    void shouldFindItemByText() {
        User owner = createUser();
        createItem(owner);
        Item item2 = createItem(owner, "шуруповерт Bosch", null);
        Item item3 = createItem(owner, "шуруповерт Ryobi", null);
        Assertions.assertEquals(List.of(item2, item3), itemRepository.searchByText("шуРУП"));
    }

    private User createUser() {
        User user = new User(null, "user", "user@user.com");
        return userRepository.save(user);
    }

    private ItemRequest createItemRequest(User requestor) {
        ItemRequest request = new ItemRequest(null, "need item", requestor, LocalDateTime.now());
        return requestRepository.save(request);
    }

    private Item createItem(User owner, String text, ItemRequest request) {
        Item item = new Item(
                null,
                "item",
                text != null ? text : "some item",
                true,
                owner,
                request
        );
        return itemRepository.save(item);
    }

    private Item createItem(User owner) {
        return createItem(owner, null, null);
    }
}
