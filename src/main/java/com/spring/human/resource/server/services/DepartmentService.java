package com.spring.human.resource.server.services;

import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.entities.Department;
import com.spring.human.resource.server.payload.department.DepartmentRequest;
import com.spring.human.resource.server.payload.department.DepartmentResponse;
import com.spring.human.resource.server.repositories.DepartmentRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class DepartmentService extends BaseService<Department, Integer> {
    private final DepartmentRepository repository;
    private final MessageSourceHelper messageSourceHelper;

    protected DepartmentService(BaseRepository<Department, Integer> repository, MessageSourceHelper messageSourceHelper) {
        super(repository);
        this.repository = (DepartmentRepository) repository;
        this.messageSourceHelper = messageSourceHelper;
    }

    @Transactional(readOnly = true)
    public PaginationResponse<DepartmentResponse> getAllDepartmentWithConditions(int page, int prePage, String search) {
        long totalRecords = repository.countAllDepartmentWithConditions(search);
        int offset = PagingUtil.getOffSet(page, prePage);
        int totalPage = PagingUtil.getTotalPage(totalRecords, prePage);

        List<Department> departmentList = repository.findAllDepartmentWithConditions(offset, prePage, search);
        List<DepartmentResponse> responseList = new ArrayList<>();
        if (departmentList != null) {
            responseList = departmentList.stream().map(department -> responseBuilder(department)).toList();
        }
        return PaginationResponse.<DepartmentResponse>builder()
                .page(page)
                .prePage(prePage)
                .data(responseList)
                .totalPage(totalPage)
                .totalRecord(totalRecords)
                .build();

    }

    @Transactional(readOnly = true)
    public DepartmentResponse getDepartmentById(int id) {
        Department department = findByFields(Map.of("deparmentId", id));
        if (department == null) return new DepartmentResponse();
        return responseBuilder(department);
    }

    @Transactional(rollbackFor = Exception.class)
    public DepartmentResponse createDepartment(DepartmentRequest request) {
        Department department = entityBuilder(request);
        department = repository.save(department);
        return responseBuilder(department);
    }

    @Transactional(rollbackFor = Exception.class)
    public DepartmentResponse updateDepartmentById(int id, DepartmentRequest request) {
        Department department = findByFields(Map.of("departmentId", id));
        department.setDepartmentName(request.getDepartmentName());
        department.setDesciption(request.getDescription());
        department = repository.save(department);
        return responseBuilder(department);
    }

    public void deleteDepartmentById(int id) {
        try {
            Department department = findByFields(Map.of("departmentId", id));
            repository.delete(department);
        } catch (Exception e) {
            log.error(messageSourceHelper.getMessage(messageSourceHelper.getMessage("error.departmentNotFound", id)));
            throw new BadRequestException("Department with id " + id + " not found");
        }
    }

    private final Department entityBuilder(DepartmentRequest request) {
        return Department.builder()
                .departmentName(request.getDepartmentName())
                .desciption(request.getDescription())
                .build();
    }

    private DepartmentResponse responseBuilder(Department department) {
        return DepartmentResponse.builder()
                .departmentId(department.getDepartmentId())
                .departmentName(department.getDepartmentName())
                .description(department.getDesciption())
                .build();
    }
}
