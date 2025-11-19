package com.project.student.education.service;


import com.project.student.education.DTO.AssignmentDTO;
import com.project.student.education.config.AssignmentId;
import com.project.student.education.entity.*;
import com.project.student.education.repository.AssignmentRepository;
import com.project.student.education.repository.ClassSectionRepository;
import com.project.student.education.repository.SubjectRepository;
import com.project.student.education.repository.TeacherRepository;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AssignmentService {

    @Autowired
    private TeacherRepository teacherRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private ClassSectionRepository classSectionRepository;
    @Autowired
    private IdGenerator idGenerator;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AssignmentRepository assignmentRepository;

    public AssignmentDTO createAssignment(String teacherId, String subjectId, String classSectionId, AssignmentDTO assignmentDTO) {

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(()->new RuntimeException("Teacher Not Found"));

        Subject subject=subjectRepository.findById(subjectId)
                .orElseThrow(()->new RuntimeException("Subject Not Found"));

        ClassSection classSection=classSectionRepository.findById(classSectionId)
                .orElseThrow(()-> new RuntimeException("Class Section Not Found"));


        if (assignmentRepository.existsByTitleAndSubject_SubjectIdAndClassSection_ClassSectionId(assignmentDTO.getTitle(),subjectId,classSectionId)){
            throw new RuntimeException("Assignment with same title already exists for this subject & class.");

        }
        String newId=idGenerator.generateId("assignment");
        Assignment assignment= Assignment.builder()
                .id(new AssignmentId(newId,subjectId))
                .title(assignmentDTO.getTitle())
                .description(assignmentDTO.getDescription())
                .createdBy(teacherId)
                .assignedTo(classSectionId)
                .status("ASSIGNED")
                .priority(assignmentDTO.getPriority())
                .assignedDate(LocalDate.now())
                .dueDate(assignmentDTO.getDueDate())
                .attachedFiles(assignmentDTO.getAttachedFiles())
                .teacher(teacher)
                .subject(subject)
                .classSection(classSection)
                .build();
        Assignment saved = assignmentRepository.save(assignment);
        return toDTO(saved);


    }

    private AssignmentDTO toDTO(Assignment a) {
        return AssignmentDTO.builder()
                .assignmentId(a.getId().getAssignmentId())
                .subjectId(a.getId().getSubjectId())
                .title(a.getTitle())
                .description(a.getDescription())
                .createdBy(a.getTeacher() != null ? a.getTeacher().getTeacherName() : a.getCreatedBy())
                .assignedTo(a.getClassSection() != null
                        ? a.getClassSection().getClassName() + a.getClassSection().getSection()
                        : a.getAssignedTo())
                .status(a.getStatus())
                .priority(a.getPriority())
                .assignedDate(a.getAssignedDate())
                .dueDate(a.getDueDate())
                .attachedFiles(a.getAttachedFiles())
                .build();
    }


    public AssignmentDTO updateAssignment(String subjectId, String assignmentId, AssignmentDTO assignmentDTO) {
        Assignment assignment = assignmentRepository.findById(new AssignmentId(assignmentId, subjectId))
                .orElseThrow(() -> new RuntimeException("Assignment Not Found"));
        if (assignmentDTO.getTitle() != null)
            assignment.setTitle(assignmentDTO.getTitle());

        if (assignmentDTO.getDescription() != null)
            assignment.setDescription(assignmentDTO.getDescription());

        if (assignmentDTO.getPriority() != null)
            assignment.setPriority(assignmentDTO.getPriority());

        if (assignmentDTO.getDueDate() != null)
            assignment.setDueDate(assignmentDTO.getDueDate());

        if (assignmentDTO.getAttachedFiles() != null)
            assignment.setAttachedFiles(assignmentDTO.getAttachedFiles());

        if (assignmentDTO.getStatus() != null)
            assignment.setStatus(assignmentDTO.getStatus());

        Assignment updated = assignmentRepository.save(assignment);
        return toDTO(updated);
    }

    public AssignmentDTO getAssignment(String subjectId, String assignmentId) {
        Assignment assignment=assignmentRepository.findById(new AssignmentId(assignmentId,subjectId))
                .orElseThrow(() -> new RuntimeException("Assignment Not Found"));

        AssignmentDTO dto= modelMapper.map(assignment, AssignmentDTO.class);
        dto.setAssignmentId(assignment.getId().getAssignmentId());
        dto.setSubjectId(assignment.getId().getSubjectId());

        dto.setCreatedBy(assignment.getTeacher() != null ? assignment.getTeacher().getTeacherName() : assignment.getCreatedBy());
        dto.setAssignedTo(assignment.getClassSection() != null ? assignment.getClassSection().getClassName() : assignment.getAssignedTo());

        return dto;
    }

    public AssignmentDTO deleteAssignment(String subjectId, String assignmentId) {

        Assignment assignment=assignmentRepository.findById(new AssignmentId(assignmentId,subjectId))
                .orElseThrow(() -> new RuntimeException("Assignment Not Found"));
        AssignmentDTO deletedAssignment = modelMapper.map(assignment, AssignmentDTO.class);
        deletedAssignment.setAssignmentId(assignment.getId().getAssignmentId());
        deletedAssignment.setSubjectId(assignment.getId().getSubjectId());
        assignmentRepository.delete(assignment);

        return  deletedAssignment;

    }

    public List<AssignmentDTO> getAllByTeacher(String teacherId) {
       List<Assignment> assignment=assignmentRepository.findByTeacher_TeacherId(teacherId);
        if(assignment.isEmpty()) {
            throw new RuntimeException("No assignments found for teacher ID: " + teacherId);
        }
        return  assignment.stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    public List<AssignmentDTO> getAllAssignmentsBySubject(String subjectId) {
        List<Assignment> assignments=assignmentRepository.findBySubject_SubjectId(subjectId);
        if(assignments.isEmpty()) {
            throw new RuntimeException("No assignments found for subject ID: " + subjectId);
        }
        return  assignments.stream().map(this::toDTO).collect(Collectors.toList());

    }

    public List<AssignmentDTO> getAllAssignmentsByClass(String classSectionId) {
        List<Assignment>assignment=assignmentRepository.findByClassSection_ClassSectionId(classSectionId);
        if(assignment.isEmpty()) {
            throw new RuntimeException("No assignments found for class section ID: " + classSectionId);

        }
        return  assignment.stream().map(this::toDTO).collect(Collectors.toList());
    }
}
