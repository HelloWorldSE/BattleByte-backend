package com.battlebyte.battlebyte.service;

import com.battlebyte.battlebyte.entity.User;
import com.battlebyte.battlebyte.exception.ServiceException;
import com.battlebyte.battlebyte.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;

@Service
public class UploadService {

    @Autowired
    public UserService userService;
    public void updateAvatar(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException("空文件");
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!getFileType(suffix).equals("image")) {
            throw new ServiceException("文件类型错误");
        }
        fileName = JwtUtil.getUserId() + "-" + fileName + "." + suffix;
        String filePath = "avatar/";
        try {
            file.transferTo(new File(filePath + fileName));
        } catch (IOException e) {
            throw new ServiceException("上传文件失败" + e.getMessage());
        }
        User user = new User();
        user.setId(JwtUtil.getUserId());
        user.setAvatar(filePath + fileName);
        userService.update(user);
    }

    private static String getFileType(String extension) {
        String document = "txt doc pdf ppt pps xlsx xls docx csv";
        String music = "mp3 wav wma mpa ram ra aac aif m4a";
        String video = "avi mpg mpe mpeg asf wmv mov qt rm mp4 flv m4v webm ogv ogg";
        String image = "bmp dib pcp dif wmf gif jpg tif eps psd cdr iff tga pcd mpt png jpeg";
        if (image.contains(extension)) {
            return "image";
        } else if (document.contains(extension)) {
            return "document";
        } else if (music.contains(extension)) {
            return "music";
        } else if (video.contains(extension)) {
            return "video";
        } else {
            return "other";
        }
    }
}
