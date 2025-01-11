package com.ll.webchattingserver.entity.chat.repository;

import com.ll.webchattingserver.entity.chat.Chat;
import org.springframework.data.repository.query.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.List;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query("SELECT c FROM Chat c WHERE c.receiveRoom = :roomId AND DATE(c.createdAt) = DATE(:date)")
    List<Chat> findByIdAndDate(
            @Param("roomId") String roomId,
            @Param("date") LocalDateTime date
    );
}
