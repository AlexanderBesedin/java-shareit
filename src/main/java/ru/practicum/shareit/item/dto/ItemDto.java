package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * TODO Sprint add-controllers.
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ItemDto {
    private Long id;
    @NotBlank(message = "Name must be completed")
    private String name;
    @NotBlank(message = "Description must be completed")
    private String description;
    @NotNull(message = "Available must be completed")
    private Boolean available;
    private Long requestId;
}
