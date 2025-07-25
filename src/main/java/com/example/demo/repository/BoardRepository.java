package com.example.demo.repository;

import com.example.demo.entity.BoardEntity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface BoardRepository extends JpaRepository<BoardEntity, Long> {
    @Modifying
    @Query(value = "update BoardEntity b set b.boardHits=b.boardHits+1 where b.id=:id") // Entity 기준으로 쿼리 작성
    void updateHits(@Param("id") Long id);

    @Query("SELECT DISTINCT b FROM BoardEntity b LEFT JOIN FETCH b.boardFileEntityList")
    Page<BoardEntity> findAllWithFile(Pageable pageable);

    Page<BoardEntity> findAllByBoardWriter(String writer, Pageable pageable);
}
