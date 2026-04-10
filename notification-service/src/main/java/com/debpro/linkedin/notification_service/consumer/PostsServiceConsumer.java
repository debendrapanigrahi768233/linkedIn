package com.debpro.linkedin.notification_service.consumer;

import com.debpro.linkedin.notification_service.client.ConnectionsClient;
import com.debpro.linkedin.notification_service.dto.PersonDto;
import com.debpro.linkedin.notification_service.entity.Notification;
import com.debpro.linkedin.notification_service.repository.NotificationRepository;
import com.debpro.linkedin.notification_service.service.SendNotificationService;
import com.debpro.linkedin.posts_service.events.PostCreatedEvent;
import com.debpro.linkedin.posts_service.events.PostLikedEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PostsServiceConsumer {

    private final ConnectionsClient connectionsClient;
    private final NotificationRepository notificationRepository;
    private final SendNotificationService sendNotificationService;

    @KafkaListener(topics = "post-created-topic")
    public void handlePostCreated(PostCreatedEvent postCreatedEvent){
        log.info("Sending notifications: handlePostCreated");
        List<PersonDto> connectedPersons = connectionsClient.getFirstConnections(postCreatedEvent.getCreatorId());
        for(PersonDto personDto: connectedPersons){
            sendNotificationService.sendNotification(personDto.getUserId(), "Your connection "+ postCreatedEvent.getCreatorId() + "has created a post, check it out!");
        }
    }

    @KafkaListener(topics = "post-liked-topic")
    public void handlePostLiked(PostLikedEvent postLikedEvent){
        log.info("Sending notifications: handlePostLiked");
        sendNotificationService.sendNotification(postLikedEvent.getCreatorId(), "Your connection "+ postLikedEvent.getLikedByUserId() + "has liked your post "+ postLikedEvent.getPostId());
    }


}
