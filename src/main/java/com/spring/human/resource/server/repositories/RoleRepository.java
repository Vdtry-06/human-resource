package com.spring.human.resource.server.repositories;

import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.resource.server.entities.Role;
import com.spring.human.resource.server.payload.rolepermission.RolePermissionResponse;
import com.spring.human.resource.server.services.RolePermissionService;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends BaseRepository<Role, Integer> {
    @Query(value = "SELECT COUNT(*) FROM public.roles r "
            + "WHERE ( :search IS NULL OR :search = '' OR r.role_name ILIKE CONCAT('%', :search, '%')) ", nativeQuery = true)
    long countAllRoleWithConditions(@Param("search") String search);

    @Query(value = "SELECT r.role_id, r.role_name FROM public.roles r "
            + "WHERE ( :search IS NULL OR :search = '' OR r.role_name ILIKE CONCAT('%', :search, '%')) "
            + "ORDER BY r.role_name ASC "
            + "LIMIT :limit OFFSET :offset ", nativeQuery = true)
    List<Role> findAllRoleWithConditions(@Param("offset") int offset,
                                                  @Param("limit") int limit,
                                                  @Param("search") String search);

    @Query("SELECT new com.spring.human.resource.server.payload.rolepermission.RolePermissionResponse(r.roleName, p.permissionName) " +
            "FROM Role r JOIN r.permissions p")
    List<RolePermissionService> findAllRolePermission();

    @Query("SELECT new com.spring.human.resource.server.payload.rolepermission.RolePermissionResponse(r.roleName, p.permissionName) " +
            "FROM Role r JOIN r.permissions p " +
            "WHERE r.roleId = :roleId AND p.permissionId = :permissionId")
    Optional<RolePermissionResponse> findRolePermission(@Param("roleId") int roleId,
                                                        @Param("permissionId") int permissionId);
}
