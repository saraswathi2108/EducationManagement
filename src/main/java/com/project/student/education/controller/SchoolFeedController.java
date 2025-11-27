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

import java.time.LocalDate;

import java.util.List;

@RestController

@RequestMapping("/api/student/feed")

public class SchoolFeedController {

    @Autowired

    private FileService fileService;

    @Autowired

    private SchoolFeedRepository feedRepo;

    @GetMapping("/all")

    public ResponseEntity<List<SchoolFeed>> getSchoolFeed() {

        return ResponseEntity.ok(feedRepo.findAll(Sort.by(Sort.Direction.DESC, "postDate")));

    }

    @PostMapping(value = "/create", consumes = "multipart/form-data")

    public ResponseEntity<SchoolFeed> createPost(

            @RequestPart("data") SchoolFeed feed,

            @RequestPart(value = "file", required = false) MultipartFile file

    ) throws IOException {

        if (file != null && !file.isEmpty()) {

            String imageUrl = fileService.uploadFile(file);

            feed.setImageUrl(imageUrl);

        }

        if (feed.getPostDate() == null) {

            feed.setPostDate(LocalDate.now());

        }

        return ResponseEntity.ok(feedRepo.save(feed));

    }

    @PutMapping(value = "/update/{id}", consumes = "multipart/form-data")

    public ResponseEntity<SchoolFeed> updatePost(

            @PathVariable Integer id,

            @RequestPart("data") SchoolFeed updatedFeed,

            @RequestPart(value = "file", required = false) MultipartFile file

    ) throws IOException {

        SchoolFeed existing = feedRepo.findById(id)

                .orElseThrow(() -> new RuntimeException("Post not found"));

        existing.setTitle(updatedFeed.getTitle());

        existing.setDescription(updatedFeed.getDescription());

        existing.setType(updatedFeed.getType());

        if (file != null && !file.isEmpty()) {

            // Optional: Delete old file if needed

            // fileService.deleteFile(existing.getImageUrl());

            String newUrl = fileService.uploadFile(file);

            existing.setImageUrl(newUrl);

        }

        return ResponseEntity.ok(feedRepo.save(existing));

    }

    @DeleteMapping("/delete/{id}")

    public ResponseEntity<String> deletePost(@PathVariable Integer id) {

        SchoolFeed existing = feedRepo.findById(id)

                .orElseThrow(() -> new RuntimeException("Post not found"));

        // Optional: Delete image from storage

        if (existing.getImageUrl() != null) {

            fileService.deleteFile(existing.getImageUrl());

        }

        feedRepo.delete(existing);

        return ResponseEntity.ok("Post deleted successfully");

    }

}
