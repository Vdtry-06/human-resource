package com.spring.human.resource.server.controllers;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.payload.auth.LoginRequest;
import com.spring.human.resource.server.payload.auth.LoginResponse;
import com.spring.human.resource.server.payload.user.UserRequest;
import com.spring.human.resource.server.payload.user.UserResponse;
import com.spring.human.resource.server.services.AuthService;
import com.spring.human.resource.server.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RestController
@Tag(name = "user")
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;
    private final AuthService authService;
    private final MessageSourceHelper messageSourceHelper;

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        return new ApiResponse<LoginResponse>(true, authService.login(loginRequest));
    }

    @PostMapping("/logout")
    public ApiResponse<Object> logout(HttpServletRequest request) throws BadRequestException {
        authService.logout(request);
        return new ApiResponse<Object>(true, messageSourceHelper.getMessage("notification.logoutSuccessfully"));
    }

    @Operation(summary = "API get all user")
    @GetMapping
    @PreAuthorize("hasAuthority('READ_USERS')")
    public PaginationResponse<UserResponse> getAllUsersWithConditions(
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int prePage,
            @RequestParam(required = false, defaultValue = StringUtil.EMPTY) String search
    ) {
        return userService.getAllUsersWithConditions(page, prePage, search);
    }

    @Operation(summary = "API get user by ID")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_USERS')")
    public ResponseEntity<UserResponse> getUserById(@PathVariable("id") int id) throws BadRequestException {
        return ResponseEntity.ok(userService.getUserById(id));
    }

    @GetMapping("/me")
    public ApiResponse<UserResponse> getCurrentUser() {
        return new ApiResponse<>(true, userService.getCurrentUser());
    }

    @Operation(summary = "API create user")
    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_USERS')")
    public ResponseEntity<UserResponse> createUser(@RequestBody UserRequest userRequest) throws BadRequestException {
        return ResponseEntity.ok(userService.createUser(userRequest));
    }

    @Operation(summary = "API update user")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_USERS')")
    public ResponseEntity<UserResponse> updateUser(@PathVariable("id") int id, @RequestBody UserRequest.UpdateUserRequest request) throws BadRequestException {
        return ResponseEntity.ok(userService.updateUserById(id, request));
    }

    @Operation(summary = "API delete user")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USERS')")
    public ResponseEntity<String> deleteUser(@PathVariable("id") int id) throws BadRequestException {
        return ResponseEntity.ok(userService.deleteUserById(id));
    }
}
