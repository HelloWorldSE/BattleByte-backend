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

@RequestMapping("/api/room")
@RestController
public class RoomController {
    @Autowired
    private RoomService roomService;

    @PostMapping("/add")
    public void addRoom(@RequestBody Room room) {
        roomService.addRoom(room);
    }

    @PostMapping("update")
    public void updateRoom(@RequestBody Room room) {
        roomService.updateRoom(room);
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
