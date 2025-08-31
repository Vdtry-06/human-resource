package com.spring.human.resource.server.repositories;

import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.resource.server.entities.Position;
import org.springframework.stereotype.Repository;

@Repository
public interface PositionRepository extends BaseRepository<Position, Integer> {

}
