package com.spring.human.resource.server.services;

import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.enumerated.SystemRole;
import com.spring.human.lib.exceptions.UnAuthorizationException;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.configs.security.SecurityHelper;
import com.spring.human.resource.server.entities.Department;
import com.spring.human.resource.server.entities.Employee;
import com.spring.human.resource.server.entities.Role;
import com.spring.human.resource.server.entities.User;
import com.spring.human.resource.server.payload.user.UserRequest;
import com.spring.human.resource.server.payload.user.UserResponse;
import com.spring.human.resource.server.repositories.*;
import com.spring.human.resource.server.repositories.model.EmployeeModel;
import com.spring.human.resource.server.repositories.model.UserModel;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
@Log4j2
public class UserService extends BaseService<User, Integer> {
    private final UserRepository repository;
    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final PositionRepository positionRepository;
    private final RoleRepository roleRepository;
    private final SecurityHelper securityHelper;
    private final MessageSourceHelper messageSourceHelper;
    private final EmployeeService employeeService;

    protected UserService(BaseRepository<User, Integer> repository, EmployeeRepository employeeRepository, DepartmentRepository departmentRepository, PositionRepository positionRepository, RoleRepository roleRepository,
                          SecurityHelper securityHelper, MessageSourceHelper messageSourceHelper, EmployeeService employeeService) {
        super(repository);
        this.repository = (UserRepository) repository;
        this.employeeRepository = employeeRepository;
        this.departmentRepository = departmentRepository;
        this.positionRepository = positionRepository;
        this.roleRepository = roleRepository;
        this.securityHelper = securityHelper;
        this.messageSourceHelper = messageSourceHelper;
        this.employeeService = employeeService;
    }

    @Transactional(readOnly = true)
    public PaginationResponse<UserResponse> getAllUsersWithConditions(int page, int prePage, String search) {
        User currentUserLogin = findByFields(Map.of("username", securityHelper.getCurrentUserLogin()));
        String currentUserRole = currentUserLogin.getRoleId().getRoleName().name();

        long totalRecord;

        int offset, totalPage;
        List<UserModel> userList;

        switch (currentUserRole) {
            case "ADMIN":
                totalRecord = repository.countUsersBySearch(search);
                offset = PagingUtil.getOffSet(page, prePage);
                totalPage = PagingUtil.getTotalPage(totalRecord, prePage);
                userList = repository.searchUsersWithPagination(search, prePage, offset);
                break;
            case "MANAGER":
                List<Employee> employees = currentUserLogin.getEmployees();
                List<Department> departments = employees.stream().map(Employee::getDepartmentId).distinct().toList();
                int departmentId = departments.get(0).getDepartmentId();
                totalRecord = repository.countUsersByDepartment(departmentId, search);
                offset = PagingUtil.getOffSet(page, prePage);
                totalPage = PagingUtil.getTotalPage(totalRecord, prePage);
                userList = repository.findAllUsersByDepartment(departmentId, offset, prePage, search);
                break;
            case "EMPLOYEE":
                throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
            default:
                throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        }

        List<UserResponse> response = new ArrayList<>();
        if (userList != null) {
            response = userList.stream().map(user -> responseBuilder(user)).toList();
        }

        return PaginationResponse.<UserResponse>builder().page(page).prePage(prePage).data(response)
                .totalPage(totalPage).totalRecord(totalRecord).build();

    }

