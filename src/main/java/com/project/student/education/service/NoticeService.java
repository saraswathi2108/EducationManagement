package com.project.student.education.service;

import com.project.student.education.entity.IdGenerator;
import com.project.student.education.entity.Notice;
import com.project.student.education.repository.NoticeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoticeService {

    @Autowired
    private NoticeRepository noticeRepository;

    @Autowired
    private IdGenerator idGenerator;

    public Notice create(Notice notice) {

        if (noticeRepository.existsByNoticeName(notice.getNoticeName())) {
            throw new RuntimeException("Notice already exists with name: " + notice.getNoticeName());
        }
        notice.setId(idGenerator.generateId("NOT"));
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

        return noticeRepository.save(existing);
    }

    public void delete(String id) {

        Notice existing = noticeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Notice not found with id: " + id));

        noticeRepository.delete(existing);
    }
    public List<Notice> getAll() {
        return noticeRepository.findAll();
    }

}
