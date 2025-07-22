package com.example.demo.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "walk_course_path")
public class WalkCoursePathEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int pointOrder;
    private Double latitude;
    private Double longitude;
    private String district;    // 행정구
    private String neighborhood; // 행정동

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private WalkCourseEntity course;
}
