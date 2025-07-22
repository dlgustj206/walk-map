package com.example.demo.service;

import com.example.demo.dto.BoardDTO;
import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.BoardFileEntity;
import com.example.demo.repository.BoardFileRepository;
import com.example.demo.repository.BoardRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BoardService {

    private final BoardRepository boardRepository;
    private final BoardFileRepository boardFileRepository;

    public void save(BoardDTO boardDTO) throws IOException {
        List<MultipartFile> fileList = boardDTO.getBoardFile();

        boolean hasRealFile = fileList != null &&
                fileList.stream().anyMatch(file -> !file.isEmpty());

        // 파일 첨부 여부에 따라 로직 분리
        if (!hasRealFile) {
            BoardEntity boardEntity = BoardEntity.toSaveEntity(boardDTO);
            boardRepository.save(boardEntity); // DTO -> Entity
        } else {
            /*
                1. DTO에 담긴 파일을 꺼냄
                2. 파일의 이름 가져옴
                3. 서버 저장용 이름을 만듦(내사진.jpg -> 84593840598_내사진.jpg)
                4. 저장 경로 설정
                5. 해당 경로에 파일 저장
                6. 게시글 정보는 board에 데이터 save 처리
                7. 파일 정보는 board_file에 데이터 save 처리
             */
            BoardEntity boardEntity = BoardEntity.toSaveFileEntity(boardDTO);
            Long savedId = boardRepository.save(boardEntity).getId(); // 자식 테이블에서는 부모가 어떤 번호인지에 대한 정보가 필요하기 때문에 부모 게시글에 대한 pk 값이 필요함
            BoardEntity board = boardRepository.findById(savedId).get(); // 부모 엔티티가 전달되어야 하기 때문에 부모 엔티티를 DB로부터 가져옴
            for (MultipartFile boardFile : boardDTO.getBoardFile()) {
//              MultipartFile boardFile = boardDTO.getBoardFile(); // 반복문을 이용하여 파일을 꺼내고 있기 때문에 필요 없음
                String originalFilename = boardFile.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "D:/board_img/" + storedFileName; // D:/board_img/84593840598_내사진.jpg
                boardFile.transferTo(new File(savePath));
                BoardFileEntity boardFileEntity = BoardFileEntity.toBoardFileEntity(board, originalFilename, storedFileName); // 부모 엔티티와 파일 정보를 사용해 BoardFileEntity 객체 생성
                boardFileRepository.save(boardFileEntity); // boardFileEntity 객체를 board_file에 저장
            }
        }
    }

    @Transactional
    public List<BoardDTO> findAll() {
        List<BoardEntity> boardEntityList = boardRepository.findAll();
        List<BoardDTO> boardDTOList = new ArrayList<>(); // boardEntityList에 담긴 데이터를 여기로 옮겨담음(Entity -> DTO)

        for (BoardEntity boardEntity: boardEntityList) {
            boardDTOList.add(BoardDTO.toBoardDTO(boardEntity));
        }
        return boardDTOList;
    }

    @Transactional
    public void updateHits(Long id) {
        boardRepository.updateHits(id);
    }

    @Transactional
    public BoardDTO findById(Long id) {
        Optional<BoardEntity> optionalBoardEntity = boardRepository.findById(id);
        if (optionalBoardEntity.isPresent()) {
            return BoardDTO.toBoardDTO(optionalBoardEntity.get());
        } else {
            return null;
        }
    }

    @Transactional
    public BoardDTO update(BoardDTO boardDTO, List<Long> deleteFileIds) throws IOException {
        Optional<BoardEntity> optionalBoard = boardRepository.findById(boardDTO.getId());
        if (optionalBoard.isEmpty()) {
            throw new IllegalArgumentException("해당 게시글이 존재하지 않습니다.");
        }

        BoardEntity boardEntity = optionalBoard.get();
        boardEntity.setBoardTitle(boardDTO.getBoardTitle());
        boardEntity.setBoardContents(boardDTO.getBoardContents());
        boardEntity.setBoardHits(boardDTO.getBoardHits());

        // 삭제 요청된 파일들 제거
        if (deleteFileIds != null && !deleteFileIds.isEmpty()) {
            for (Long fileId : deleteFileIds) {
                boardFileRepository.deleteById(fileId);
            }
        }

        // 새 파일 저장
        List<MultipartFile> newFiles = boardDTO.getBoardFile();
        boolean hasNewFiles = newFiles != null && newFiles.stream().anyMatch(file -> !file.isEmpty());

        if (hasNewFiles) {
            for (MultipartFile file : newFiles) {
                String originalFilename = file.getOriginalFilename();
                String storedFileName = System.currentTimeMillis() + "_" + originalFilename;
                String savePath = "D:/board_img/" + storedFileName;
                file.transferTo(new File(savePath));
                BoardFileEntity fileEntity = BoardFileEntity.toBoardFileEntity(boardEntity, originalFilename, storedFileName);
                boardFileRepository.save(fileEntity);
            }
        }

        // 파일 존재 여부 다시 체크해서 설정
        int remainingFileCount = boardFileRepository.findAll().stream()
                .filter(f -> f.getBoardEntity().getId().equals(boardEntity.getId())).toList().size();
        boardEntity.setFileAttached(remainingFileCount > 0 ? 1 : 0);

        boardRepository.save(boardEntity);
        return findById(boardDTO.getId());
    }

    public void delete(Long id) {
        boardRepository.deleteById(id);
    }

    public Page<BoardDTO> paging(Pageable pageable) {
        int page = pageable.getPageNumber() - 1; // page 위치에 있는 값은 0부터 시작하기 때문에 1을 빼줌
        int pageLimit = 7; // 한 페이지에 보여줄 글의 개수
        // 한 페이지당 7개씩 글을 보여주고, 정렬 기준은 id 기준으로 내림차순 정렬
        Page<BoardEntity> boardEntities = boardRepository.findAllWithFile(
                PageRequest.of(page, pageLimit, Sort.by(Sort.Direction.DESC, "id")));

        System.out.println("boardEntities.getContent() = " + boardEntities.getContent()); // 요청 페이지에 해당하는 글
        System.out.println("boardEntities.getTotalElements() = " + boardEntities.getTotalElements()); // 전체 글 개수
        System.out.println("boardEntities.getNumber() = " + boardEntities.getNumber()); // DB로 요청한 페이지 번호
        System.out.println("boardEntities.getTotalPages() = " + boardEntities.getTotalPages()); // 전체 페이지 개수
        System.out.println("boardEntities.getSize() = " + boardEntities.getSize()); // 한 페이지에 보여지는 글 개수
        System.out.println("boardEntities.hasPrevious() = " + boardEntities.hasPrevious()); // 이전 페이지 존재 여부
        System.out.println("boardEntities.isFirst() = " + boardEntities.isFirst()); // 첫 페이지 여부
        System.out.println("boardEntities.isLast() = " + boardEntities.isLast()); // 마지막 페이지 여부

        // 목록: id, writer, title, hits, createdTime
        Page<BoardDTO> boardDTOS = boardEntities.map(BoardDTO::toBoardDTO);
        return boardDTOS;
    }

    public Page<BoardEntity> getMyPosts(String nickname, Pageable pageable) {
        return boardRepository.findAllByBoardWriter(nickname, pageable);
    }
}