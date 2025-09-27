package com.spring.human.resource.server.services;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.enumerated.EmployeeStatus;
import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.entities.Department;
import com.spring.human.resource.server.entities.Employee;
import com.spring.human.resource.server.entities.Position;
import com.spring.human.resource.server.entities.User;
import com.spring.human.resource.server.payload.employee.EmployeeRequest;
import com.spring.human.resource.server.payload.employee.EmployeeResponse;
import com.spring.human.resource.server.repositories.DepartmentRepository;
import com.spring.human.resource.server.repositories.EmployeeRepository;
import com.spring.human.resource.server.repositories.PositionRepository;
import com.spring.human.resource.server.repositories.UserRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class EmployeeService extends BaseService<Employee, Integer> {
    private final EmployeeRepository repository;
    private final UserRepository userRepository;
    private final PositionRepository positionRepository;
    private final DepartmentRepository departmentRepository;
    private final MessageSourceHelper messageSourceHelper;

    protected EmployeeService(BaseRepository<Employee, Integer> repository, UserRepository userRepository, PositionRepository positionRepository, DepartmentRepository departmentRepository, MessageSourceHelper messageSourceHelper) {
        super(repository);
        this.repository = (EmployeeRepository) repository;
        this.userRepository = userRepository;
        this.positionRepository = positionRepository;
        this.departmentRepository = departmentRepository;
        this.messageSourceHelper = messageSourceHelper;
    }

    @Transactional(readOnly = true)
    public PaginationResponse<EmployeeResponse> getAllEmployeeeWithConditions(int page, int prePage, String search) {
        long totalRecords = repository.countEmployees(search);
        int offset = PagingUtil.getOffSet(page, prePage);
        int totalPage = PagingUtil.getTotalPage(totalRecords, prePage);
        List<Employee> employeeList = repository.searchEmployeesWithPagination(search, offset, totalPage);
        List<EmployeeResponse> responseList = employeeList.stream().map(employee -> toEmployeeResponse(employee)).toList();

        return PaginationResponse.<EmployeeResponse>builder()
                .page(page)
                .prePage(prePage)
                .data(responseList)
                .totalRecord(totalRecords)
                .totalPage(totalPage)
                .build();
    }

    @Transactional(readOnly = true)
    public EmployeeResponse getEmployeeById(int id) {
        Employee employee = repository.findById(id).orElse(null);
        if (employee == null) {
            throw new BadRequestException(messageSourceHelper.getMessage("error.employeeNotFound"));
        } else {
            return toEmployeeResponse(employee);
        }
    }

    @Transactional(rollbackFor = BadRequestException.class)
    public EmployeeResponse createEmployee(EmployeeRequest request) {
        Employee employee = toEmployee(request);
        employee = repository.save(employee);
        System.out.println(employee.getEmployeeId());
        return toEmployeeResponse(employee);
    }

    @Transactional(rollbackFor = BadRequestException.class)
    public EmployeeResponse updateEmployeeById(int id, EmployeeRequest request) {
        Employee employee = repository.findById(id).orElse(null);
        if (employee == null) throw new BadRequestException(messageSourceHelper.getMessage("error.employeeNotFound", id));
        if (request.getFirstName() != null) employee.setFirstName(request.getFirstName());
        if (request.getLastName() != null) employee.setLastName(request.getLastName());
        if (request.getDateOfBirth() != null) employee.setDateOfBirth(request.getDateOfBirth());
        if (request.getPhoneNumber() != null) employee.setPhoneNumber(request.getPhoneNumber());
        if (request.getAddress() != null) employee.setAddress(request.getAddress());
        if (request.getHireDate() != null) employee.setHireDate(request.getHireDate());
        if (request.getSalary() != null) employee.setSalary(request.getSalary());
        if (request.getStatus() != null) employee.setStatus(request.getStatus());

        User user = userRepository.findById(request.getUserId()).orElse(null);
        if (user != null) employee.setUserId(user);
        Position position = positionRepository.findById(request.getPositionId()).orElse(null);
        if (position != null) employee.setPositionId(position);
        Department department = departmentRepository.findById(request.getDepartmentId()).orElse(null);
        if (department != null) employee.setDepartmentId(department);

        repository.save(employee);
        return toEmployeeResponse(employee);

    }

    public ApiResponse<String> deleteEmployeeById(int id) {
        Employee employee = repository.findById(id).orElse(null);
        if (employee != null) {
            repository.delete(employee);
            return new ApiResponse<>(true, messageSourceHelper.getMessage("success.deleteEmployee", id));
        } else {
            return new ApiResponse<>(false, messageSourceHelper.getMessage("error.employeeNotFound", id));
        }
    }

    @Transactional(readOnly = true)
    public PaginationResponse<EmployeeResponse> findAllByDepartmentId(int id, int page, int prePage) {
        Department department = departmentRepository.findById(id).orElse(null);
        if (department != null) {
            Pageable pageable = PageRequest.of(page - 1, prePage);
            Page<Employee> employeePage = repository.findAllByDepartmentId(department, pageable);
            List<EmployeeResponse> listEmployee = employeePage.getContent().stream().map(e -> toEmployeeResponse(e)).toList();
            return PaginationResponse.<EmployeeResponse>builder()
                    .page(page)
                    .prePage(prePage)
                    .data(listEmployee)
                    .totalRecord(employeePage.getTotalElements())
                    .totalPage(employeePage.getTotalPages())
                    .build();
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.employeeNotFoundDepartmentId", id));
        }
    }

    @Transactional(readOnly = true)
    public PaginationResponse<EmployeeResponse> findAllByPositionId(int id, int page, int prePage) {
        Position position = positionRepository.findById(id).orElse(null);
        if (position != null) {
            Pageable pageable = PageRequest.of(page - 1, page);
            Page<Employee> employeePage = repository.findAllByPositionId(position, pageable);
            List<EmployeeResponse> listEmployee = employeePage.getContent().stream().map(e -> toEmployeeResponse(e)).toList();
            return PaginationResponse.<EmployeeResponse>builder()
                    .page(page)
                    .prePage(prePage)
                    .data(listEmployee)
                    .totalRecord(employeePage.getTotalElements())
                    .totalPage(employeePage.getTotalPages())
                    .build();
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.employeeNotFoundPositionId", id));
        }
    }

    private Employee toEmployee(EmployeeRequest request) {
        new Employee();
        return Employee.builder()
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .dateOfBirth(request.getDateOfBirth())
                .phoneNumber(request.getPhoneNumber())
                .address(request.getAddress())
                .hireDate(request.getHireDate() == null ? LocalDate.now() : request.getHireDate())
                .salary(request.getSalary())
                .status(request.getStatus() == null ? EmployeeStatus.DEFAULT : request.getStatus())
                .userId(userRepository.findById(request.getUserId()).orElse(null))
                .positionId(positionRepository.findById(request.getPositionId()).orElse(null))
                .departmentId(departmentRepository.findById(request.getDepartmentId()).orElse(null))
                .build();
    }

    private EmployeeResponse toEmployeeResponse(Employee employee) {
        new EmployeeResponse();
        return EmployeeResponse.builder()
                .id(employee.getEmployeeId())
                .firstName(employee.getFirstName())
                .lastName(employee.getLastName())
                .dateOfBirth(employee.getDateOfBirth())
                .phoneNumber(employee.getPhoneNumber())
                .address(employee.getAddress())
                .hireDate(employee.getHireDate())
                .salary(employee.getSalary())
                .status(employee.getStatus() != null ? employee.getStatus() : EmployeeStatus.valueOf(""))
                .userId(employee.getUserId() != null ? employee.getUserId().getUserId() : 0)
                .positionId(employee.getPositionId() != null ? employee.getPositionId().getPositionId() : 0)
                .departmentId(employee.getDepartmentId() != null ? employee.getDepartmentId().getDepartmentId() : 0)
                .build();
    }
}
