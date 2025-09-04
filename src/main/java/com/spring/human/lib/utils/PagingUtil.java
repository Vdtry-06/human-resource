package com.spring.human.lib.utils;

// Phân trang
public class PagingUtil {
    public static final String DEFAULT_PAGE = "1";
    public static final String DEFAULT_SIZE = "10";
    public static final int DEFAULT_SIZE_INT = Integer.parseInt(DEFAULT_SIZE);

    // Constructor
    private PagingUtil() {}

    // vị trí bắt đầu lấy dữ liệu trong SQL
    // perSize: per page size: là số bản ghi trên mỗi trang
    public static Integer getOffSet(Integer page, Integer perSize) {
        return (page - 1) * (perSize == null ? DEFAULT_SIZE_INT : perSize);
    }

    // tổng số trang
    // perSize: per page size: là số bản ghi trên mỗi trang
    public static Integer getTotalPage(Long totalRecord, Integer perSize) {
        return (int) Math.ceil((double) totalRecord / (perSize == null ? DEFAULT_SIZE_INT : perSize));
    }
}
