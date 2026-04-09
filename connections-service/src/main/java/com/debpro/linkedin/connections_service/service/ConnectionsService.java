package com.debpro.linkedin.connections_service.service;

import com.debpro.linkedin.connections_service.auth.UserContextHolder;
import com.debpro.linkedin.connections_service.entity.Person;
import com.debpro.linkedin.connections_service.repository.PersonsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConnectionsService {

    private final PersonsRepository personsRepository;

    public List<Person> getFirstDegreeConnections(){
        Long userId = UserContextHolder.getCurrentUserId();
        log.info("Getting all first degree connections of user with id: "+userId);
        return personsRepository.getFirstDegreeConnections(userId);
    }
}
