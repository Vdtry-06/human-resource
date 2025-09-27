package com.spring.human.resource.server.payload.rolepermission;

import com.spring.human.lib.enumerated.SystemRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class RolePermissionResponse {
    private String roleName;
    private String permissionName;

    public RolePermissionResponse(SystemRole role, String permissionName) {
        this.roleName = role.toString();
        this.permissionName = permissionName;
    }
}
