package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UploadService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/update")
public class UploadController {
    @Autowired
    public UploadService uploadService;

    @PostMapping("/avatar")
    public void updateAvatar(@RequestParam MultipartFile file) {
        uploadService.updateAvatar(file);
    }


}
