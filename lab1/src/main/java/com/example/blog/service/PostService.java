package com.example.blog.service;

import com.example.blog.dto.PostCreateDto;
import com.example.blog.dto.PostResponseDto;
import com.example.blog.entity.Post;
import com.example.blog.exception.ResourceNotFoundException;
import com.example.blog.repository.PostRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class PostService {

    private static final Logger log = LoggerFactory.getLogger(PostService.class);
    private final PostRepository postRepository;

    public PostService(PostRepository postRepository) {
        this.postRepository = postRepository;
    }

    @Transactional(readOnly = true)
    public List<PostResponseDto> getAllPosts() {
        log.debug("Извлечение всех постов из базы данных");
        List<PostResponseDto> posts = postRepository.findAll().stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
        log.info("Успешно извлечено {} постов", posts.size());
        return posts;
    }

    @Transactional(readOnly = true)
    public PostResponseDto getPostById(Long id) {
        log.debug("Поиск поста с ID: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Пост не найден по ID: {}", id);
                    return new ResourceNotFoundException("Пост не найден с ID: " + id);
                });
        return mapToResponse(post);
    }

    public void createPost(PostCreateDto dto) {
        log.debug("Создание нового поста с заголовком: {}", dto.getTitle());
        Post post = new Post();
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        postRepository.save(post);
        log.info("Новый пост успешно сохранен в базу данных");
    }

    public void updatePost(Long id, PostCreateDto dto) {
        log.debug("Попытка обновления поста с ID: {}", id);
        Post post = postRepository.findById(id)
                .orElseThrow(() -> {
                    log.error("Сбой обновления: Пост с ID {} не существует", id);
                    return new ResourceNotFoundException("Пост не найден с ID: " + id);
                });
        post.setTitle(dto.getTitle());
        post.setContent(dto.getContent());
        postRepository.save(post);
        log.info("Пост с ID: {} успешно обновлен", id);
    }

    public void deletePost(Long id) {
        log.debug("Запрос на удаление поста с ID: {}", id);
        if (!postRepository.existsById(id)) {
            log.error("Сбой удаления: Пост с ID {} не найден в базе", id);
            throw new ResourceNotFoundException("Пост не найден с ID: " + id);
        }
        postRepository.deleteById(id);
        log.info("Пост с ID: {} полностью удален", id);
    }

    private PostResponseDto mapToResponse(Post post) {
        PostResponseDto dto = new PostResponseDto();
        dto.setId(post.getId());
        dto.setTitle(post.getTitle());
        dto.setContent(post.getContent());
        return dto;
    }
}