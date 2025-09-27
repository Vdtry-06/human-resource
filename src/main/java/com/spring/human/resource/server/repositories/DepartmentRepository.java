package com.spring.human.resource.server.repositories;

import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.resource.server.entities.Department;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DepartmentRepository extends BaseRepository<Department, Integer> {
    @Query(value = "SELECT COUNT(*) FROM public.departments d "
            + "WHERE ( :search IS NULL OR :search = '' OR d.department_name ILIKE CONCAT('%', :search, '%')) ", nativeQuery = true)
    long countAllDepartmentWithConditions(@Param("search") String search);

    @Query(value = "SELECT d.department_id, d.department_name, d.description FROM public.departments d "
            + "WHERE ( :search IS NULL OR :search = '' OR d.department_name ILIKE CONCAT('%', :search, '%')) "
            + "ORDER BY d.department_id ASC "
            + "LIMIT :limit OFFSET :offset ", nativeQuery = true)
    List<Department> findAllDepartmentWithConditions(@Param("offset") int offset,
                                                     @Param("limit") int limit,
                                                     @Param("search") String search);
}