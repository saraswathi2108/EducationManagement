package com.project.student.education.controller;

import com.project.student.education.entity.SchoolFeed;
import com.project.student.education.repository.SchoolFeedRepository;
import com.project.student.education.service.FileService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/student/feed")
public class SchoolFeedController {

    @Autowired
    private FileService fileService;
 
    @Autowired
    private SchoolFeedRepository feedRepo; // Create Repository interface also
 
    @GetMapping("/all")
    public ResponseEntity<List<SchoolFeed>> getSchoolFeed() {
        return ResponseEntity.ok(feedRepo.findAll(Sort.by(Sort.Direction.DESC, "postDate")));
    }
    @PostMapping(value = "/create", consumes = "multipart/form-data")
    public ResponseEntity<SchoolFeed> createPost(
            @RequestPart("feed") SchoolFeed feed,
            @RequestPart(value = "file", required = false) MultipartFile file
    ) throws IOException {

        String imageUrl = null;

        if (file != null && !file.isEmpty()) {
            imageUrl = fileService.uploadFile(file);
            feed.setImageUrl(imageUrl);
        }

        return ResponseEntity.ok(feedRepo.save(feed));
    }

}