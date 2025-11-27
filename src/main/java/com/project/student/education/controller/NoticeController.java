package com.project.student.education.controller;


import com.project.student.education.entity.Notice;
import com.project.student.education.service.NoticeService;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/student/notice")
@AllArgsConstructor
public class NoticeController {

    @Autowired
    private NoticeService noticeService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create")
    public ResponseEntity<Notice> create(@RequestBody Notice notice) {
        Notice notice1 = noticeService.create(notice);
        return ResponseEntity.ok().body(notice1);
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/get/{id}")
    private ResponseEntity<Notice>getNotice(@PathVariable String id) {
        return ResponseEntity.ok(noticeService.getNotice(id));
    }
    @PreAuthorize("hasRole('ADMIN')")

    @PutMapping("/update/{id}")
    public ResponseEntity<Notice> updateNotice(
            @PathVariable String id,
            @RequestBody Notice notice) {

        return ResponseEntity.ok(noticeService.update(id, notice));
    }


    @PreAuthorize("hasRole('ADMIN')")

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteNotice(@PathVariable String id) {
        noticeService.delete(id);
        return ResponseEntity.ok("Notice deleted successfully");
    }

    @PreAuthorize("hasAnyRole('ADMIN','TEACHER','STUDENT')")

    @GetMapping("/all")
    public ResponseEntity<List<Notice>> getAllNotices() {
        return ResponseEntity.ok(noticeService.getAll());
    }

}
