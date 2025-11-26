package com.project.student.education.service;

import com.project.student.education.DTO.AssignmentSubmissionDTO;
import com.project.student.education.config.AssignmentId;
import com.project.student.education.config.AssignmentSubmissionId;
import com.project.student.education.entity.Assignment;
import com.project.student.education.entity.AssignmentSubmission;
import com.project.student.education.repository.AssignmentRepository;
import com.project.student.education.repository.AssignmentSubmissionRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AssignmentSubmissionService {

    private final AssignmentSubmissionRepository submissionRepository;
    private final AssignmentRepository assignmentRepository;
    private final ModelMapper modelMapper;
    private final FileService fileService;

    public String submitAssignment(
            String assignmentId,
            String subjectId,
            AssignmentSubmissionDTO dto,
            List<MultipartFile> relatedLinks
    ) throws IOException {

        log.info("Creating submission for assignmentId={}, subjectId={}, student={}",
                assignmentId, subjectId, dto.getStudentId());

        Assignment assignment = assignmentRepository.findById(new AssignmentId(assignmentId, subjectId))
                .orElseThrow(() -> new EntityNotFoundException("Assignment not found"));

        Long maxNumber = submissionRepository.findMaxSubmissionNumber(assignmentId, subjectId);
        Long nextNumber = (maxNumber == null) ? 1L : maxNumber + 1;

        AssignmentSubmissionId compositeId = new AssignmentSubmissionId(
                new AssignmentId(assignmentId, subjectId),
                nextNumber
        );

        // --------- FILE UPLOAD ---------
        List<String> uploadedUrls = new ArrayList<>();

        if (relatedLinks != null && !relatedLinks.isEmpty()) {
            for (MultipartFile file : relatedLinks) {
                String uploadedUrl = fileService.uploadFile(file);  // Save to S3/local
                uploadedUrls.add(uploadedUrl);
            }
        }

        // --------- CREATE ENTITY ---------
        AssignmentSubmission submission = AssignmentSubmission.builder()
                .id(compositeId)
                .assignment(assignment)
                .studentId(dto.getStudentId())
                .note(dto.getNote())
                .relatedLinks(uploadedUrls)
                .status("SUBMITTED")
                .submittedDate(LocalDateTime.now())
                .build();

        submissionRepository.save(submission);

        log.info("Submission {} created successfully for assignment {}", nextNumber, assignmentId);

        return "Assignment submitted successfully";
    }



    public String reviewSubmission(String assignmentId, String subjectId, Long submissionNumber, AssignmentSubmissionDTO dto) {
        log.info("Reviewing submission {} for assignment {}-{}", submissionNumber, assignmentId, subjectId);

        AssignmentSubmissionId id = new AssignmentSubmissionId(new AssignmentId(assignmentId, subjectId), submissionNumber);
        AssignmentSubmission submission = submissionRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Submission not found"));

        submission.setRemark(dto.getRemark());
        submission.setReviewedBy(dto.getReviewedBy());
        submission.setStatus(dto.getStatus() != null ? dto.getStatus() : "REVIEWED");

        submissionRepository.save(submission);
        log.info("Submission {} reviewed by {}", submissionNumber, dto.getReviewedBy());

        return "Submission reviewed successfully";
    }


    public List<AssignmentSubmissionDTO> getSubmissions(String assignmentId, String subjectId) {
        log.info("Fetching submissions for assignment {}-{}", assignmentId, subjectId);
        return submissionRepository.findAll().stream()
                .filter(sub -> sub.getAssignment().getId().getAssignmentId().equals(assignmentId)
                        && sub.getAssignment().getId().getSubjectId().equals(subjectId))
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AssignmentSubmissionDTO> getSubmissionsByStudent(String studentId) {
        log.info("Fetching submissions by student {}", studentId);
        return submissionRepository.findByStudentId(studentId)
                .stream().map(this::toDTO).collect(Collectors.toList());
    }

    private AssignmentSubmissionDTO toDTO(AssignmentSubmission submission) {

        return AssignmentSubmissionDTO.builder()
                .assignmentId(submission.getAssignment().getId().getAssignmentId())
                .subjectId(submission.getAssignment().getId().getSubjectId())
                .submissionNumber(submission.getId().getSubmissionNumber())
                .studentId(submission.getStudentId())
                .note(submission.getNote())
                .relatedLinks(submission.getRelatedLinks())
                .status(submission.getStatus())
                .remark(submission.getRemark())
                .reviewedBy(submission.getReviewedBy())
                .submittedDate(submission.getSubmittedDate())
                .build();
    }

}
