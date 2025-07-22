package com.example.demo.repository;

import com.example.demo.entity.WalkCourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface WalkCourseRepository extends JpaRepository<WalkCourseEntity, Long> {

    // 기본 (가나다순)
    Page<WalkCourseEntity> findAllByOrderByNameAsc(Pageable pageable);

    // 거리순
    Page<WalkCourseEntity> findAllByOrderByDistanceKmAsc(Pageable pageable);

    // 소요시간순 정렬
    Page<WalkCourseEntity> findAllByOrderByDurationAsc(Pageable pageable);

    // 찜 많은순 (찜 개수가 동일하면 가나다순 정렬)
    @Query("SELECT wc FROM WalkCourseEntity wc LEFT JOIN wc.favorites f GROUP BY wc.id ORDER BY COUNT(f) DESC, wc.name ASC")
    Page<WalkCourseEntity> findAllByOrderByFavoriteCountDescNameAsc(Pageable pageable);

    // 난이도순 (초급/중급/고급)
    Page<WalkCourseEntity> findByLevelOrderByNameAsc(int level, Pageable pageable);

    // 난이도 + 거리순
    Page<WalkCourseEntity> findByLevelOrderByDistanceKmAscNameAsc(int level, Pageable pageable);

    // 난이도 + 소요시간
    Page<WalkCourseEntity> findByLevelOrderByDurationAsc(int level, Pageable pageable);

    // 난이도 + 찜순
    @Query("SELECT wc FROM WalkCourseEntity wc LEFT JOIN wc.favorites f WHERE wc.level = :level GROUP BY wc.id ORDER BY COUNT(f) DESC, wc.name ASC")
    Page<WalkCourseEntity> findByLevelOrderByFavoriteCountDescNameAsc(int level, Pageable pageable);

    // ID 리스트 기반 가나다순
    Page<WalkCourseEntity> findByIdInOrderByNameAsc(List<Long> ids, Pageable pageable);

    // ID 리스트 기반 거리순
    Page<WalkCourseEntity> findByIdInOrderByDistanceKmAsc(List<Long> ids, Pageable pageable);

    // ID 리스트 기반 소요시간순
    Page<WalkCourseEntity> findByIdInOrderByDurationAsc(List<Long> ids, Pageable pageable);

    // ID 리스트 기반 찜 많은순
    @Query("""
        SELECT wc
        FROM WalkCourseEntity wc
        LEFT JOIN wc.favorites f
        WHERE wc.id IN :ids
        GROUP BY wc.id
        ORDER BY COUNT(f) DESC, wc.name ASC
    """)
    Page<WalkCourseEntity> findByIdInOrderByFavoriteCountDescNameAsc(List<Long> ids, Pageable pageable);

    // ID + level + 가나다순
    Page<WalkCourseEntity> findByLevelAndIdInOrderByNameAsc(int level, List<Long> ids, Pageable pageable);

    // ID + level + 거리순
    Page<WalkCourseEntity> findByLevelAndIdInOrderByDistanceKmAscNameAsc(int level, List<Long> ids, Pageable pageable);

    // ID + level + 소요시간순
    Page<WalkCourseEntity> findByLevelAndIdInOrderByDurationAsc(int level, List<Long> ids, Pageable pageable);

    // ID + level + 찜 많은순
    @Query("""
        SELECT wc
        FROM WalkCourseEntity wc
        LEFT JOIN wc.favorites f
        WHERE wc.level = :level AND wc.id IN :ids
        GROUP BY wc.id
        ORDER BY COUNT(f) DESC, wc.name ASC
    """)
    Page<WalkCourseEntity> findByLevelAndIdInOrderByFavoriteCountDescNameAsc(int level, List<Long> ids, Pageable pageable);
}
