package ru.practicum.shareit.comment.dto;

import java.time.LocalDateTime;


import jakarta.validation.constraints.NotEmpty;
import lombok.Data;


@Data
public class CommentDto {

    private Long id;

    @NotEmpty(message = "Ошибка! Текст комментария не может быть пустым.")
    private String text;

    private String authorName;

    private LocalDateTime created;
}