    public UserResponse getUserById(int id) throws BadRequestException {
        User currentUserLogin = findByFields(Map.of("username", securityHelper.getCurrentUserLogin()));
        String currentUserRole = currentUserLogin.getRoleId().getRoleName().name();
        UserModel user;
        EmployeeModel e;

        switch (currentUserRole) {
            case "ADMIN":
                user = repository.findUserById(id);
                break;
            case "MANAGER":
                List<Employee> employees = currentUserLogin.getEmployees();
                List<Department> departments = employees.stream().map(Employee::getDepartmentId).distinct().toList();
                int departmentId = departments.get(0).getDepartmentId();
                e = employeeRepository.findEmployeeByUserId(id);
                if(departmentId != e.getDepartmentId()) {
                    throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
                }
                user = repository.findUserByIdAndDepartment(id,departmentId);
                break;
            case "EMPLOYEE":
                if (currentUserLogin.getUserId() != id)
                    throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
                user = repository.findUserById(id);
            default:
                throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        }

        if (user != null)
            return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(), user.getRoleName());
        else
            throw new BadRequestException(messageSourceHelper.getMessage("error.employeeNotFoundDepartmentId", id));
    }

    // Nếu bất kỳ Exception nào (checked hoặc unchecked) xảy ra, transaction cũng sẽ rollback.
    // Không chỉ RuntimeException mà kể cả Exception thông thường cũng được rollback.
    @Transactional(rollbackFor = BadRequestException.class)
    public UserResponse createUser(UserRequest request) throws BadRequestException {
        User currentUserLogin = findByFields(Map.of("username", securityHelper.getCurrentUserLogin()));
        String currentUserRole = currentUserLogin.getRoleId().getRoleName().name();
        EmployeeModel currentUserEmployee = employeeRepository.findEmployeeByUserId(currentUserLogin.getUserId());

        if (currentUserRole.equals(SystemRole.EMPLOYEE.name())) {
            throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        }

        Role roleRequest = roleRepository.findById(request.getRoleId())
                .orElseThrow(() -> new BadRequestException(messageSourceHelper.getMessage("error.notFound")));

        Role roleCurrentUser = roleRepository.findById(currentUserLogin.getRoleId().getRoleId())
                .orElseThrow(() -> new BadRequestException(messageSourceHelper.getMessage("error.notFound")));

        Integer departmentId = currentUserEmployee.getDepartmentId() != null ? currentUserEmployee.getDepartmentId()
                : null;

        if (roleRequest.getRoleName().equals(SystemRole.ADMIN)) {
            throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        }

        if (currentUserRole.equals(SystemRole.ADMIN.name()))
            departmentId = request.getDepartmentId();

        if (roleCurrentUser.getRoleName().equals(SystemRole.MANAGER)) {
            if (roleCurrentUser.getRoleName().equals(roleRequest.getRoleName()))
                throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        }

        User user = new User();
        user.setUsername(request.getUsername());

        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user.setRoleId(roleRequest);

        user = repository.save(user);

        if (currentUserRole.equals(SystemRole.MANAGER.name()) || currentUserRole.equals(SystemRole.ADMIN.name())) {
            employeeRepository.save(Employee.builder().firstName(request.getFirstName()).lastName(request.getLastName())
                    .dateOfBirth(request.getDateOfBirth())
                    .departmentId(departmentRepository.findById(departmentId).orElse(null))
                    .positionId(positionRepository.findById(request.getPositionId()).orElse(null))
                    .hireDate(LocalDate.now()).userId(user).build());
        }

        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(),
                user.getRoleId().getRoleName().name());
    }

    // Nếu bất kỳ Exception nào (checked hoặc unchecked) xảy ra, transaction cũng sẽ rollback.
    // Không chỉ RuntimeException mà kể cả Exception thông thường cũng được rollback.
    @Transactional(rollbackFor = BadRequestException.class)
    public UserResponse updateUserById(int id, UserRequest.UpdateUserRequest request) throws BadRequestException {
        User currentUserLogin = findByFields(Map.of("username", securityHelper.getCurrentUserLogin()));
        String currentUserRole = currentUserLogin.getRoleId().getRoleName().name();
        switch (currentUserRole) {
            case "ADMIN":
                break;
            case "MANAGER":
                EmployeeModel currentUserEmployee = employeeRepository.findEmployeeByUserId(currentUserLogin.getUserId());
                EmployeeModel updatingEmployee = employeeRepository.findEmployeeByUserId(id);
                if (currentUserEmployee.getDepartmentId() != updatingEmployee.getDepartmentId())
                    throw new BadRequestException("warning.accessDenied");
                break;
            case "EMPLOYEE":
                if(id != currentUserLogin.getUserId()) {
                    throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
                }
                break;
            default:
                throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        }

        User user = repository.findById(id).orElse(null);
        if (user == null)
            throw new BadRequestException(messageSourceHelper.getMessage("error.notFound"));

        user.setUsername(request.getUsername());
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder(12);
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setEmail(request.getEmail());
        user = repository.save(user);
        return new UserResponse(user.getUserId(), user.getUsername(), user.getEmail(),
                user.getRoleId().getRoleName().name());
    }

    // Nếu bất kỳ Exception nào (checked hoặc unchecked) xảy ra, transaction cũng sẽ rollback.
    // Không chỉ RuntimeException mà kể cả Exception thông thường cũng được rollback.
    @Transactional(rollbackFor = BadRequestException.class)
    public String deleteUserById(int id) throws BadRequestException {
        User currentUserLogin = findByFields(Map.of("username", securityHelper.getCurrentUserLogin()));
        if (!currentUserLogin.getRoleId().getRoleName().equals(SystemRole.ADMIN))
            throw new UnAuthorizationException(messageSourceHelper.getMessage("warning.accessDenied"));
        User user = repository.findById(id).orElse(null);
        if (user == null)
            return "User not found!";

        if (user.getRoleId().getRoleName().equals(SystemRole.ADMIN)) {
            throw new BadRequestException(messageSourceHelper.getMessage("error.deleteAdmin"));
        }

        Employee userEmployee = employeeService.findByFields(Map.of("userId", user));
        if (userEmployee != null)
            employeeRepository.delete(userEmployee);

        repository.delete(user);
        return "Delete user " + id + " successfully!";
    }

    // Chỉ lấy dữ liệu ra khi đọc đúng
    // Tối ưu cho việc query dữ liệu, không ghi xuống DB
    @Transactional(readOnly = true)
    public UserResponse getCurrentUser() {
        String username = securityHelper.getCurrentUserLogin();
        User user = findByFields(Map.of("username", username));
        log.info("Username: " + username);

        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRoleId().getRoleName().name())
                .build();
    }

    private UserResponse responseBuilder(UserModel user) {
        return UserResponse.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .email(user.getEmail())
                .roleName(user.getRoleName())
                .build();
    }
}
