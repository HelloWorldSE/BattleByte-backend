package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.RoomDao;
import com.battlebyte.battlebyte.entity.Room;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import jakarta.transaction.Transactional;
import org.apache.ibatis.annotations.Insert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    @Autowired
    private RoomDao roomDao;

    @Modifying
    @Transactional
    public void addRoom(Room room) {
        room.setId(null);
        room.setStatus(0);
        room.setUid(JwtUtil.getUserId());
        roomDao.save(room);
    }

    @Modifying
    @Transactional
    public void updateRoom(Room room) {
        if (findHolder(room.getId()) != JwtUtil.getUserId()) {
            throw new ServiceException("对该房间没有更改权限");
        }
        roomDao.save(room);
    }

    public Page<Room> findRoomByStatus(Integer status, Pageable pageable) {
        if (status < 0) {
            return roomDao.findAll(pageable);
        }
        return roomDao.findAllByStatus(status, pageable);
    }

    /* ------------------     以下为私有方法   ------------------------ */

    private Integer findHolder(Integer integer) {
        Room room = roomDao.findById(integer).orElse(null);
        if (room == null) {
            throw new ServiceException("未找到此房间");
        }
        return room.getUid();
    }
}
