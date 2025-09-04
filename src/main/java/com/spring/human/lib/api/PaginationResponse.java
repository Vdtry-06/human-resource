package com.spring.human.lib.api;

import lombok.*;

import java.io.Serializable;
import java.util.List;


// Generic và chuyển đối tượng thành chuỗi byte
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PaginationResponse<T> implements Serializable {
    private static final long serialVersionUID = 1L;

    private int page;
    private int prePage;
    private List<T> data;
    private long totalRecord;
    private long totalPage;
}
