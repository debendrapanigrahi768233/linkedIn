package com.debpro.linkedin.posts_service.service;

import com.debpro.linkedin.posts_service.auth.UserContextHolder;
import com.debpro.linkedin.posts_service.client.ConnectionsClient;
import com.debpro.linkedin.posts_service.dto.PersonDto;
import com.debpro.linkedin.posts_service.dto.PostDto;
import com.debpro.linkedin.posts_service.dto.PostRequestCreateDto;
import com.debpro.linkedin.posts_service.entity.Post;
import com.debpro.linkedin.posts_service.events.PostCreatedEvent;
import com.debpro.linkedin.posts_service.exception.ResourceNotFoundException;
import com.debpro.linkedin.posts_service.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostsService {
    private final PostsRepository postsRepository;
    private final ModelMapper modelMapper;
    private final ConnectionsClient connectionsClient;

    private final KafkaTemplate<Long, PostCreatedEvent> kafkaTemplate;

    public PostDto createPost(PostRequestCreateDto postRequestCreateDto) {
        Long userId = UserContextHolder.getCurrentUserId();
        Post post = modelMapper.map(postRequestCreateDto, Post.class);
        post.setUserId(userId);
        Post savedPost = postsRepository.save(post);

        //send kafka event
        PostCreatedEvent postCreatedEvent = PostCreatedEvent.builder()
                .creatorId(userId)
                .content(savedPost.getContent())
                .postId(savedPost.getId())
                .build();
        kafkaTemplate.send("post-created-topic", postCreatedEvent);

        return modelMapper.map(savedPost, PostDto.class);
    }

    public PostDto getPostById(Long postId) {

//        //Testing openfeign
//        Long userId = UserContextHolder.getCurrentUserId();
//        List<PersonDto> firstDegreeConnections = connectionsClient.getFirstConnections();
//
//        //To Do: Send notifications to all the connections

        Post post = postsRepository.findById(postId).orElseThrow(()-> new ResourceNotFoundException("post not found with id: "+postId));

        return  modelMapper.map(post, PostDto.class);
    }

    public List<PostDto> getAllPostsOfUser(Long userId) {
        List<Post> posts = postsRepository.findByUserId(userId);
        return posts.stream().map((post)-> modelMapper.map(post, PostDto.class)).collect(Collectors.toList());
    }
}
