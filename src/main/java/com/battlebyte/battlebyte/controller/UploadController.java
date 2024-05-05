package com.battlebyte.battlebyte.controller;

import com.battlebyte.battlebyte.common.Result;
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
    public void updateAvatar(@RequestParam MultipartFile file) {
        uploadService.updateAvatar(file);
    }

    @GetMapping("/getAvatar")
    public Result getAvatar(@RequestParam(defaultValue = "0") Integer id) {
        return Result.success(uploadService.getAvatar(id));
    }

}
