package com.example.demo.repository;

import com.example.demo.entity.FavoriteEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WalkCourseEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface FavoriteRepository extends JpaRepository<FavoriteEntity, Long> {

    Optional<FavoriteEntity> findByUserAndWalkCourse(UserEntity user, WalkCourseEntity walkCourse);

    List<FavoriteEntity> findByUser(UserEntity user);

    boolean existsByUserAndWalkCourse(UserEntity user, WalkCourseEntity walkCourse);

    Page<FavoriteEntity> findByUser(UserEntity user, Pageable pageable);
}
