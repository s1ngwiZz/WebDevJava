package com.example.blog.controller;

import com.example.blog.dto.PostCreateDto;
import com.example.blog.dto.CommentCreateDto;
import com.example.blog.service.CommentService;
import com.example.blog.service.PostService;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/posts")
public class PostController {

    private final PostService postService;
    private final CommentService commentService;

    public PostController(PostService postService, CommentService commentService) {
        this.postService = postService;
        this.commentService = commentService;
    }

    @GetMapping
    public String listPosts(Model model) {
        model.addAttribute("posts", postService.getAllPosts());
        return "posts/list";
    }

    @GetMapping("/{id}")
    public String viewPost(@PathVariable Long id, Model model) {
        model.addAttribute("post", postService.getPostById(id));
        model.addAttribute("comments", commentService.getCommentsByPostId(id));
        if (!model.containsAttribute("commentCreateDto")) {
            model.addAttribute("commentCreateDto", new CommentCreateDto());
        }
        return "posts/view";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        if (!model.containsAttribute("postCreateDto")) {
            model.addAttribute("postCreateDto", new PostCreateDto());
        }
        return "posts/form";
    }

    @PostMapping
    public String createPost(@Valid @ModelAttribute("postCreateDto") PostCreateDto dto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "posts/form";
        }
        postService.createPost(dto);
        return "redirect:/posts";
    }

    @GetMapping("/{id}/edit")
    public String showEditForm(@PathVariable Long id, Model model) {
        if (!model.containsAttribute("postCreateDto")) {
            var post = postService.getPostById(id);
            PostCreateDto dto = new PostCreateDto();
            dto.setTitle(post.getTitle());
            dto.setContent(post.getContent());
            model.addAttribute("postCreateDto", dto);
        }
        model.addAttribute("postId", id);
        return "posts/edit";
    }

    @PostMapping("/{id}/edit")
    public String updatePost(@PathVariable Long id,
                             @Valid @ModelAttribute("postCreateDto") PostCreateDto dto,
                             BindingResult result, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("postId", id);
            return "posts/edit";
        }
        postService.updatePost(id, dto);
        return "redirect:/posts/" + id;
    }

    @PostMapping("/{id}/delete")
    public String deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return "redirect:/posts";
    }
}