package com.spring.human.resource.server.controllers;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.payload.department.DepartmentRequest;
import com.spring.human.resource.server.payload.department.DepartmentResponse;
import com.spring.human.resource.server.services.DepartmentService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@RequiredArgsConstructor
@Tag(name = "department")
@RequestMapping("/departments")
public class DepartmentController {
    private final DepartmentService departmentService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_DEPARTMENTS')")
    public PaginationResponse<DepartmentResponse> getDepartmentWithCondtions(
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int prePage,
            @RequestParam(required = false, defaultValue = StringUtil.EMPTY) String search
    ) {
        return departmentService.getAllDepartmentWithConditions(page, prePage, search);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_DEPARTMENTS')")
    public ApiResponse<DepartmentResponse> getDepartmentById(@PathVariable("id") int id) {
        DepartmentResponse response = departmentService.getDepartmentById(id);
        return new ApiResponse<DepartmentResponse>(true, response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_DEPARTMENTS')")
    public ApiResponse<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return new ApiResponse<>(true, response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_DEPARTMENTS')")
    public ApiResponse<DepartmentResponse> updateDepartmentById(@PathVariable("id") int id, @Valid @RequestBody DepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartmentById(id, request);
        return new ApiResponse<DepartmentResponse>(true, response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_DEPARTMENTS')")
    public ApiResponse<String> deleteDepartmentById(@PathVariable("id") int id) {
        departmentService.deleteDepartmentById(id);
        return new ApiResponse<String>(true);
    }
}
