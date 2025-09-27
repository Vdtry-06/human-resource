package com.spring.human.resource.server.controllers;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.resource.server.payload.rolepermission.RolePermissionRequest;
import com.spring.human.resource.server.services.RolePermissionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@RequiredArgsConstructor
@Tag(name = "role-permission")
@RequestMapping("role-permission")
public class RolePermissionController {
    private final RolePermissionService rolePermissionService;

    @Operation(summary = "API get all role-permissions")
    @GetMapping("/role-permissions")
    @PreAuthorize("hasAuthority('READ_ROLE_PERMISSION')")
    public ResponseEntity<?> getAllRolePermissions() {
        return ResponseEntity.ok(new ApiResponse<>(true, rolePermissionService.getAllRolePermission()));
    }

    @Operation(summary = "API find role-permissions by roleId, permissionId")
    @GetMapping("/role-permissions/{roleId}/{permissionId}")
    @PreAuthorize("hasAuthority('READ_ROLE_PERMISSION')")
    public ResponseEntity<?> findRolePermission(@PathVariable int roleId, @PathVariable int permissionId) {
        return ResponseEntity.ok(new ApiResponse<>(true, rolePermissionService.getRolePermission(
                new RolePermissionRequest(roleId, permissionId)
        )));
    }

    @Operation(summary = "API find permissions by roleId")
    @GetMapping("/roles/{roleId}/permissions")
    @PreAuthorize("hasAuthority('READ_ROLE_PERMISSION')")
    public ResponseEntity<?> findPermissionsByRoleId(@PathVariable int roleId) {
        return ResponseEntity.ok(new ApiResponse<>(true, rolePermissionService.getPermissionsByRoleId(roleId)));
    }

    @Operation(summary = "API find roles by permissions")
    @GetMapping("/permissions/{permissionId}/roles")
    @PreAuthorize("hasAuthority('READ_ROLE_PERMISSION')")
    public ResponseEntity<?> findRolesByPermissionId(@PathVariable int permissionId) {
        return ResponseEntity.ok(new ApiResponse<>(true, rolePermissionService.getRolesByPermissionId(permissionId)));
    }

    @Operation(summary = "API create permission for role")
    @PostMapping("/role-permissions")
    @PreAuthorize("hasAuthority('WRITE_ROLE_PERMISSION')")
    public ResponseEntity<?> createRolePermission(@RequestBody RolePermissionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, rolePermissionService.createRolePermission(request)));
    }

    @Operation(summary = "API delete permission for role")
    @PreAuthorize("hasAuthority('DELETE_ROLE_PERMISSION')")
    @DeleteMapping("/role-permissions/{roleId}/{permissionId}")
    public ResponseEntity<?> deleteRolePermission(@PathVariable int roleId, @PathVariable int permissionId) {
        rolePermissionService.deleteRolePermission(new RolePermissionRequest(roleId, permissionId));
        return ResponseEntity.ok(new ApiResponse<>(true, "RolePermission deleted successfully!"));
    }
}
