package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.Room;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.RoomService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/api/room")
@RestController
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/add")
    public Room addRoom(@RequestBody Room room) {
        if (room.getName().length() >= 20 || room.getName().length() <= 2) {
            throw new ServiceException("房间名过长或过短");
        }
        Room room1 = null;
        try {
            room1 = roomService.addRoom(room);
        } catch (Exception e) {
            throw new ServiceException("添加失败");
        }
        return room1;
    }

    @PostMapping("update")
    public Room updateRoom(@RequestBody Room room) {
        if (room.getName().length() >= 20 || room.getName().length() <= 2) {
            throw new ServiceException("房间名过长或过短");
        }
        return roomService.updateRoom(room);
    }

    /**
     * 传status，就是查询对应status的房间 <p>
     * 不传，就是查询所有房间
     */
    @GetMapping("")
    public Page<Room> findRoom(@RequestParam(defaultValue = "-1") Integer status,
                                       @RequestParam(defaultValue = "1") Integer page, @RequestParam(defaultValue = "10") Integer pageSize) {
        Pageable pageable = PageRequest.of(page - 1, pageSize);
        return roomService.findRoomByStatus(status, pageable);
    }

    @GetMapping("/myroom")
    public List<Room> findRoomByUserAndStatus(@RequestParam(defaultValue = "0") Integer status) {
        return roomService.findRoomByUserAndStatus(JwtUtil.getUserId(), status);
    }


    @GetMapping("/id")
    public Room findRoomById(@RequestParam(defaultValue = "-1") Integer id) {
        return roomService.findRoomById(id);
    }

    @DeleteMapping("/{id}")
    public void deleteRoom(@PathVariable("id") Integer id) {
        if (roomService.findHolder(id) == JwtUtil.getUserId()) {
            roomService.deleteById(id);
        } else {
            throw new ServiceException("无权限！");
        }
    }

}
