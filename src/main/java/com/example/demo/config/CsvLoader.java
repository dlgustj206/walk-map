package com.example.demo.config;

import com.example.demo.repository.WalkCourseRepository;
import com.example.demo.service.WalkCourseCsvService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileInputStream;

@Component
@RequiredArgsConstructor
public class CsvLoader implements CommandLineRunner {

    private final WalkCourseCsvService walkCourseCsvService;
    private final WalkCourseRepository walkCourseRepository;

    @Override
    public void run(String... args) throws Exception {
        if (walkCourseRepository.count() > 0) {
            return;
        }

        File file = new File("src/main/resources/walkcourses.csv");

        if (!file.exists()) {
            System.out.println("CSV 파일이 존재하지 않습니다.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(file)) {
            walkCourseCsvService.importCsv(fis);
        }

        System.out.println("*** CSV 데이터 삽입 완료 ***");
    }
}