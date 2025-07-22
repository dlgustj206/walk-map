package com.example.demo.repository;

import com.example.demo.entity.WalkCoursePathEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface WalkCoursePathRepository extends JpaRepository<WalkCoursePathEntity, Long> {

    // 입력한 district 또는 neighborhood가 포함된 코스 ID 목록 반환 (중복 제거)
    @Query("""
        SELECT DISTINCT p.course.id
        FROM WalkCoursePathEntity p
        WHERE (:keyword IS NULL OR p.district LIKE %:keyword% OR p.neighborhood LIKE %:keyword%)
    """)
    List<Long> findDistinctCourseIdsByKeyword(String keyword);
}
