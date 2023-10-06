package ru.practicum.shareit.item.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    Optional<Item> findByOwnerIdAndId(Long ownerId, Long id);

    List<Item> findAllByOwnerIdOrderByIdAsc(Long owner);

    List<Item> findByRequestIdOrderByIdDesc(Long requestId);

    @Query("select i from Item i " +
            "where (lower(i.name) like lower(concat('%', ?1, '%')) " +
            " or lower(i.description) like lower(concat('%', ?1, '%'))) and available = true")
    List<Item> searchByText(String text);
}
