package com.debpro.linkedin.posts_service.service;

import com.debpro.linkedin.posts_service.auth.UserContextHolder;
import com.debpro.linkedin.posts_service.entity.Post;
import com.debpro.linkedin.posts_service.entity.PostLike;
import com.debpro.linkedin.posts_service.events.PostLikedEvent;
import com.debpro.linkedin.posts_service.exception.BadRequestException;
import com.debpro.linkedin.posts_service.exception.ResourceNotFoundException;
import com.debpro.linkedin.posts_service.repository.PostLikeRepository;
import com.debpro.linkedin.posts_service.repository.PostsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class PostLikeService {
    private final PostLikeRepository postLikeRepository;
    private final PostsRepository postsRepository;
    private final KafkaTemplate<Long, PostLikedEvent> kafkaTemplate;

    public void likePost(Long postId){
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Liking a post with id:"+postId);

        Post post = postsRepository.findById(postId).orElseThrow(()->new ResourceNotFoundException("Not found"));

        boolean isAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId,postId);
        if(isAlreadyLiked) throw new BadRequestException("You cannot like the same post again");

        PostLike postLike = new PostLike();
        postLike.setPostId(postId);
        postLike.setUserId(userId);
        postLikeRepository.save(postLike);

        PostLikedEvent  postLikedEvent = PostLikedEvent.builder()
                .creatorId(post.getUserId())
                .postId(postId)
                .likedByUserId(userId).build();
        kafkaTemplate.send("post-liked-topic", postId,  postLikedEvent);

    }

    public void unlikePost(Long postId) {
        log.info("UnLiking a post with id:"+postId);
        Long userId = UserContextHolder.getCurrentUserId();
        boolean isPostExists = postsRepository.existsById(postId);
        if(!isPostExists) throw new IllegalStateException("Post not found with id "+ postId);

        boolean isAlreadyLiked = postLikeRepository.existsByUserIdAndPostId(userId,postId);
        if(!isAlreadyLiked) throw new BadRequestException("You cannot unlike the post you have not liked");
        postLikeRepository.deleteByUserIdAndPostId(userId, postId);


    }
}
