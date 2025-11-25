package com.project.student.education.service;


import com.project.student.education.entity.FeeHead;
import com.project.student.education.entity.FeeStructure;
import com.project.student.education.entity.IdGenerator;
import com.project.student.education.repository.FeeHeadRepository;
import com.project.student.education.repository.FeeStructureRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FeeService {

    private final FeeHeadRepository feeHeadRepository;
    private final IdGenerator idGenerator;
    private final FeeStructureRepository feeStructureRepository;


    public FeeHead create(FeeHead feeHead) {
        if (feeHeadRepository.existsByName(feeHead.getName())) {
            throw new RuntimeException("Fee head with same name already exists");
        }
        feeHead.setId(idGenerator.generateId("FEE"));
        return feeHeadRepository.save(feeHead);
    }

    public FeeHead getById(String id) {
        return feeHeadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Fee head not found"));
    }
    public List<FeeHead> getAll() {
        return feeHeadRepository.findAll();
    }

    public FeeStructure createFeeStructure(FeeStructure fs) {

        boolean exists = feeStructureRepository
                .findByFeeHeadIdAndClassSectionIdAndRouteIdAndTermCodeAndAcademicYear(
                        fs.getFeeHeadId(),
                        fs.getClassSectionId(),
                        fs.getRouteId(),
                        fs.getTermCode(),
                        fs.getAcademicYear()
                ).isPresent();

        if (exists) {
            throw new RuntimeException("Fee structure already exists for this configuration");
        }

        fs.setId(idGenerator.generateId("FEE"));
        return feeStructureRepository.save(fs);
    }

    public List<FeeStructure> getAllfee() {
        return feeStructureRepository.findAll();
    }

    public List<FeeStructure> getByClassAndYear(String classId, String academicYear) {
        return feeStructureRepository.findByClassSectionIdAndAcademicYear(classId, academicYear);

    }

    public List<FeeStructure> getByRouteAndYear(String routeId, String academicYear) {
        return feeStructureRepository.findByRouteIdAndAcademicYear(routeId, academicYear);
    }
}
