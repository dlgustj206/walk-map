package com.example.demo.controller;

import com.example.demo.dto.BoardDTO;
import com.example.demo.dto.CommentDTO;
import com.example.demo.entity.BoardFileEntity;
import com.example.demo.entity.UserEntity;
import com.example.demo.repository.BoardFileRepository;
import com.example.demo.service.BoardService;
import com.example.demo.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.View;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequiredArgsConstructor
@RequestMapping("/board")
public class BoardController {

    private final BoardService boardService; // 생성자 주입 방식으로 의존성 주입
    private final CommentService commentService;
    private final BoardFileRepository boardFileRepository;

    @GetMapping("/save")
    public String saveForm() {
        return "board/save";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute BoardDTO boardDTO,
                       @AuthenticationPrincipal UserEntity user) throws IOException { // DTO 객체를 통해 입력값을 가져옴
        boardDTO.setBoardWriter(user.getNickname()); // 닉네임 자동 설정
        boardService.save(boardDTO);
        return "redirect:/board/paging";
    }

    /**
     * 해당 게시글의 조회수를 하나 올리고
     * 게시글 데이터를 가져와서 detail.html에 출력해야 하므로
     * 컨트롤러에서 두 가지의 메서드를 호출하도록 구현
     */
    @GetMapping("/{id}")
    public String findById(@PathVariable Long id, Model model,
                           @PageableDefault(page=1) Pageable pageable,
                           @AuthenticationPrincipal UserEntity loginUser) { // 경로 상에 있는 값을 가져올 때는 @PathVariable 사용
        boardService.updateHits(id);
        BoardDTO boardDTO = boardService.findById(id);
        List<CommentDTO> commentDTOList = commentService.findAll(id);

        model.addAttribute("commentList", commentDTOList);
        model.addAttribute("board", boardDTO);
        model.addAttribute("page", pageable.getPageNumber());

        if (loginUser != null) {
            model.addAttribute("loginUserNickname", loginUser.getNickname());
        }

        return "board/detail";
    }

    @GetMapping("/update/{id}")
    public String updateForm(@PathVariable Long id, Model model) {
        BoardDTO boardDTO = boardService.findById(id);
        model.addAttribute("boardUpdate", boardDTO);
        return "board/update";
    }

    @PostMapping("/update")
    @ResponseBody
    public String update(@ModelAttribute BoardDTO boardDTO,
                         @RequestParam(value = "deleteFileIds", required = false) String deleteFileIds,
                         @AuthenticationPrincipal UserEntity user) throws IOException {

        boardDTO.setBoardWriter(user.getNickname());

        List<Long> deleteIds = new ArrayList<>();
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            for (String idStr : deleteFileIds.split(",")) {
                deleteIds.add(Long.parseLong(idStr));
            }
        }

        boardService.update(boardDTO, deleteIds);

        return "/board/" + boardDTO.getId();
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable Long id) {
        boardService.delete(id);
        return "redirect:/board/paging";
    }

    @GetMapping("/paging")
    public String paging(@PageableDefault(page=1) Pageable pageable, Model model) {
//      pageable.getPageNumber();
        Page<BoardDTO> boardList = boardService.paging(pageable);
        int blockLimit = 7; // 보여지는 페이지 개수를 7개로 제한
        int startPage = (((int)(Math.ceil((double)pageable.getPageNumber() / blockLimit))) - 1) * blockLimit + 1;
        int endPage = ((startPage + blockLimit - 1) < boardList.getTotalPages()) ? startPage + blockLimit - 1 : boardList.getTotalPages();

        model.addAttribute("boardList", boardList);
        model.addAttribute("startPage", startPage);
        model.addAttribute("endPage", endPage);
        return "board/paging";
    }

    @GetMapping("/download")
    public ResponseEntity<Resource> downloadFile(@RequestParam Long fileId) throws Exception {
        // BoardFileEntity를 DB에서 조회
        BoardFileEntity boardFileEntity = boardFileRepository.findById(fileId)
                .orElseThrow(() -> new FileNotFoundException("파일 정보를 찾을 수 없습니다."));

        String storedFileName = boardFileEntity.getStoredFileName();
        String originalFileName = boardFileEntity.getOriginalFileName();

        // 로컬 저장 경로와 결합
        String uploadPath = "D:/board_img/";
        Path path = Paths.get(uploadPath + storedFileName);
        Resource resource = new UrlResource(path.toUri());

        if (!resource.exists()) {
            throw new FileNotFoundException("저장 경로에 파일이 없습니다.");
        }

        // 한글, 공백 깨짐 방지
        String encodedFileName = URLEncoder.encode(originalFileName, StandardCharsets.UTF_8)
                .replaceAll("\\+", "%20");

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION,
                        "attachment; filename=\"" + encodedFileName + "\"")
                .body(resource);
    }
}
