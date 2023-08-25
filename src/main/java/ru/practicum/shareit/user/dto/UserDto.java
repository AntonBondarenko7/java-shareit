package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
public class UserDto {

    private Long id;
    private String name;
    @NotBlank(message = "email не может быть пустым")
    @Email(message = "Введен некорректный email")
    private String email;

}
