package com.ll.webchattingserver.domain.room.repository;

import com.ll.webchattingserver.domain.room.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RoomRepository extends JpaRepository<Room, UUID> {
}
