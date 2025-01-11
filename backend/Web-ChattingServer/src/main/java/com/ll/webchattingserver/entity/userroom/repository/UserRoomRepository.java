package com.ll.webchattingserver.entity.userroom.repository;

import com.ll.webchattingserver.entity.room.Room;
import com.ll.webchattingserver.entity.user.User;
import com.ll.webchattingserver.entity.userroom.UserRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRoomRepository extends JpaRepository<UserRoom, Long> {
    List<UserRoom> findByRoom(Room room);

    void deleteByUserAndRoom(User user, Room room);
    void deleteByRoom(Room room);
}
