package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.Getter;

import javax.validation.constraints.NotEmpty;
import java.time.LocalDateTime;

import static ru.practicum.shareit.common.utils.Constants.PATTERN_FOR_DATETIME;


@Getter
public class CommentDto {

    private Long id;

    @NotEmpty(message = "Ошибка! Текст комментария не может быть пустым.")
    private String text;

    private String authorName;

    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = PATTERN_FOR_DATETIME)
    private LocalDateTime created;

}
