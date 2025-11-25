package com.project.student.education.repository;

import com.fasterxml.jackson.databind.introspect.AnnotationCollector;
import com.project.student.education.entity.FeeStructure;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeStructureRepository extends JpaRepository<FeeStructure, String> {

    List<FeeStructure> findByClassSectionIdAndAcademicYear(String classId, String academicYear);

    List<FeeStructure> findByRouteIdAndAcademicYear(String routeId, String academicYear);


    Optional<FeeStructure> findByFeeHeadIdAndClassSectionIdAndRouteIdAndTermCodeAndAcademicYear(String feeHeadId, String classSectionId, String routeId, String termCode, String academicYear);
}
