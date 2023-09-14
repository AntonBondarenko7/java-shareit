package ru.practicum.shareit.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;


@Data
public class UserDto {

    private Long id;

    private String name;

    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Введен некорректный email")
    private String email;
}
