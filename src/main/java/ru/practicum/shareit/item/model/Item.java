package ru.practicum.shareit.item.model;

import ru.practicum.shareit.request.ItemRequest;
import ru.practicum.shareit.user.model.User;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class Item {

    private Long id;
    @NotBlank
    private String name;
    @NotBlank
    private String description;
    @NotBlank
    private Boolean isAvailable;
    private final User owner;
    private ItemRequest request;
}
