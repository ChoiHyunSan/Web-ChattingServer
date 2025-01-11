package com.ll.webchattingserver.entity.userroom.repository;

import com.ll.webchattingserver.entity.userroom.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    List<UserRoom> findByRoomId(UUID room);
}
