package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.service.UploadService;
import com.battlebyte.battlebyte.service.UserService;
import com.battlebyte.battlebyte.util.JwtUtil;
import jakarta.persistence.criteria.CriteriaBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    public UploadService uploadService;

    @PostMapping("/avatar")
    public void updateAvatar(@RequestParam MultipartFile file) {
        uploadService.updateAvatar(file);
    }

    @GetMapping("/getAvatar")
    public byte[] getAvatar(@RequestParam(defaultValue = "0") Integer id) {
        return uploadService.getAvatar(id);
    }

}
