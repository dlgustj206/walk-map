package com.example.demo.service;

import com.example.demo.entity.WalkCourseEntity;
import com.example.demo.entity.WalkCoursePathEntity;
import com.example.demo.repository.WalkCourseRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class WalkCourseCsvService {

    private final WalkCourseRepository walkCourseRepository;

    public void importCsv(InputStream inputStream) throws IOException {
        if (walkCourseRepository.count() > 0) {
            System.out.println("이미 코드가 존재합니다.");
            return;
        }

        Map<String, List<String[]>> grouped = new LinkedHashMap<>();

        try (BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
            String header = br.readLine();
            String line;
            while ((line = br.readLine()) != null) {
                String[] row = line.split(",");
                String courseName = row[8];
                grouped.computeIfAbsent(courseName, k -> new ArrayList<>()).add(row);
            }
        }

        for (Map.Entry<String, List<String[]>> entry : grouped.entrySet()) {
            List<String[]> rows = entry.getValue();
            String[] first = rows.get(0);

            WalkCourseEntity course = WalkCourseEntity.builder()
                    .name(first[8])
                    .city(first[1])
                    .district(first[2])
                    .neighborhood(first[3])
                    .distanceKm(Double.parseDouble(first[4].toLowerCase().replace("km", "").trim()))
                    .duration(first[5])
                    .level(Integer.parseInt(first[6]))
                    .favorite(Integer.parseInt(first[7]))
                    .build();

            List<WalkCoursePathEntity> paths = new ArrayList<>();
            for (String[] row : rows) {
                WalkCoursePathEntity path = WalkCoursePathEntity.builder()
                        .pointOrder(Integer.parseInt(row[9]))        // 경로 순서
                        .latitude(Double.parseDouble(row[11]))       // 위도
                        .longitude(Double.parseDouble(row[10]))      // 경도
                        .district(row[2])                            // 행정구
                        .neighborhood(row[3])                        // 행정동
                        .course(course)
                        .build();
                paths.add(path);
            }

            course.setPaths(paths);
            walkCourseRepository.save(course);
        }
    }
}
