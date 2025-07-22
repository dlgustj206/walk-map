package com.example.demo.controller;

import com.example.demo.dto.WalkCourseDetailDTO;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WalkCourseEntity;
import com.example.demo.service.FavoriteService;
import com.example.demo.service.WalkCourseService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/courses")
public class WalkCourseController {

    private final WalkCourseService walkCourseService;
    private final FavoriteService favoriteService;

    // 코스 리스트 페이지
    @GetMapping
    public String list(@RequestParam(defaultValue = "default") String sort,
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) Integer level,
                       @RequestParam(required = false) String keyword,
                       Model model) {

        Page<WalkCourseEntity> coursePage = walkCourseService.getCoursesPagedSorted(sort, page, level, keyword);


        model.addAttribute("courses", coursePage.getContent());
        model.addAttribute("page", coursePage);
        model.addAttribute("sort", sort);
        model.addAttribute("level", level);
        model.addAttribute("keyword", keyword);

        return "course/list";
    }

    // 코스 상세 페이지
    @GetMapping("/{id}")
    public String detail(@PathVariable Long id, Model model,
                         @AuthenticationPrincipal UserEntity user) {
        WalkCourseDetailDTO course = walkCourseService.getCourseDetailDto(id, user); // DTO 반환
        model.addAttribute("course", course);
        return "course/detail";
    }

    // 즐겨찾기 추가
    @PostMapping("/{id}/favorite")
    public String addFavorite(@PathVariable Long id,
                              @AuthenticationPrincipal UserEntity user,
                              RedirectAttributes redirectAttributes) {
        favoriteService.addFavorite(user, id);
        redirectAttributes.addFlashAttribute("message", "즐겨찾기 완료");
        return "redirect:/courses/" + id;
    }

    // 즐겨찾기 해제
    @PostMapping("/{id}/unfavorite")
    public String removeFavorite(@PathVariable Long id,
                                 @AuthenticationPrincipal UserEntity user,
                                 RedirectAttributes redirectAttributes) {
        favoriteService.removeFavorite(user, id);
        redirectAttributes.addFlashAttribute("message", "즐겨찾기 해제");
        return "redirect:/courses/" + id;
    }
}
