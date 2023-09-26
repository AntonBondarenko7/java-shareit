package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotNull;
import lombok.Data;
import ru.practicum.shareit.user.dto.UserDto;

@Data
public class ItemRequestDto {

    private Long id;
    private String description;
    @NotNull(message = "Пользователь не может быть null.")
    private UserDto requester;

}
