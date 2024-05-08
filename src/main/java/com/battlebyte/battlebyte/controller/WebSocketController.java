package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.util.JwtUtil;
import com.battlebyte.battlebyte.websocket.WebSocketServer;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class WebSocketController {

    @GetMapping("/page")
    public ModelAndView page() {
        return new ModelAndView("webSocket");
    }

    @PostMapping("/push/{toUID}")
    public ResponseEntity<String> pushToClient(@RequestBody String message, @PathVariable String toUID) throws Exception {
        WebSocketServer.sendInfo(message, toUID);
        return ResponseEntity.ok("Send Success!");
    }

    @GetMapping("/currentMatch")
    public int currentMatch() {
        return WebSocketServer.getCurrentMatch();
    }

    @GetMapping("/onlineCount")
    public int onlineCount() {
        return WebSocketServer.getOnlineCount();
    }

}