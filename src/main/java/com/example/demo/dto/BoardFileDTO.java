package com.example.demo.dto;

import com.example.demo.entity.BoardFileEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BoardFileDTO {
    private Long id;
    private String originalFileName;
    private String storedFileName;

    public static BoardFileDTO toBoardFileDTO(BoardFileEntity entity) {
        BoardFileDTO dto = new BoardFileDTO();
        dto.setId(entity.getId());
        dto.setOriginalFileName(entity.getOriginalFileName());
        dto.setStoredFileName(entity.getStoredFileName());
        return dto;
    }
}
