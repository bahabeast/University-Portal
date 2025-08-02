package com.example.universityportal.service;

import com.example.universityportal.model.Student;
import com.example.universityportal.repository.StudentJdbcRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private StudentJdbcRepository studentJdbcRepository;

    private static final String PHOTO_DIR = "photos";

    public void save(Student student, MultipartFile photoFile) throws IOException {
        student.setPhotoPath(null);
        studentJdbcRepository.save(student);

        if (photoFile != null && !photoFile.isEmpty()) {
            String extension = FilenameUtils.getExtension(photoFile.getOriginalFilename());
            String filename = student.getId() + "." + extension;
            Path photoPath = Paths.get(PHOTO_DIR, filename);

            Files.createDirectories(photoPath.getParent());
            Files.copy(photoFile.getInputStream(), photoPath, StandardCopyOption.REPLACE_EXISTING);

            student.setPhotoPath("/students/photo/" + student.getId());
            studentJdbcRepository.save(student);
        }
    }

    public Optional<Student> findById(Long id) {
        return studentJdbcRepository.findById(id);
    }

    public Optional<Student> findByUserId(Long userId) {
        return studentJdbcRepository.findAll().stream()
                .filter(s -> s.getUserId().equals(userId))
                .findFirst();
    }

    public List<Student> getAllStudents() {
        return studentJdbcRepository.findAll();
    }
}
