package com.spring.human.resource.server.controllers;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.payload.employee.EmployeeRequest;
import com.spring.human.resource.server.services.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "employee")
@RequestMapping("/employees")
public class EmployeeController {
    private final EmployeeService employeeService;

    @Operation(summary = "Get all employees filter by name, phone numer, status")
    @GetMapping
    @PreAuthorize("hasAuthority('READ_EMPLOYEES')")
    public ResponseEntity<?> getAllEmployeesWithCondtions(
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_SIZE) int prePage,
            @RequestParam(required = false, defaultValue = StringUtil.EMPTY) String search
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, employeeService.getAllEmployeeeWithConditions(page, prePage, search)));
    }

    @Operation(summary = "Get all employees filter by position id")
    @GetMapping("/position/{positionId}")
    @PreAuthorize("hasAuthority('READ_EMPLOYEES')")
    public ResponseEntity<?> getAllEmployeeByPosition(
            @PathVariable int positionId,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_SIZE) int perpage) {
        return ResponseEntity.ok(employeeService.findAllByPositionId(positionId, page, perpage));
    }

    @Operation(summary = "Get all employees filter by department id")
    @GetMapping("/department/{departmentId}")
    @PreAuthorize("hasAuthority('READ_EMPLOYEES')")
    public ResponseEntity<?> getAllEmployeeByDepartment(
            @PathVariable int departmentId,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_SIZE) int perpage) {
        return ResponseEntity.ok(employeeService.findAllByDepartmentId(departmentId, page, perpage));
    }

    @Operation(summary = "Get employee by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_EMPLOYEES')")
    public ResponseEntity<?> getEmployeeById(@PathVariable int id) {
        return ResponseEntity.ok(new ApiResponse<>(true, employeeService.getEmployeeById(id)));
    }

    @Operation(summary = "Create new employee")
    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_EMPLOYEES')")
    public ResponseEntity<?> createEmployee(@ParameterObject EmployeeRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, employeeService.createEmployee(request)));
    }

    @Operation(summary = "Update employee by id")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_EMPLOYEES')")
    public ResponseEntity<?> updateEmployeeById(@PathVariable("id") int id, @RequestBody EmployeeRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, employeeService.updateEmployeeById(id, request)));
    }

    @Operation(summary = "Delete employee by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_EMPLOYEES')")
    public ResponseEntity<?> deleteEmployeeById(@PathVariable("id") int id) {
        return ResponseEntity.ok(new ApiResponse<>(true, employeeService.deleteEmployeeById(id)));
    }
}
