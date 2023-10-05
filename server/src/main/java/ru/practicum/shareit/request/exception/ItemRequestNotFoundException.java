package ru.practicum.shareit.request.exception;

import ru.practicum.shareit.common.exception.NotFoundException;

public class ItemRequestNotFoundException extends NotFoundException {

    public ItemRequestNotFoundException(Long requestId) {
        super("Запрос с идентификатором " + requestId +  " не найден.");
    }

}
