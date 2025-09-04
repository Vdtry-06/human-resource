package com.spring.human.resource.server.repositories;

import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.resource.server.entities.Position;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PositionRepository extends BaseRepository<Position, Integer> {
    // truy vấn bảng positions trong schema public
    // nếu search = NULL or '' => đk trả về true : bỏ qua tìm kiếm
    // nếu search có giá trị khác => sử dụng ILIKE
    /*
        p.position_name ILIKE CONCAT('%', :search, '%')): tìm kiếm bất kì theo
    */
    @Query(value = "SELECT COUNT(*) FROM public.positions p "
            + "WHERE ( :search IS NULL OR :search = '' OR p.position_name ILIKE CONCAT('%', :search, '%')) ", nativeQuery = true)
    long countAllPositionWithConditions(@Param("search") String search);

    @Query(value = "SELECT p.position_id, p.position_name FROM public.positions p "
            + "WHERE ( :search IS NULL OR :search = '' OR p.position_name ILIKE CONCAT('%', :search, '%')) "
            + "ORDER BY p.position_id ASC "
            + "LIMIT :limit OFFSET :offset ", nativeQuery = true)
    List<Position> findAllPositionsWithConditions(@Param("offset") int offset,
                                                  @Param("limit") int limit,
                                                  @Param("search") String search);
}
