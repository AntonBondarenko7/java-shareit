package ru.practicum.shareit.comment.repository;

import ru.practicum.shareit.comment.model.Comment;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

    List<Comment> findAllByItemId(Long itemId);

}
