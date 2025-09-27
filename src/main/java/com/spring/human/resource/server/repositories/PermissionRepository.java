package com.spring.human.resource.server.repositories;

import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.resource.server.entities.Permission;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PermissionRepository extends BaseRepository<Permission, Integer> {
    @Query(value = "SELECT COUNT(*) FROM public.permissions p "
            + "WHERE ( :search IS NULL OR :search = '' OR p.permission_name ILIKE CONCAT('%', :search, '%')) ", nativeQuery = true)
    long countAllPositionWithConditions(@Param("search") String search);

    @Query(value = "SELECT p.permission_id, p.permission_name, p.description FROM public.permissions p "
            + "WHERE ( :search IS NULL OR :search = '' OR p.permission_name ILIKE CONCAT('%', :search, '%')) "
            + "ORDER BY p.permission_id ASC "
            + "LIMIT :limit OFFSET :offset ", nativeQuery = true)
    List<Permission> findAllPermissionsWithConditions(@Param("offset") int offset,
                                                      @Param("limit") int perpage,
                                                      @Param("search") String search);

}
