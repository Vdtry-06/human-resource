package com.spring.human.resource.server.controllers;

import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.payload.permission.PermissionRequest;
import com.spring.human.resource.server.services.PermissionService;
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
@Tag(name = "permisison")
@RequestMapping("/permissions")
public class PermissionController {
    private final PermissionService permissionService;

    @Operation(summary = "API get all permissions")
    @GetMapping
    @PreAuthorize("hasAuthority('READ_PERMISSIONS')")
    public ResponseEntity<?> getAllPermissionsWithConditions(
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int prePage,
            @RequestParam(required = false, defaultValue = StringUtil.EMPTY) String search
    ) {
        return ResponseEntity.ok(new ApiResponse<>(true, permissionService.getAllPermissionsWithCondtions(page, prePage, search)));
    }

    @Operation(summary = "API get permisison by id")
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_PERMISSIONS')")
    public ResponseEntity<?> getPermissionById(@PathVariable("id") int id) {
        return ResponseEntity.ok(new ApiResponse<>(true, permissionService.getPermissionById(id)));
    }

    @Operation(summary = "API create new permisison")
    @PostMapping("")
    @PreAuthorize("hasAuthority('WRITE_PERMISSIONS')")
    public ResponseEntity<?> createPermission(@RequestBody PermissionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, permissionService.createPermission(request)));
    }

    @Operation(summary = "API update permisison by id")
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_PERMISSIONS')")
    public ResponseEntity<?> updatePermissionById(@PathVariable("id") int id, @RequestBody PermissionRequest request) {
        return ResponseEntity.ok(new ApiResponse<>(true, permissionService.updatePermissionById(id, request)));
    }

    @Operation(summary = "API delete permisison by id")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_PERMISSIONS')")
    public ResponseEntity<?> deletePermissionById(@PathVariable("id") int id) {
        return ResponseEntity.ok(new ApiResponse<>(true, permissionService.deletePermissionById(id)));
    }
}
