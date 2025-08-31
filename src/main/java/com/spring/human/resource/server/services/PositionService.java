package com.spring.human.resource.server.services;

import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.resource.server.entities.Position;
import com.spring.human.resource.server.payload.position.PositionRequest;
import com.spring.human.resource.server.payload.position.PositionResponse;
import com.spring.human.resource.server.repositories.PositionRepository;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Map;

@Service
public class PositionService extends BaseService<Position, Integer> {
    // chỉ đọc (read - only) chỉ gán 1 lần thông qua constructor, không bị thay đổi ở nơi khác
    private final PositionRepository repository;

    // sẽ tự động inject repository
    // truy cập được trong cùng package, lớp con khác package chỉ được kế thừa
    protected PositionService(BaseRepository<Position, Integer> repository) {
        super(repository);
        this.repository = (PositionRepository) repository;
    }

    // Chỉ lấy dữ liệu ra khi đọc đúng
    // Tối ưu cho việc query dữ liệu, không ghi xuống DB
    @Transactional(readOnly = true)
    public PositionResponse getPositionById(int id) {
        Position position = findByFields(Map.of("positionId", id));
        if (position == null) return new PositionResponse();
        return responseBuilder(position);
    }

    // Nếu bất kỳ Exception nào (checked hoặc unchecked) xảy ra, transaction cũng sẽ rollback.
    // Không chỉ RuntimeException mà kể cả Exception thông thường cũng được rollback.
    @Transactional(rollbackFor = Exception.class)
    public PositionResponse createPosition(PositionRequest request) {
        Position position = entityBuilder(request);
        position = save(position);
        return responseBuilder(position);
    }

    // Nếu bất kỳ Exception nào (checked hoặc unchecked) xảy ra, transaction cũng sẽ rollback.
    // Không chỉ RuntimeException mà kể cả Exception thông thường cũng được rollback.
    @Transactional(rollbackFor = Exception.class)
    public PositionResponse updatePosition(Integer id, PositionRequest request) throws Exception {
        Position position = findByFields(Map.of("positionId", id));
        position.setPositionName(request.getPositionName());
        position = save(position);
        return responseBuilder(position);
    }

    // Thông báo rằng có thể xảy ra 1 ngoại lệ
    public void deletePosition(int id) throws Exception {
        try {
            Position position = findByFields(Map.of("positionId", id));
            repository.delete(position);
        } catch (Exception e) {
            // Tự định nghĩa lỗi
            throw new BadRequestException("Position not found");
        }
    }

    private Position entityBuilder(PositionRequest request) {
        return Position.builder()
                .positionName(request.getPositionName())
                .build();
    }

    private PositionResponse responseBuilder(Position position) {
        return PositionResponse.builder()
                .positionId(position.getPositionId())
                .positionName(position.getPositionName())
                .build();
    }
}
