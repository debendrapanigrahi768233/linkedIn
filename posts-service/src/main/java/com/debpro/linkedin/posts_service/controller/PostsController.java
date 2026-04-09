package com.debpro.linkedin.posts_service.controller;

import com.debpro.linkedin.posts_service.auth.UserContextHolder;
import com.debpro.linkedin.posts_service.dto.PostDto;
import com.debpro.linkedin.posts_service.dto.PostRequestCreateDto;
import com.debpro.linkedin.posts_service.entity.Post;
import com.debpro.linkedin.posts_service.service.PostsService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/core")
@RequiredArgsConstructor
public class PostsController {

    private final PostsService postsService;
    private final ModelMapper modelMapper;

    @PostMapping
    public ResponseEntity<PostDto> createPost(@RequestBody PostRequestCreateDto postRequestCreateDto, HttpServletRequest httpServletRequest){

        PostDto createdPost = postsService.createPost(postRequestCreateDto);
        return new ResponseEntity<>(createdPost, HttpStatus.CREATED);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDto> getPost(@PathVariable Long postId){

        Long userId = UserContextHolder.getCurrentUserId();

        PostDto post = postsService.getPostById(postId);
        return ResponseEntity.ok(post);
    }

    @GetMapping("/users/{userId}/allPosts")
    public ResponseEntity<List<PostDto>> getAllPostsOfUser(@PathVariable Long userId){
        List<PostDto> posts = postsService.getAllPostsOfUser(userId);
        return ResponseEntity.ok(posts);
    }
}
