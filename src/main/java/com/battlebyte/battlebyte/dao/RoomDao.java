package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomDao extends JpaRepository<Room, Integer> {
    public Page<Room> findAllByStatus(Integer status, Pageable pageable);

    @Query(value = "select * from room where status = ?2 and exists(select * from user_game_record where user_game_record.user_id = ?1 and room.game_id = user_game_record.game_id)", nativeQuery = true)
    public List<Room> findAllByUserAndStatus(Integer userid, Integer status);

    // TODO: 只有一个？
    public Room findByGameId(Integer gid);
}
