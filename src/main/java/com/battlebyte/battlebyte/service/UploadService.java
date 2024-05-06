package com.battlebyte.battlebyte.service;

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

    public String updateAvatar(@RequestParam MultipartFile file) {
        if (file.isEmpty()) {
            throw new ServiceException("空文件");
        }
        String fileName = file.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf(".") + 1);
        if (!getFileType(suffix).equals("image")) {
            throw new ServiceException("文件类型错误");
        }
        fileName = JwtUtil.getUserId() + "-" + fileName;
        String filePath = "/home/ubuntu/BattleByte-backend/avatar/";
        try {
            file.transferTo(new File(filePath + fileName));
        } catch (IOException e) {
            throw new ServiceException("上传文件失败" + e.getMessage());
        }
        return fileName;
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
//
//    public byte[] getAvatar(Integer uid) {
//        if (uid <= 0) {
//            uid = JwtUtil.getUserId();
//        }
//        User user = userService.findById(uid);
//        String path = user.getAvatar();
//        if (path == null) {
//            path = "/home/ubuntu/BattleByte-backend/avatar/default.jpg";
//        }
//        try {
//            // 读取头像文件
//            File file = new File(path);
//            FileInputStream inputStream = new FileInputStream(file);
//            byte[] avatarBytes = IOUtils.toByteArray(inputStream);
//            inputStream.close();
//
//            // 返回头像的字节数组和相关的HTTP头信息
//            // 例如：Content-Type等
//            return avatarBytes;
//        } catch (IOException e) {
//            throw new ServiceException("获取头像信息失败");
//        }
//    }
}
