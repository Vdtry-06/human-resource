package com.spring.human.resource.server.controllers;


import com.spring.human.lib.api.ApiResponse;
import com.spring.human.lib.api.PaginationResponse;
import com.spring.human.lib.utils.PagingUtil;
import com.spring.human.lib.utils.StringUtil;
import com.spring.human.resource.server.payload.position.PositionRequest;
import com.spring.human.resource.server.payload.position.PositionResponse;
import com.spring.human.resource.server.services.PositionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RequiredArgsConstructor
@RestController
@RequestMapping("/positions")
public class PositionController {
    private final PositionService positionService;

    @GetMapping
    public PaginationResponse<PositionResponse> getAllPositionWithConditions(
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_PAGE) int page,
            @RequestParam(required = false, defaultValue = PagingUtil.DEFAULT_SIZE) int perpage,
            @RequestParam(required = false, defaultValue = StringUtil.EMPTY) String search ) {
        return positionService.getPositionWithConditions(page, perpage, search);
    }

    @GetMapping("/{id}")
    public ApiResponse<PositionResponse> getPositionById(@PathVariable("id") int id) {
        PositionResponse response = positionService.getPositionById(id);
        return new ApiResponse<PositionResponse>(true, response);
    }

    @PostMapping
    public ApiResponse<PositionResponse> createPosition(@RequestBody PositionRequest positionRequest) {
        PositionResponse response = positionService.createPosition(positionRequest);
        return new ApiResponse<PositionResponse>(true, response);
    }

    @PutMapping("/{id}")
    public ApiResponse<PositionResponse> updatePosition(@PathVariable("id") int id, @RequestBody PositionRequest positionRequest) throws Exception {
        PositionResponse response = positionService.updatePosition(id, positionRequest);
        return new ApiResponse<PositionResponse>(true, response);
    }

    @DeleteMapping("/{id}")
    public ApiResponse<String> deletePosition(@PathVariable("id") int id) throws Exception {
        positionService.deletePosition(id);
        return new ApiResponse<String>(true);
    }
}
