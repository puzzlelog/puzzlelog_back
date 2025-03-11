package com.puzzlelog.puzzlelog.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.List;
import com.puzzlelog.puzzlelog.entity.FriendEntity;
import com.puzzlelog.puzzlelog.entity.FriendStatus;

public interface FriendRepository extends JpaRepository<FriendEntity, Integer> {
    Optional<FriendEntity> findByUserIdAndFriendId(String userId, String friendId);
    List<FriendEntity> findByUserIdAndStatus(String userId, FriendStatus status);
}
