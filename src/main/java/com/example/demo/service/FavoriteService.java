package com.example.demo.service;

import com.example.demo.entity.FavoriteEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WalkCourseEntity;
import com.example.demo.repository.FavoriteRepository;
import com.example.demo.repository.WalkCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FavoriteService {

    private final FavoriteRepository favoriteRepository;
    private final WalkCourseRepository walkCourseRepository;

    public void addFavorite(UserEntity user, Long courseId) {
        WalkCourseEntity course = walkCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("코스 없음"));

        if (favoriteRepository.existsByUserAndWalkCourse(user, course)) return;

        FavoriteEntity favorite = new FavoriteEntity();
        favorite.setUser(user);
        favorite.setWalkCourse(course);
        favoriteRepository.save(favorite);

        // 찜 수 증가
        course.setFavorite(course.getFavorite() + 1);
        walkCourseRepository.save(course);
    }

    public void removeFavorite(UserEntity user, Long courseId) {
        WalkCourseEntity course = walkCourseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("코스 없음"));

        FavoriteEntity fav = favoriteRepository.findByUserAndWalkCourse(user, course)
                .orElseThrow(() -> new RuntimeException("즐겨찾기 안 되어 있음"));

        favoriteRepository.delete(fav);

        // 찜 수 감소
        course.setFavorite(Math.max(0, course.getFavorite() - 1));
        walkCourseRepository.save(course);
    }

    public List<WalkCourseEntity> getMyFavorites(UserEntity user) {
        return favoriteRepository.findByUser(user).stream()
                .map(FavoriteEntity::getWalkCourse)
                .toList();
    }

    public Page<WalkCourseEntity> getMyFavorites(UserEntity user, Pageable pageable) {
        return favoriteRepository.findByUser(user, pageable)
                .map(FavoriteEntity::getWalkCourse);
    }
}
