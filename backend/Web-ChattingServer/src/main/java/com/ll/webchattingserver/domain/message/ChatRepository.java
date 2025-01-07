package com.ll.webchattingserver.domain.message;

import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.receiveRoom = :roomId AND DATE(c.created_at) = DATE(:date)")
    List<Chat> findByDateBetween(
            @Param("roomId") String roomId,
            @Param("date") LocalDateTime date
    );
}
