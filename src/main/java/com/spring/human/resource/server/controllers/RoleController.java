package com.spring.human.resource.server.controllers;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.payload.role.RoleRequest;
import com.spring.human.resource.server.services.RoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Log4j2
@Validated
@Tag(name = "role")
@RestController
@RequiredArgsConstructor
@RequestMapping("/roles")
public class RoleController {
    private final RoleService roleService;

    @Operation(summary = "API get all roles")
    @GetMapping("")
    @PreAuthorize("hasAuthority('READ_ROLES')")
    public ResponseEntity<?> getAllRoles() {
        return ResponseEntity.ok(new ApiResponse<>(true, roleService.findAll()));
    }

    @Operation(summary = "API get role by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_ROLES')")
    public ResponseEntity<?> getRoleById(@PathVariable("id") int id) {
        return ResponseEntity.ok(new ApiResponse<>(true, roleService.getRoleById(id)));
    }

    @Operation(summary = "API create new role")
    @PostMapping("")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    public ResponseEntity<?> createRole(@RequestBody RoleRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, roleService.createRole(request)));
    }

    @Operation(summary = "API update role by id")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_ROLES')")
    public ResponseEntity<?> updateRoleById(@PathVariable("id") int id, @RequestBody RoleRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, roleService.updateRoleById(id, request)));
    }

    @Operation(summary = "API delete role by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ROLES')")
    public ResponseEntity<?> deleteRoleById(@PathVariable("id") int id) {
        return ResponseEntity.ok(new ApiResponse<>(true, roleService.deleteRoleById(id)));
    }
}
