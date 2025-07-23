package com.example.demo.controller;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.CommentEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.entity.WalkCourseEntity;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import com.example.demo.service.FavoriteService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequiredArgsConstructor
@RequestMapping("/mypage")
public class MyPageController {

    private final FavoriteService favoriteService;
    private final BoardService boardService;
    private final CommentService commentService;

    @GetMapping
    public String showMyPage(@AuthenticationPrincipal UserEntity user,
                             @RequestParam(defaultValue = "0", name = "favoritePage") int favoritePage,
                             @RequestParam(defaultValue = "0", name = "postPage") int postPage,
                             @RequestParam(defaultValue = "0", name = "commentPage") int commentPage,
                             Model model) {

        model.addAttribute("email", user.getEmail());
        model.addAttribute("nickname", user.getNickname());

        // 각 섹션별 Pageable
        Pageable favoritePageable = PageRequest.of(favoritePage, 5);
        Pageable postPageable = PageRequest.of(postPage, 5);
        Pageable commentPageable = PageRequest.of(commentPage, 5);

        // 찜한 산책 코스
        Page<WalkCourseEntity> favoriteCourses = favoriteService.getMyFavorites(user, favoritePageable);
        model.addAttribute("favorites", favoriteCourses);

        // 내가 작성한 게시글
        Page<BoardEntity> myPosts = boardService.getMyPosts(user.getNickname(), postPageable);
        model.addAttribute("myPosts", myPosts);

        // 내가 작성한 댓글
        Page<CommentEntity> myComments = commentService.getMyComments(user.getNickname(), commentPageable);
        model.addAttribute("myComments", myComments);

        return "mypage/mypage.html";
    }
}

