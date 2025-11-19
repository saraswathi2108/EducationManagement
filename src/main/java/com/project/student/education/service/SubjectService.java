package com.project.student.education.service;

import com.project.student.education.DTO.AssignSubjectTeacherDTO;
import com.project.student.education.DTO.ClassSubjectAssignRequest;
import com.project.student.education.DTO.ClassSubjectMappingDTO;
import com.project.student.education.DTO.SubjectDTO;
import com.project.student.education.entity.*;
import com.project.student.education.repository.ClassSectionRepository;
import com.project.student.education.repository.ClassSubjectMappingRepository;
import com.project.student.education.repository.SubjectRepository;
import com.project.student.education.repository.TeacherRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SubjectService {

    private final ClassSectionRepository classSectionRepository;
    private final SubjectRepository subjectRepository;
    private final TeacherRepository teacherRepository;
    private final IdGenerator idGenerator;
    private final ModelMapper modelMapper;
    private final ClassSubjectMappingRepository classSubjectMapping;

    public SubjectDTO createSubject(SubjectDTO subjectDTO) {

        subjectRepository.findBySubjectNameIgnoreCase(subjectDTO.getSubjectName())
                .ifPresent(s -> { throw new RuntimeException("Subject name already exists!"); });

        subjectRepository.findBySubjectCodeIgnoreCase(subjectDTO.getSubjectCode())
                .ifPresent(s -> { throw new RuntimeException("Subject code already exists!"); });

        String id = idGenerator.generateId("SUB");

        Subject subject = Subject.builder()
                .subjectId(id)
                .subjectName(subjectDTO.getSubjectName())
                .subjectCode(subjectDTO.getSubjectCode())
                .active(true)
                .build();

        Subject saved = subjectRepository.save(subject);
        return modelMapper.map(saved, SubjectDTO.class);
    }

    public SubjectDTO updateSubject(String id, SubjectDTO dto) {
        Subject existing = subjectRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Subject not found!"));

        existing.setSubjectName(dto.getSubjectName());
        existing.setSubjectCode(dto.getSubjectCode());
        existing.setActive(dto.getActive());

        Subject updated = subjectRepository.save(existing);
        return modelMapper.map(updated, SubjectDTO.class);
    }

    public List<SubjectDTO> getAllSubjects() {
        return subjectRepository.findAll()
                .stream()
                .map(subject -> modelMapper.map(subject, SubjectDTO.class))
                .toList();
    }


    @Transactional
    public void deleteSubject(String subjectId) {

        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found!"));

        List<ClassSubjectMapping> mappings =
                classSubjectMapping.findBySubject_SubjectId(subjectId);

        for (ClassSubjectMapping m : mappings) {
            classSubjectMapping.delete(m);
        }
        subjectRepository.delete(subject);
    }


    public SubjectDTO getSubjectById(String subjectId) {
        Subject subject = subjectRepository.findById(subjectId)
                .orElseThrow(() -> new RuntimeException("Subject not found with ID: " + subjectId));
        return modelMapper.map(subject, SubjectDTO.class);
    }

    public List<ClassSubjectMappingDTO> assignSubjects(ClassSubjectAssignRequest req) {

        ClassSection classSection = classSectionRepository.findById(req.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("ClassSection not found"));

        List<ClassSubjectMappingDTO> result = new ArrayList<>();

        for (String subjectId : req.getSubjectIds()) {

            boolean exists = classSubjectMapping
                    .findByClassSection_ClassSectionIdAndSubject_SubjectId(
                            req.getClassSectionId(), subjectId)
                    .isPresent();

            if (exists) continue;

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

            Teacher teacher = null;
            if (req.getTeacherId() != null) {
                teacher = teacherRepository.findById(req.getTeacherId())
                        .orElseThrow(() -> new RuntimeException("Teacher not found"));
            }

            ClassSubjectMapping mapping = ClassSubjectMapping.builder()
                    .id(idGenerator.generateId("CSM"))
                    .classSection(classSection)
                    .subject(subject)
                    .teacher(teacher)
                    .build();

            ClassSubjectMapping saved = classSubjectMapping.save(mapping);
            result.add(convertToDTO(saved));
        }

        return result;
    }

    public List<ClassSubjectMappingDTO> updateSubjectsAndTeachers(ClassSubjectAssignRequest request) {

        ClassSection classSection = classSectionRepository.findById(request.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("ClassSection not found"));

        List<ClassSubjectMapping> existingMappings =
                classSubjectMapping.findByClassSection_ClassSectionId(request.getClassSectionId());

        Set<String> newSubjectIds = new HashSet<>(request.getSubjectIds());

        for (ClassSubjectMapping mapping : existingMappings) {
            if (!newSubjectIds.contains(mapping.getSubject().getSubjectId())) {
                classSubjectMapping.delete(mapping);
            }
        }

        List<ClassSubjectMappingDTO> result = new ArrayList<>();

        for (String subjectId : newSubjectIds) {

            Optional<ClassSubjectMapping> existing =
                    classSubjectMapping.findByClassSection_ClassSectionIdAndSubject_SubjectId(
                            request.getClassSectionId(), subjectId);

            Subject subject = subjectRepository.findById(subjectId)
                    .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

            Teacher teacher = null;
            if (request.getTeacherId() != null) {
                teacher = teacherRepository.findById(request.getTeacherId())
                        .orElseThrow(() -> new RuntimeException("Teacher not found"));
            }

            ClassSubjectMapping saved;

            if (existing.isPresent()) {
                ClassSubjectMapping m = existing.get();
                m.setTeacher(teacher);
                saved = classSubjectMapping.save(m);
            } else {
                saved = classSubjectMapping.save(ClassSubjectMapping.builder()
                        .id(idGenerator.generateId("CSM"))
                        .classSection(classSection)
                        .subject(subject)
                        .teacher(teacher)
                        .build());
            }

            result.add(convertToDTO(saved));
        }

        return result;
    }

    private ClassSubjectMappingDTO convertToDTO(ClassSubjectMapping m) {
        ClassSubjectMappingDTO dto = modelMapper.map(m, ClassSubjectMappingDTO.class);

        dto.setClassSectionId(m.getClassSection().getClassSectionId());
        dto.setSubjectId(m.getSubject().getSubjectId());
        dto.setSubjectName(m.getSubject().getSubjectName());
        dto.setTeacherId(m.getTeacher() != null ? m.getTeacher().getTeacherId() : null);
        dto.setTeacherName(m.getTeacher() != null ? m.getTeacher().getTeacherName() : null);

        return dto;
    }

    public List<ClassSubjectMappingDTO> getAssignedSubjects(String classSectionId) {

        List<ClassSubjectMapping> mappings =
                classSubjectMapping.findByClassSection_ClassSectionId(classSectionId);

        return mappings.stream()
                .map(this::convertToDTO)
                .toList();
    }

    public String assignTeacherToSubject(AssignSubjectTeacherDTO dto) {

        if (classSubjectMapping.existsByClassSection_ClassSectionIdAndSubject_SubjectId(
                dto.getClassSectionId(), dto.getSubjectId()
        )) {
            throw new RuntimeException("Teacher already assigned for this subject in this class.");
        }

        ClassSection section = classSectionRepository.findById(dto.getClassSectionId())
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        Subject subject = subjectRepository.findById(dto.getSubjectId())
                .orElseThrow(() -> new RuntimeException("Subject not found"));

        Teacher teacher = teacherRepository.findById(dto.getTeacherId())
                .orElseThrow(() -> new RuntimeException("Teacher not found"));

        ClassSubjectMapping mapping = ClassSubjectMapping.builder()
                .id(UUID.randomUUID().toString())
                .classSection(section)
                .subject(subject)
                .teacher(teacher)
                .build();

        classSubjectMapping.save(mapping);

        return subject.getSubjectName() + " assigned to " + teacher.getTeacherName() +
                " for class " + section.getClassName() + section.getSection();
    }

    public Map<String, Object> getMappingForClass(String classSectionId) {

        ClassSection section = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        List<ClassSubjectMapping> mappings =
                classSubjectMapping.findByClassSection_ClassSectionId(classSectionId);

        List<Map<String, String>> subjectTeacherList = mappings.stream()
                .map(m -> Map.of(
                        "subject", m.getSubject().getSubjectName(),
                        "teacher", m.getTeacher().getTeacherName()
                ))
                .toList();

        return Map.of(
                "classSection", section.getClassName() + section.getSection(),
                "subjects", subjectTeacherList
        );
    }

}
