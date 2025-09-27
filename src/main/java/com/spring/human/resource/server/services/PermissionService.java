package com.spring.human.resource.server.services;

import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.entities.Permission;
import com.spring.human.resource.server.payload.permission.PermissionRequest;
import com.spring.human.resource.server.payload.permission.PermissionResponse;
import com.spring.human.resource.server.payload.role.RoleResponse;
import com.spring.human.resource.server.repositories.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
public class PermissionService extends BaseService<Permission, Integer> {

    private final PermissionRepository repository;
    private final MessageSourceHelper messageSourceHelper;

    protected PermissionService(BaseRepository<Permission, Integer> repository, MessageSourceHelper messageSourceHelper) {
        super(repository);
        this.repository = (PermissionRepository) repository;
        this.messageSourceHelper = messageSourceHelper;
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public PaginationResponse<PermissionResponse> getAllPermissionsWithCondtions(int page, int prePage, String search) {
        long totalRecords = repository.countAllPositionWithConditions(search);
        
        int offset = PagingUtil.getOffSet(page, prePage);
        
        int totalPage = PagingUtil.getTotalPage(totalRecords, prePage);
        List<Permission> permissionList = repository.findAllPermissionsWithConditions(offset, prePage, search);
        List<PermissionResponse> responseList = new ArrayList<>();
        if (permissionList != null) {
            responseList = permissionList.stream().map(permission -> responseBuilder(permission)).toList();
        }
        
        return PaginationResponse.<PermissionResponse>builder()
                .page(page)
                .prePage(prePage)
                .data(responseList)
                .totalPage(totalPage)
                .totalRecord(totalRecords)
                .build();
    }

    public PermissionResponse getPermissionById(int id) {
        Permission permission= repository.findById(id).orElse(null);
        if(permission != null)
            return new PermissionResponse(permission.getPermissionId(), permission.getPermissionName(), permission.getDescription());
        else throw new BadRequestException(messageSourceHelper.getMessage("error.permissionNotFound"));
    }

    public PermissionResponse createPermission(PermissionRequest request) {
        Permission permission = new Permission();
        permission.setPermissionName(request.getName());
        permission.setDescription(request.getDescription());
        repository.save(permission);
        return new PermissionResponse(
                permission.getPermissionId(),
                request.getName(),
                request.getDescription()
        );
    }

    public PermissionResponse updatePermissionById(int id, PermissionRequest request) {
        Permission permission = repository.findById(id).orElse(null);
        if (permission != null) {
            if (!request.getName().isEmpty())
                permission.setPermissionName(request.getName());

            if (!request.getDescription().isEmpty())
                permission.setDescription(request.getDescription());

            repository.save(permission);
            return new PermissionResponse(
                    permission.getPermissionId(),
                    permission.getPermissionName(),
                    permission.getDescription()
            );
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.permissionNotFound"));
        }
    }

    public String deletePermissionById(int id) {
        Permission permission = repository.findById(id).orElse(null);
        if (permission != null) {
            repository.delete(permission);
            return "Deleted permission " + id + " successfully!";
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.permissionNotFound"));
        }
    }

    @Transactional(readOnly = true)
    public List<RoleResponse> getRoleListByPermissionId(int id) {
        Permission permission = repository.findById(id).orElse(null);
        if (permission != null) {
            List<RoleResponse> list = new ArrayList<>();
            permission.getRoles().forEach(role -> {
                list.add(new RoleResponse(
                        role.getRoleId(),
                        role.getRoleName().name(),
                        role.getDescription()
                ));
            });
            return list;
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.permissionNotFound"));
        }
    }

    private PermissionResponse responseBuilder(Permission permission) {
        return PermissionResponse.builder()
                .id(permission.getPermissionId())
                .name(permission.getPermissionName())
                .description(permission.getDescription())
                .build();
    }
}
