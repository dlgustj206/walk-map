package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "walk_course")
public class WalkCourseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;        // 코스명
    private String city;        // 행정시
    private String district;    // 행정구
    private String neighborhood; // 행정동
    private Double distanceKm;  // 거리 (km)
    private String duration;    // 소요시간
    private int level;       // 난이도 (초급 / 중급 / 고급)
    private int favorite;       // 찜 개수

    @Builder.Default
    @OneToMany(mappedBy = "course", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<WalkCoursePathEntity> paths = new ArrayList<>();

    @OneToMany(mappedBy = "walkCourse", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FavoriteEntity> favorites = new ArrayList<>();
}
