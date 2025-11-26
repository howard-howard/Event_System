package com.example.demo.Student;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Async
public class StudentCSVHelper {

    public static String TYPE = "text/csv";

    public static boolean hasCSVFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType()) || file.getOriginalFilename().endsWith(".csv");
    }

    public static List<Student> csvToStudents(MultipartFile file) {

        CSVFormat format = CSVFormat.Builder.create()
                .setHeader()
                .setSkipHeaderRecord(true)
                .setIgnoreEmptyLines(true)
                .setIgnoreSurroundingSpaces(true)
                .setIgnoreHeaderCase(true)
                .build();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {

            reader.mark(1);
            if (reader.read() != 0xFEFF) {
                reader.reset();
            }

            CSVParser csvParser = new CSVParser(reader, format);

            List<Student> students = new ArrayList<>();

            for (CSVRecord record : csvParser) {

                Student s = new Student(
                        record.get("name"),
                        record.get("tel_no"),
                        record.get("email"),
                        record.get("password")
                );

                students.add(s);
            }

            return students;

        } catch (IOException e) {
            throw new RuntimeException("Failed to parse CSV: " + e.getMessage());
        }
    }



}
