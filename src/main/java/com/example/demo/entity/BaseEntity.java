package com.example.demo.entity;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@MappedSuperclass
@EntityListeners({AuditingEntityListener.class})
@Getter
public class BaseEntity { // 시간 정보를 다루는 클래스

    @CreationTimestamp // 생성된 시간
    @Column(updatable = false) // 수정 X
    private LocalDateTime createdTime;

    @UpdateTimestamp // 업데이트된 시간
    @Column(insertable = false) // 입력 X
    private LocalDateTime updatedTime;
}