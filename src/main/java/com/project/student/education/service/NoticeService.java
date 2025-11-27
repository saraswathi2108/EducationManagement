package com.project.student.education.service;

import com.project.student.education.entity.IdGenerator;
import com.project.student.education.entity.Notice;
import com.project.student.education.repository.NoticeRepository;
import com.project.student.education.repository.StudentRepository;
import com.project.student.education.repository.TeacherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private StudentRepository studentRepository;

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private NotificationService notificationService;

    public Notice create(Notice notice) {

        if (noticeRepository.existsByNoticeName(notice.getNoticeName())) {
            throw new RuntimeException("Notice already exists with name: " + notice.getNoticeName());
        }
        notice.setId(idGenerator.generateId("NOT"));
        notifyAllUsers(
                "New Notice Posted",
                notice.getNoticeName() + ": " + notice.getNoticeDescription()
        );

        return noticeRepository.save(notice);
    }

    public Notice getNotice(String id) {
        return noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));
    }
    public Notice update(String id, Notice updatedNotice) {

        Notice existing = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));

        // ✅ duplicate check excluding current record
        if (noticeRepository.existsByNoticeName(updatedNotice.getNoticeName())
                && !existing.getNoticeName().equals(updatedNotice.getNoticeName())) {
            throw new RuntimeException("Another notice already exists with name: " + updatedNotice.getNoticeName());
        }

        existing.setNoticeName(updatedNotice.getNoticeName());
        existing.setNoticeDescription(updatedNotice.getNoticeDescription());
        existing.setNoticeType(updatedNotice.getNoticeType());
        existing.setNoticeDate(updatedNotice.getNoticeDate());
       Notice saved= noticeRepository.save(existing);
        notifyAllUsers(
                "Notice Updated",
                saved.getNoticeName() + " has been updated."
        );
        return saved;
    }

    public void delete(String id) {

        Notice existing = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));

        noticeRepository.delete(existing);
    }
    public List<Notice> getAll() {
        return noticeRepository.findAll();
    }
    private void notifyAllUsers(String title, String message) {

        // Notify all students
        List<String> studentIds = studentRepository.findAllStudentIds();
        for (String sid : studentIds) {
            notificationService.sendNotification(sid, title, message, "NOTICE");
        }

        // Notify all teachers
        List<String> teacherUsernames = teacherRepository.findAllTeacherIds();
        for (String tid : teacherUsernames) {
            notificationService.sendNotification(tid, title, message, "NOTICE");
        }
    }

}
