package com.spring.human.resource.server.services;

import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.repository.BaseRepository;
import com.spring.human.lib.service.BaseService;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.resource.server.configs.language.MessageSourceHelper;
import com.spring.human.resource.server.entities.Position;
import com.spring.human.resource.server.payload.position.PositionRequest;
import com.spring.human.resource.server.payload.position.PositionResponse;
import com.spring.human.resource.server.repositories.PositionRepository;
import lombok.extern.log4j.Log4j2;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Log4j2
@Service
public class PositionService extends BaseService<Position, Integer> {
    // chỉ đọc (read - only) chỉ gán 1 lần thông qua constructor, không bị thay đổi ở nơi khác
    private final PositionRepository repository;
    private final MessageSourceHelper messageSourceHelper;

    // sẽ tự động inject repository
    // truy cập được trong cùng package, lớp con khác package chỉ được kế thừa
    protected PositionService(BaseRepository<Position, Integer> repository, MessageSourceHelper messageSourceHelper) {
        super(repository);
        this.repository = (PositionRepository) repository;
        this.messageSourceHelper = messageSourceHelper;
    }

    public PaginationResponse<PositionResponse> getPositionWithConditions(int page, int prePage, String search) {
        // tổng số bản ghi tìm kiếm
        long totalRecord = repository.countAllPositionWithConditions(search);
        // vị trí bắt đầu lấy dữ liệu trong SQL
        int offset = PagingUtil.getOffSet(page, prePage);
        // tổng số trang
        int totalPage = PagingUtil.getTotalPage(totalRecord, prePage);
        List<Position> positionList = repository.findAllPositionsWithConditions(offset, prePage, search);
        List<PositionResponse> responseList = new ArrayList<>();
        if (positionList != null) {
            responseList = positionList.stream().map(position -> responseBuilder(position)).toList();
        }

        return PaginationResponse.<PositionResponse>builder()
                .page(page)
                .prePage(prePage)
                .data(responseList)
                .totalPage(totalPage)
                .totalRecord(totalRecord)
                .build();
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
            log.error(messageSourceHelper.getMessage("error.positionNotFound", id));
            throw new BadRequestException(messageSourceHelper.getMessage("error.positionNotFound", id));
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
