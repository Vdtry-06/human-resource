package com.spring.human.resource.server.services;

import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.entities.Permission;
import com.spring.human.resource.server.entities.Role;
import com.spring.human.resource.server.payload.permission.PermissionResponse;
import com.spring.human.resource.server.payload.role.RoleResponse;
import com.spring.human.resource.server.payload.rolepermission.RolePermissionRequest;
import com.spring.human.resource.server.payload.rolepermission.RolePermissionResponse;
import com.spring.human.resource.server.repositories.PermissionRepository;
import com.spring.human.resource.server.repositories.RoleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class RolePermissionService extends BaseService<Role, Integer> {
    private final RoleRepository repository;
    private final PermissionRepository permissionRepository;
    private final PermissionService permissionService;
    private final MessageSourceHelper messageSourceHelper;

    protected RolePermissionService(BaseRepository<Role, Integer> repository, PermissionRepository permissionRepository, PermissionService permissionService, MessageSourceHelper messageSourceHelper) {
        super(repository);
        this.repository = (RoleRepository) repository;
        this.permissionRepository = permissionRepository;
        this.permissionService = permissionService;
        this.messageSourceHelper = messageSourceHelper;
    }

    @Transactional(readOnly = true)
    public List<RolePermissionService> getAllRolePermission() {
        return repository.findAllRolePermission();
    }

    @Transactional(readOnly = true)
    public RolePermissionResponse getRolePermission(RolePermissionRequest request) {
        return repository.findRolePermission(request.getRoleId(), request.getPermissionId()).orElse(null);
    }


    public RolePermissionResponse createRolePermission(RolePermissionRequest request) {
        Role role = repository.findById(request.getRoleId()).orElse(null);
        Permission permission = permissionRepository.findById(request.getPermissionId()).orElse(null);
        if(role!= null && permission != null) {
            role.getPermissions().add(permission);
            repository.save(role);
            return new RolePermissionResponse(
                    role.getRoleName().toString(),
                    permission.getPermissionName()
            );
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.roleNotFound", request.getRoleId()));
        }
    }

    public void deleteRolePermission(RolePermissionRequest request) {
        Role role = repository.findById(request.getRoleId()).orElse(null);
        Permission permission = permissionRepository.findById(request.getPermissionId()).orElse(null);
        if(role!= null && permission != null) {
            role.getPermissions().remove(permission);
            repository.save(role);
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.deleteRolePermission", request.getRoleId(), request.getPermissionId()));
        }
    }

    public List<RoleResponse> getRolesByPermissionId(int id) {
        return permissionService.getRoleListByPermissionId(id);
    }

    public List<PermissionResponse> getPermissionsByRoleId(int id) {
        List<PermissionResponse> list = new ArrayList<>();
        repository.findById(id).orElse(null).getPermissions().forEach(permission -> {
            list.add(new PermissionResponse(
                    permission.getPermissionId(),
                    permission.getPermissionName(),
                    permission.getDescription()
            ));
        });
        return list;
    }
}
