package com.debpro.linkedin.posts_service.events;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PostLikedEvent {
    Long creatorId;
    Long postId;
    Long likedByUserId;
}
