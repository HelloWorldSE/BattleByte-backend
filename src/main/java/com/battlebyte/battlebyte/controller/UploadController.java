package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.service.UploadService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/upload")
public class UploadController {
    @Autowired
    public UploadService uploadService;

    @PostMapping("/avatar")
    public String updateAvatar(@RequestParam MultipartFile file) {
        return uploadService.updateAvatar(file);
    }

}
