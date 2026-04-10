package com.debpro.linkedin.connections_service.service;

import com.debpro.linkedin.connections_service.auth.UserContextHolder;
import com.debpro.linkedin.connections_service.entity.Person;
import com.debpro.linkedin.connections_service.event.AcceptConnectionRequestEvent;
import com.debpro.linkedin.connections_service.event.SendConnectionRequestEvent;
import com.debpro.linkedin.connections_service.repository.PersonsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    private final PersonsRepository personsRepository;
    private final KafkaTemplate<Long, SendConnectionRequestEvent> sendConnectionRequestEventKafkaTemplate;
    private final KafkaTemplate<Long, AcceptConnectionRequestEvent> acceptConnectionRequestEventKafkaTemplate;


    public List<Person> getFirstDegreeConnections(){
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Getting all first degree connections of user with id: "+userId);
        return personsRepository.getFirstDegreeConnections(userId);
    }

    public Boolean sendConnectionRequest(Long receiverId) {
        Long senderId = UserContextHolder.getCurrentUserId();
        log.info("Send connection request from "+senderId+ "to " + receiverId);
        if( senderId == receiverId){
            throw new RuntimeException("Both are same person, not allowed");
        }
        boolean alreadySentRequest  = personsRepository.connectionRequestExists(senderId,receiverId);
        if(alreadySentRequest){
            throw new RuntimeException("Connection request already exists");
        }
        boolean alreadyConnected = personsRepository.alreadyConnected(senderId,receiverId);
        if(alreadyConnected){
            throw new RuntimeException("Already connected");
        }

        personsRepository.addConnectionRequest(senderId,receiverId);

        SendConnectionRequestEvent sendConnectionRequestEvent = SendConnectionRequestEvent.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .build();
        sendConnectionRequestEventKafkaTemplate.send("send-connection-request-topic", sendConnectionRequestEvent);
        log.info("Request sent successfully");
        return true;
    }

    public Boolean acceptConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();
        log.info("Accept connection request from "+senderId+ "to " + receiverId);
        boolean connectionRequested  = personsRepository.connectionRequestExists(senderId,receiverId);
        if(!connectionRequested){
            throw new RuntimeException("Connection request not exists");
        }

        personsRepository.addConnection(senderId,receiverId);
        AcceptConnectionRequestEvent acceptConnectionRequestEvent = AcceptConnectionRequestEvent.builder()
                .receiverId(receiverId)
                .senderId(senderId)
                .build();
        acceptConnectionRequestEventKafkaTemplate.send("accept-connection-request-topic", acceptConnectionRequestEvent);
        log.info("Request accepted successfully");
        return true;
    }

    public Boolean rejectConnectionRequest(Long senderId) {
        Long receiverId = UserContextHolder.getCurrentUserId();
        log.info("Accept connection request from "+senderId+ "to " + receiverId);
        boolean connectionRequested  = personsRepository.connectionRequestExists(senderId,receiverId);
        if(!connectionRequested){
            throw new RuntimeException("Connection request not exists");
        }
        personsRepository.rejectConnectionRequest(senderId,receiverId);
        log.info("Request rejected successfully");
        return true;
    }
}
