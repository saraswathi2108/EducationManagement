package com.project.student.education.service;

import com.project.student.education.DTO.ClassSectionDTO;
import com.project.student.education.DTO.StudentDTO;
import com.project.student.education.entity.*;
import com.project.student.education.repository.*;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ClassSectionService {

    private final ClassSectionRepository classSectionRepository;
    private final TeacherRepository teacherRepository;
    private final IdGenerator idGenerator;
    private final ModelMapper modelMapper;
    private final StudentRepository studentRepository;
    private final ClassSubjectMappingRepository classSubjectMappingRepository;
    private final SubjectRepository subjectRepository;

    public ClassSectionDTO createClassSection(ClassSectionDTO dto) {

        // Duplicate check
        classSectionRepository.findByClassNameAndSectionAndAcademicYear(
                        dto.getClassName(), dto.getSection(), dto.getAcademicYear()
                )
                .ifPresent(existing -> {
                    throw new RuntimeException("Class section already exists for this academic year!");
                });

        // Generate class section ID
        String id = idGenerator.generateId("CLS");

        // Fetch class teacher
        Teacher classTeacher = null;

        if (dto.getClassTeacherId() != null) {
            classTeacher = teacherRepository.findById(dto.getClassTeacherId())
                    .orElseThrow(() ->
                            new RuntimeException("Teacher not found with ID: " + dto.getClassTeacherId()));
        }

// Create Class Section
        ClassSection classSection = ClassSection.builder()
                .classSectionId(id)
                .className(dto.getClassName())
                .section(dto.getSection())
                .academicYear(dto.getAcademicYear())
                .classTeacher(classTeacher)
                .capacity(dto.getCapacity())
                .currentStrength(dto.getCurrentStrength())
                .build();

        ClassSection savedClass = classSectionRepository.save(classSection);

        if (dto.getSubjectIds() != null) {
            for (String subjectId : dto.getSubjectIds()) {

                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

                String mappingId = idGenerator.generateId("CSM");

                ClassSubjectMapping mapping = ClassSubjectMapping.builder()
                        .id(mappingId)
                        .classSection(savedClass)
                        .subject(subject)
                        .teacher(null)
                        .build();

                classSubjectMappingRepository.save(mapping);
            }
        }

        return mapToDTO(savedClass);
    }


    public List<ClassSectionDTO> getAllClassSections() {
        return classSectionRepository.findAll()
                .stream()
                .map(this::mapToDTO)
                .toList();
    }

    public ClassSectionDTO getClassSection(String className, String section, String academicYear) {
        ClassSection found = classSectionRepository
                .findByClassNameAndAcademicYear(className, academicYear)
                .orElseThrow(() -> new RuntimeException("Class section not found!"));
        return mapToDTO(found);
    }

    public List<StudentDTO> getStudentsByClassSection(String classSectionId) {
        List<Student> students = studentRepository.findByClassSection_ClassSectionId(classSectionId);

        if (students.isEmpty()) {
            throw new RuntimeException("No students found for class section: " + classSectionId);
        }

        return students.stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .toList();
    }

    public ClassSectionDTO assignTeacher(String classSectionId, String teacherId, String teacherName) {
        ClassSection classSection = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        Teacher teacher = teacherRepository.findById(teacherId)
                .orElseThrow(() -> new RuntimeException("Teacher not found with ID: " + teacherId));

        classSection.setClassTeacher(teacher);

        ClassSection updated = classSectionRepository.save(classSection);
        return mapToDTO(updated);
    }

    @Transactional
    public ClassSectionDTO updateClassSection(String id, ClassSectionDTO dto) {

        ClassSection existing = classSectionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        existing.setClassName(dto.getClassName());
        existing.setSection(dto.getSection());
        existing.setAcademicYear(dto.getAcademicYear());
        existing.setCapacity(dto.getCapacity());
        existing.setCurrentStrength(dto.getCurrentStrength());
        if (dto.getClassTeacherId() != null) {
            Teacher teacher = teacherRepository.findById(dto.getClassTeacherId())
                    .orElseThrow(() ->
                            new RuntimeException("Teacher not found: " + dto.getClassTeacherId()));
            existing.setClassTeacher(teacher);
        } else {
            existing.setClassTeacher(null);
        }
        classSubjectMappingRepository.deleteByClassSection_ClassSectionId(id);
        if (dto.getSubjectIds() != null && !dto.getSubjectIds().isEmpty()) {
            for (String subjectId : dto.getSubjectIds()) {

                Subject subject = subjectRepository.findById(subjectId)
                        .orElseThrow(() -> new RuntimeException("Subject not found: " + subjectId));

                ClassSubjectMapping mapping = ClassSubjectMapping.builder()
                        .id(idGenerator.generateId("CSM"))
                        .classSection(existing)
                        .subject(subject)
                        .teacher(null)
                        .build();

                classSubjectMappingRepository.save(mapping);
            }
        }

        ClassSection saved = classSectionRepository.save(existing);
        return mapToDTO(saved);
    }


    private ClassSectionDTO mapToDTO(ClassSection section) {
        ClassSectionDTO dto = modelMapper.map(section, ClassSectionDTO.class);

        if (section.getClassTeacher() != null) {
            dto.setClassTeacherId(section.getClassTeacher().getTeacherId());
            dto.setClassTeacherName(section.getClassTeacher().getTeacherName());
        }
        List<ClassSubjectMapping> mappings =
                classSubjectMappingRepository.findByClassSection_ClassSectionId(section.getClassSectionId());

        List<String> subjectIds = mappings.stream()
                .map(m -> m.getSubject().getSubjectId())
                .toList();

        dto.setSubjectIds(subjectIds);


        return dto;
    }

    public ClassSectionDTO assignStudentToClassSection(String classSectionId, String studentId) {

        ClassSection classSection = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found"));

        student.setClassSection(classSection);
        student.setGrade(classSection.getClassName());
        student.setSection(classSection.getSection());
        studentRepository.save(student);

        int current = (classSection.getCurrentStrength() == null) ? 0 : classSection.getCurrentStrength();
        classSection.setCurrentStrength(current + 1);

        classSectionRepository.save(classSection);

        return mapToDTO(classSection);
    }


    @Transactional
    public ClassSectionDTO deleteClassSection(String classSectionId) {

        ClassSection classSection = classSectionRepository.findById(classSectionId)
                .orElseThrow(() -> new RuntimeException("Class section not found"));

        classSubjectMappingRepository.deleteByClassSection_ClassSectionId(classSectionId);
        classSectionRepository.delete(classSection);

        return mapToDTO(classSection);
    }

    public List<StudentDTO> getUnassignedStudentsByGrade(String grade) {
        return studentRepository.findByGradeAndClassSectionIsNull(grade)
                .stream()
                .map(student -> modelMapper.map(student, StudentDTO.class))
                .toList();
    }
}
