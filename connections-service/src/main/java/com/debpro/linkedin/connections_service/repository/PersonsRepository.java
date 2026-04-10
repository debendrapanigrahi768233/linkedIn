package com.debpro.linkedin.connections_service.repository;

import com.debpro.linkedin.connections_service.entity.Person;
import org.springframework.data.neo4j.repository.Neo4jRepository;
import org.springframework.data.neo4j.repository.query.Query;

import java.util.List;
import java.util.Optional;

public interface PersonsRepository extends Neo4jRepository<Person, Long> {
    Optional<Person> getByName(String name);


    @Query("MATCH (personA:Person) -[:CONNECTED_TO]- (personB:Person) " +
            "WHERE personA.userId = $userId " +
            "Return personB")
    List<Person> getFirstDegreeConnections(Long userId);

    // 🔹 Check if connection request already exists
    @Query("""
        MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        RETURN COUNT(r) > 0
    """)
    boolean connectionRequestExists(Long senderId, Long receiverId);

    // 🔹 Check if already connected
    @Query("""
        MATCH (p1:Person)-[r:CONNECTED_TO]-(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        RETURN COUNT(r) > 0
    """)
    boolean alreadyConnected(Long senderId, Long receiverId);

    // 🔹 Send connection request
    @Query("""
        MATCH (p1:Person), (p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        CREATE (p1)-[:REQUESTED_TO]->(p2)
        RETURN true
    """)
    boolean addConnectionRequest(Long senderId, Long receiverId);

    // 🔹 Accept connection request
    @Query("""
        MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        DELETE r
        CREATE (p1)-[:CONNECTED_TO]-(p2)
        RETURN true
    """)
    boolean addConnection(Long senderId, Long receiverId);

    // 🔹 Reject connection request
    @Query("""
        MATCH (p1:Person)-[r:REQUESTED_TO]->(p2:Person)
        WHERE p1.userId = $senderId AND p2.userId = $receiverId
        DELETE r
        RETURN true
    """)
    boolean rejectConnectionRequest(Long senderId, Long receiverId);

}
