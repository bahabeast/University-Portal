package com.example.universityportal.controller;

import com.example.universityportal.model.Student;
import com.example.universityportal.repository.StudentJdbcRepository;
import com.example.universityportal.repository.UserJdbcRepository;
import jakarta.annotation.security.RolesAllowed;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@RestController
@RequestMapping("/students")
public class StudentController {

    private final StudentJdbcRepository studentRepository;
    private final UserJdbcRepository userRepository;
    @Value("${upload.dir}")
    private String uploadDir;

    public StudentController(StudentJdbcRepository studentRepository, UserJdbcRepository userRepository) {
        this.studentRepository = studentRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    @RolesAllowed("ROLE_ADMIN")
    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    @PostMapping("/{id}/photo")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_STUDENT"})
    public ResponseEntity<String> uploadPhoto(@PathVariable Long id,
                                              @RequestParam("file") MultipartFile file) {
        try {
            Optional<Student> studentOpt = studentRepository.findById(id);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Student not found");
            }

            Files.createDirectories(Paths.get(uploadDir));
            String filename = id + ".jpg";
            Path filepath = Paths.get(uploadDir, filename);
            Files.write(filepath, file.getBytes());

            Student student = studentOpt.get();
            student.setPhotoPath(filename);
            studentRepository.save(student);

            return ResponseEntity.ok("Photo uploaded successfully");
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error saving file: " + e.getMessage());
        }
    }

    @GetMapping("/photo/{id}")
    public ResponseEntity<Resource> getPhoto(@PathVariable Long id) throws IOException {
        Path photoDir = Paths.get(uploadDir);
        Path path = photoDir.resolve(id + ".jpg");

        if (Files.exists(path)) {
            Resource resource = new UrlResource(path.toUri());
            return ResponseEntity.ok()
                    .contentType(MediaType.IMAGE_JPEG)
                    .body(resource);
        }
        return ResponseEntity.notFound().build();
    }

    @GetMapping("/{id}")
    @RolesAllowed({"ROLE_ADMIN", "ROLE_STUDENT"})
    public ResponseEntity<Student> getStudentById(@PathVariable Long id) {
        return studentRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<?> updateStudent(@PathVariable Long id, @RequestBody Student student) {
        if (!studentRepository.existsById(id)) {
            return ResponseEntity.notFound().build();
        }

        student.setId(id);
        studentRepository.updateStudent(student);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{id}")
    @RolesAllowed("ROLE_ADMIN")
    public ResponseEntity<Void> deleteStudent(@PathVariable Long id) {
        Optional<Student> studentOpt = studentRepository.findById(id);

        if (studentOpt.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Student student = studentOpt.get();

        try {
            Path photoPath = Paths.get(uploadDir).resolve(id + ".jpg");
            if (Files.exists(photoPath)) {
                Files.delete(photoPath);
                System.out.println("Deleted photo: " + photoPath.toAbsolutePath());
            } else {
                System.out.println("Photo file not found: " + photoPath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.err.println("Failed to delete photo: " + e.getMessage());
        }

        // ✅ Delete from database
        Long userId = student.getUserId();
        studentRepository.deleteById(id);
        userRepository.deleteById(userId);

        return ResponseEntity.noContent().build();
    }



}
