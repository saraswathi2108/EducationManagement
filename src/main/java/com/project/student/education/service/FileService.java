package com.project.student.education.service;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

@Service
public class FileService {

    private final String basePath = "images";
    public String uploadFile(MultipartFile file) throws IOException {


        String original = file.getOriginalFilename();
        String ext = original.substring(original.lastIndexOf("."));

        String uuidName = UUID.randomUUID().toString() + ext;
        File folder = new File(basePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

        String filePath = basePath + File.separator + uuidName;
        Files.copy(file.getInputStream(), Paths.get(filePath));

        return "/images/" + uuidName;
    }


    public String updateFile(MultipartFile newFile, String oldFileUrl) throws IOException {

        if (oldFileUrl != null && oldFileUrl.startsWith("/images/")) {

            String oldFileName = oldFileUrl.replace("/images/", "");

            File oldFile = new File(basePath + File.separator + oldFileName);

            if (oldFile.exists()) {
                oldFile.delete();
            }
        }

        return uploadFile(newFile);
    }
    public void deleteFile(String fileUrl) {

        if (fileUrl == null || !fileUrl.startsWith("/images/")) {
            return;
        }


        String fileName = fileUrl.replace("/images/", "");

        File file = new File("images/" + fileName);

        if (file.exists()) {
            file.delete();
        }
    }

}
