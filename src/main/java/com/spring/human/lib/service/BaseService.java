package com.spring.human.lib.service;

import com.spring.human.lib.repository.BaseRepository;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

// generic service
public class BaseService<T, ID> {
    // chỉ đọc (read - only) chỉ gán 1 lần thông qua constructor, không bị thay đổi ở nơi khác
    // truy cập được trong cùng package, lớp con khác package chỉ kế thừa
    protected final BaseRepository<T, ID> repository;

    protected BaseService(BaseRepository<T, ID> repository) {
        this.repository = repository;
    }

    public T save(T entity) {
        return repository.save(entity);
    }

    /*
        findOne(Specification<T> spec) nhận vào một Specification để build truy vấn
        Kết hợp tất cả các Predicate bằng toán tử AND
        toArray(new Predicate[0]): convert List<Predicate> sang Predicate[]
     */
    public T findByFields(Map<String, Object> fields) {
        return repository.findOne(
                (root, query, criteriaBuilder)
                        -> criteriaBuilder.and(buildPredicates(fields, criteriaBuilder, root)
                        .toArray(new Predicate[0]))).orElse(null);
    }

    /*
        func buildPredicates xây dựng list các condition dựa trên input
        Predicate trong JPA Criteria API là 1 condition (điều kiện) xây dựng câu lệnh WHERE trong try vấn SQL
        fields: các trường và giá trị cần lọc
        criteriaBuilder: là object hỗ trợ xây dựng các thành phần trong JPA Criteria API
            ví dụ: criteriaBuilder.equal(root.get("age"), 20) sẽ tạo điều kiện age = 20
        root: đại diện cho entity đang query
    */
    private List<Predicate> buildPredicates(Map<String, Object> fields, CriteriaBuilder criteriaBuilder, Root<T> root) {
        List<Predicate> predicates = new ArrayList<>();
        fields.forEach((field, value) -> predicates.add(criteriaBuilder.equal(root.get(field), value)));
        return predicates;
    }
}
