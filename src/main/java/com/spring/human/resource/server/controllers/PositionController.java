package com.spring.human.resource.server.controllers;


import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.payload.position.PositionRequest;
import com.spring.human.resource.server.payload.position.PositionResponse;
import com.spring.human.resource.server.services.PositionService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@Log4j2
@RequiredArgsConstructor
@RestController
@Tag(name = "position")
@RequestMapping("/positions")
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    @PreAuthorize("hasAuthority('READ_POSITIONS')")
    public PaginationResponse<PositionResponse> getAllPositionWithConditions(
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_SIZE) int prePage,
            @RequestParam(required = false, defaultValue = StringUtil.EMPTY) String search ) {
        return positionService.getPositionWithConditions(page, prePage, search);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('READ_POSITIONS')")
    public ApiResponse<PositionResponse> getPositionById(@PathVariable("id") int id) {
        PositionResponse response = positionService.getPositionById(id);
        return new ApiResponse<PositionResponse>(true, response);
    }

    @PostMapping
    @PreAuthorize("hasAuthority('WRITE_POSITIONS')")
    public ApiResponse<PositionResponse> createPosition(@RequestBody PositionRequest positionRequest) {
        PositionResponse response = positionService.createPosition(positionRequest);
        return new ApiResponse<PositionResponse>(true, response);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('WRITE_POSITIONS')")
    public ApiResponse<PositionResponse> updatePosition(@PathVariable("id") int id, @RequestBody PositionRequest positionRequest) throws Exception {
        PositionResponse response = positionService.updatePosition(id, positionRequest);
        return new ApiResponse<PositionResponse>(true, response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_POSITIONS')")
    public ApiResponse<String> deletePosition(@PathVariable("id") int id) throws Exception {
        positionService.deletePosition(id);
        return new ApiResponse<String>(true);
    }
}
