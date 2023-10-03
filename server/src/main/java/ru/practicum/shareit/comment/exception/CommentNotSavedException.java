package ru.practicum.shareit.comment.exception;

import ru.practicum.shareit.exception.NotFoundException;

public class CommentNotSavedException extends NotFoundException {

    public CommentNotSavedException() {
        super("Не удалось сохранить комментарий");
    }

}
