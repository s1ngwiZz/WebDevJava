package com.example.blog.dto;

import jakarta.validation.constraints.NotBlank;

public class CommentCreateDto {
    @NotBlank(message = "Автор должен быть указан")
    private String author;

    @NotBlank(message = "Текст комментария не может быть пустым")
    private String text;

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }
    public String getText() { return text; }
    public void setText(String text) { this.text = text; }
}