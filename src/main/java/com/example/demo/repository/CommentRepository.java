package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import com.example.demo.entity.CommentEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<CommentEntity, Long> {
    // select * from comment where board_id=? order by id desc;
    List<CommentEntity> findAllByBoardEntityOrderByIdDesc(BoardEntity boardEntity);

    Page<CommentEntity> findAllByCommentWriter(String writer, Pageable pageable);
}
