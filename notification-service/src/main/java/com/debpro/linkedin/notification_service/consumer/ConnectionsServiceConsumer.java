package com.debpro.linkedin.notification_service.consumer;

import com.debpro.linkedin.connections_service.event.AcceptConnectionRequestEvent;
import com.debpro.linkedin.connections_service.event.SendConnectionRequestEvent;
import com.debpro.linkedin.notification_service.service.SendNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ConnectionsServiceConsumer {

    private final SendNotificationService sendNotificationService;

    @KafkaListener(topics = "send-connection-request-topic")
    public void handleSendConnectionRequest(SendConnectionRequestEvent sendConnectionRequestEvent){
        String message = "You got a connection request from user with id: %d"+ sendConnectionRequestEvent.getSenderId();
        sendNotificationService.sendNotification(sendConnectionRequestEvent.getReceiverId(), message);
    }

    @KafkaListener(topics = "accept-connection-request-topic")
    public void handleAcceptConnectionRequest(AcceptConnectionRequestEvent acceptConnectionRequestEvent){
        String message = "Your connection request has been accepted by user with id: %d"+ acceptConnectionRequestEvent.getReceiverId();
        sendNotificationService.sendNotification(acceptConnectionRequestEvent.getSenderId(), message);
    }
}
