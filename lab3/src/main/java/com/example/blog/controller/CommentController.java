package com.example.blog.controller;

import com.example.blog.dto.CommentCreateDto;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/comments")
public class CommentController {

    private final CommentService commentService;
    private final PostService postService;

    public CommentController(CommentService commentService, PostService postService) {
        this.commentService = commentService;
        this.postService = postService;
    }

    @PostMapping("/post/{postId}")
    public String addComment(@PathVariable Long postId,
                             @Valid @ModelAttribute("commentCreateDto") CommentCreateDto dto,
                             BindingResult result,
                             RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.commentCreateDto", result);
            redirectAttributes.addFlashAttribute("commentCreateDto", dto);
            return "redirect:/posts/" + postId;
        }
        commentService.addComment(postId, dto);
        return "redirect:/posts/" + postId;
    }

    @PostMapping("/{id}/delete")
    public String deleteComment(@PathVariable Long id, @RequestParam Long postId) {
        commentService.deleteComment(id);
        return "redirect:/posts/" + postId;
    }
}