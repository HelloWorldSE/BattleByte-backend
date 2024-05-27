package com.battlebyte.battlebyte.dao;

import com.battlebyte.battlebyte.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoomDao extends JpaRepository<Room, Integer> {
    public Page<Room> findAllByStatus(Integer status, Pageable pageable);
}
