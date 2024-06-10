package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.dao.RoomDao;
import com.battlebyte.battlebyte.entity.Game;
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

import java.util.List;

@Service
public class RoomService {
    @Autowired
    private RoomDao roomDao;
    @Autowired
    private GameService gameService;

    @Modifying
    @Transactional
    public Room addRoom(Room room) {
        room.setId(null);
        room.setStatus(0);
        room.setUid(JwtUtil.getUserId());
        Game game = gameService.addBlankGame();
        room.setGameId(game.getId());
        return roomDao.save(room);
    }

    @Modifying
    @Transactional
    public Room updateRoom(Room room) {
        if (findHolder(room.getId()) != JwtUtil.getUserId()) {
            throw new ServiceException("对该房间没有更改权限");
        }
        room.setUid(null);
        room.setStatus(null);
        return roomDao.save(room);
    }

    // 注意：这个方法不开放给controller！如果想要更新房间的所有信息，使用这个update！
    @Modifying
    @Transactional
    public void update(Room room) {
        roomDao.save(room);
    }

    @Modifying
    @Transactional
    public void deleteById(Integer id) {
        roomDao.deleteById(id);
    }

    public Page<Room> findRoomByStatus(Integer status, Pageable pageable) {
        if (status < 0) {
            return roomDao.findAll(pageable);
        }
        return roomDao.findAllByStatus(status, pageable);
    }

    public List<Room> findRoomByUserAndStatus(Integer userid, Integer status) {
        return roomDao.findAllByUserAndStatus(userid, status);
    }

    public Room findRoomById(Integer id) {
        return roomDao.findById(id).orElse(null);
    }

    public void setStatus(Integer id, Integer status) {
        Room room = roomDao.findById(id).orElse(null);
        if (room == null) {
            throw new ServiceException("未找到此房间");
        }
        room.setStatus(status);
    }

    /* ------------------     以下为辅助方法   ------------------------ */

    public Integer findHolder(Integer integer) {
        Room room = roomDao.findById(integer).orElse(null);
        if (room == null) {
            throw new ServiceException("未找到此房间");
        }
        return room.getUid();
    }
}
