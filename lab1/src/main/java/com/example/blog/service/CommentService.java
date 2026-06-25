package com.example.blog.service;

import com.example.blog.dto.CommentCreateDto;
import com.example.blog.dto.CommentResponseDto;
import com.example.blog.entity.Comment;
import com.example.blog.entity.Post;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.repository.CommentRepository;
import com.example.blog.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class CommentService {

    private static final Logger log = LoggerFactory.getLogger(CommentService.class);
    private final CommentRepository commentRepository;
    private final PostRepository postRepository;

    public CommentService(CommentRepository commentRepository, PostRepository postRepository) {
        this.commentRepository = commentRepository;
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<CommentResponseDto> getCommentsByPostId(Long postId) {
        log.debug("Извлечение комментариев для поста с ID: {}", postId);
        return commentRepository.findByPostId(postId).stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    public void addComment(Long postId, CommentCreateDto dto) {
        log.debug("Добавление комментария от автора {} к посту {}", dto.getAuthor(), postId);
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> {
                    log.error("Сбой создания комментария: Пост с ID {} не найден", postId);
                    return new ResourceNotFoundException("Пост не найден с ID: " + postId);
                });

        Comment comment = new Comment();
        comment.setAuthor(dto.getAuthor());
        comment.setText(dto.getText());
        comment.setPost(post);

        commentRepository.save(comment);
        log.info("Комментарий успешно привязан к посту с ID: {}", postId);
    }

    public void deleteComment(Long commentId) {
        log.debug("Запрос на удаление комментария с ID: {}", commentId);
        if (!commentRepository.existsById(commentId)) {
            log.warn("Отказ в удалении: Комментарий с ID {} не найден", commentId);
            throw new ResourceNotFoundException("Комментарий не найден с ID: " + commentId);
        }
        commentRepository.deleteById(commentId);
        log.info("Комментарий с ID: {} удален", commentId);
    }

    private CommentResponseDto mapToResponse(Comment comment) {
        CommentResponseDto dto = new CommentResponseDto();
        dto.setId(comment.getId());
        dto.setAuthor(comment.getAuthor());
        dto.setText(comment.getText());
        return dto;
    }
}