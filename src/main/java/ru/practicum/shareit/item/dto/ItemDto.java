package ru.practicum.shareit.item.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.request.model.ItemRequest;


@Data
@Builder
public class ItemDto {

    private Long id;
    @NotNull(message = "Название не может быть пустым")
    private String name;
    @NotNull(message = "Описание не может быть пустым")
    private String description;
    @NotNull(message = "Доступность не может быть пустым")
    private Boolean available;
    private ItemRequest request;
}
