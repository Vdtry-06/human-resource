package com.spring.human.resource.server.services;

import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.enumerated.SystemRole;
import com.spring.human.lib.exceptions.BadRequestException;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.entities.Role;
import com.spring.human.resource.server.payload.role.RoleRequest;
import com.spring.human.resource.server.payload.role.RoleResponse;
import com.spring.human.resource.server.repositories.RoleRepository;
import org.springdoc.core.service.GenericResponseService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@Transactional
public class RoleService extends BaseService<Role, Integer> {
    private final RoleRepository repository;
    private final MessageSourceHelper messageSourceHelper;

    protected RoleService(BaseRepository<Role, Integer> repository, MessageSourceHelper messageSourceHelper) {
        super(repository);
        this.repository = (RoleRepository) repository;
        this.messageSourceHelper = messageSourceHelper;
    }


    public PaginationResponse<RoleResponse> getAllRolesWithConditions(int page, int prePage, String search) {
        long totalRecord = repository.countAllRoleWithConditions(search);
        int offset = PagingUtil.getOffSet(page, prePage);
        int totalPage = PagingUtil.getTotalPage(totalRecord, prePage);
        List<Role> roleList = repository.findAllRoleWithConditions(offset, totalPage, search);
        List<RoleResponse> responseList = new ArrayList<>();
        if (roleList != null) {
            responseList = roleList.stream().map(role -> responseBuilder(role)).toList();
            /*
                for (Role role : roleList) {
                    responseList.add(
                            RoleResponse.builder()
                                    .id(role.getRoleId())
                                    .name(role.getRoleName().name())
                                    .description(role.getDescription())
                                    .build()
                    );
                }
            */
        }

        return PaginationResponse.<RoleResponse>builder()
                .page(page)
                .prePage(prePage)
                .data(responseList)
                .totalPage(totalPage)
                .totalRecord(totalRecord)
                .build();
    }

    @Transactional(readOnly = true)
    @SuppressWarnings("null")
    public List<RoleResponse> findAll(){
        List<Role> roleList= repository.findAll();
        List<RoleResponse> roleResponses = new ArrayList<>();
        roleList.forEach(r -> roleResponses.add(new RoleResponse(r.getRoleId(),r.getRoleName().toString(), r.getDescription())));
        return roleResponses;
    }

    @Transactional(readOnly = true)
    public RoleResponse getRoleById(int id) {
        Role role= repository.findById(id).orElse(null);
        if(role!= null) {
            return new RoleResponse(role.getRoleId(),role.getRoleName().toString(), role.getDescription());
        }
        else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.roleNotFound", id));
        }
    }

    public RoleResponse createRole(RoleRequest request) {
        Role role = new Role();
        role.setRoleName(SystemRole.fromStringToEnum(request.getName()));
        role.setDescription(request.getDescription());
        repository.save(role);
        return new RoleResponse(
                role.getRoleId(),
                request.getName(),
                request.getDescription()
        );
    }

    public RoleResponse updateRoleById(Integer id, RoleRequest request) {
        Role role = repository.findById(id).orElse(null);
        if (role != null) {
            if (!request.getName().isEmpty())
                role.setRoleName(SystemRole.fromStringToEnum(request.getName()));

            if (!request.getDescription().isEmpty())
                role.setDescription(request.getDescription());

            repository.save(role);
            return new RoleResponse(
                    role.getRoleId(),
                    role.getRoleName().name(),
                    role.getDescription()
            );
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.roleNotFound", id));
        }
    }

    public String deleteRoleById(Integer id) {
        Role role = repository.findById(id).orElse(null);
        if (role != null) {
            repository.delete(role);
            return "Deleted role " + id + " " + " successfully!";
        } else {
            throw new BadRequestException(messageSourceHelper.getMessage("error.roleNotFound", id));
        }
    }

    private RoleResponse responseBuilder(Role role) {
        return RoleResponse.builder()
                .id(role.getRoleId())
                .name(role.getRoleName().toString())
                .description(role.getDescription())
                .build();
    }
}
