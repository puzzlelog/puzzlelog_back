package com.puzzlelog.api.repository.mongo;

import java.util.List;
import java.util.Optional;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.puzzlelog.api.dao.document.Invitation;

@Repository
public interface InvitationRepository extends MongoRepository<Invitation, String> {
	Optional<Invitation> findById(String invitationId);
	
	List<Invitation> findAllByReceiverIdsContaining(String receiverId);
	List<Invitation> findAllBySenderId(String senderId);
}