package com.example.demo.service;

import com.example.demo.dto.WalkCourseDetailDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WalkCourseEntity;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.WalkCoursePathRepository;
import com.example.demo.repository.WalkCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class WalkCourseService {

    private final WalkCourseRepository walkCourseRepository;
    private final WalkCoursePathRepository walkCoursePathRepository;
    private final FavoriteRepository favoriteRepository;

    public WalkCourseDetailDTO getCourseDetailDto(Long id, UserEntity user) {
        WalkCourseEntity course = walkCourseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("코스 정보 없음"));

        boolean isFavorite = false;
        if (user != null) {
            isFavorite = favoriteRepository.existsByUserAndWalkCourse(user, course);
        }

        return WalkCourseDetailDTO.fromEntity(course, isFavorite);
    }

    public Page<WalkCourseEntity> getCoursesPagedSorted(String sort, int page, Integer level, String keyword) {
        Pageable pageable = PageRequest.of(page, 20);

        // keyword가 있는 경우에만 ID 필터링을 적용
        List<Long> filteredCourseIds = null;
        boolean useFilter = false;

        if (keyword != null && !keyword.isBlank()) {
            filteredCourseIds = walkCoursePathRepository.findDistinctCourseIdsByKeyword(keyword);
            if (filteredCourseIds.isEmpty()) {
                return Page.empty(pageable);
            }
            useFilter = true;
        }

        // 정렬 + 난이도 + ID 조합
        if (level != null) {
            if (useFilter) {
                // level + keyword 있음
                return switch (sort) {
                    case "distance" -> walkCourseRepository.findByLevelAndIdInOrderByDistanceKmAscNameAsc(level, filteredCourseIds, pageable);
                    case "duration" -> walkCourseRepository.findByLevelAndIdInOrderByDurationAsc(level, filteredCourseIds, pageable);
                    case "favorite" -> walkCourseRepository.findByLevelAndIdInOrderByFavoriteCountDescNameAsc(level, filteredCourseIds, pageable);
                    default -> walkCourseRepository.findByLevelAndIdInOrderByNameAsc(level, filteredCourseIds, pageable);
                };
            } else {
                // level만 있음
                return switch (sort) {
                    case "distance" -> walkCourseRepository.findByLevelOrderByDistanceKmAscNameAsc(level, pageable);
                    case "duration" -> walkCourseRepository.findByLevelOrderByDurationAsc(level, pageable);
                    case "favorite" -> walkCourseRepository.findByLevelOrderByFavoriteCountDescNameAsc(level, pageable);
                    default -> walkCourseRepository.findByLevelOrderByNameAsc(level, pageable);
                };
            }
        } else {
            if (useFilter) {
                // keyword만 있음
                return switch (sort) {
                    case "distance" -> walkCourseRepository.findByIdInOrderByDistanceKmAsc(filteredCourseIds, pageable);
                    case "duration" -> walkCourseRepository.findByIdInOrderByDurationAsc(filteredCourseIds, pageable);
                    case "favorite" -> walkCourseRepository.findByIdInOrderByFavoriteCountDescNameAsc(filteredCourseIds, pageable);
                    default -> walkCourseRepository.findByIdInOrderByNameAsc(filteredCourseIds, pageable);
                };
            } else {
                // level도 없고 keyword도 없음 → 전체 조회
                return switch (sort) {
                    case "distance" -> walkCourseRepository.findAllByOrderByDistanceKmAsc(pageable);
                    case "duration" -> walkCourseRepository.findAllByOrderByDurationAsc(pageable);
                    case "favorite" -> walkCourseRepository.findAllByOrderByFavoriteCountDescNameAsc(pageable);
                    default -> walkCourseRepository.findAllByOrderByNameAsc(pageable);
                };
            }
        }
    }
}

