package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

@Data
public class ItemRequestDto {

    private Long id;

    @NotNull(message = "У запроса вещи должно быть описание.")
    private String description;

    private Instant created = Instant.now();

    private Set<ItemDto> items = new HashSet<>();

}
