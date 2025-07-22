package com.example.demo.dto;

import com.example.demo.entity.WalkCourseEntity;
import com.example.demo.entity.WalkCoursePathEntity;
import lombok.*;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WalkCourseDetailDTO {

    private Long id;
    private String name;
    private double distanceKm;
    private String duration;
    private int level;
    private int favoriteCount; // 찜 수
    private boolean favorite;  // 찜 여부
    private List<PathDTO> paths;

    @Getter
    @Setter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PathDTO {
        private double latitude;
        private double longitude;
    }

    public static WalkCourseDetailDTO fromEntity(WalkCourseEntity entity, boolean favorite) {
        List<WalkCoursePathEntity> sortedPaths = entity.getPaths().stream()
                .sorted(Comparator.comparingDouble(WalkCoursePathEntity::getLatitude).reversed()) // 북→남 정렬
                .collect(Collectors.toList());

        List<PathDTO> selectedPaths = List.of(
                PathDTO.builder()
                        .latitude(sortedPaths.get(0).getLatitude())
                        .longitude(sortedPaths.get(0).getLongitude())
                        .build(),
                PathDTO.builder()
                        .latitude(sortedPaths.get(sortedPaths.size() - 1).getLatitude())
                        .longitude(sortedPaths.get(sortedPaths.size() - 1).getLongitude())
                        .build()
        );

        return WalkCourseDetailDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .distanceKm(entity.getDistanceKm())
                .duration(entity.getDuration())
                .level(entity.getLevel())
                .favoriteCount(entity.getFavorite())
                .favorite(favorite)
                .paths(selectedPaths)
                .build();
    }
}
