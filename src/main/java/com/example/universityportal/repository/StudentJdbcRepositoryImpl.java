package com.example.universityportal.repository;

import com.example.universityportal.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.*;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.*;

@Repository
public class StudentJdbcRepositoryImpl implements StudentJdbcRepository {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Student> rowMapper = (rs, rowNum) -> {
        Student s = new Student();
        s.setId(rs.getLong("id"));
        s.setFullName(rs.getString("full_name"));
        s.setGroupNumber(rs.getString("group_number"));
        s.setCourse(rs.getInt("course"));
        s.setPhotoPath(rs.getString("photo_path"));
        s.setUserId(rs.getLong("user_id"));
        return s;
    };

    @Override
    public void save(Student student) {
        Optional<Student> existing = findByUserId(student.getUserId());

        if (existing.isPresent()) {
            String sql = "UPDATE students SET full_name=?, group_number=?, course=?, photo_path=? WHERE user_id=?";
            jdbcTemplate.update(sql,
                    student.getFullName(),
                    student.getGroupNumber(),
                    student.getCourse(),
                    student.getPhotoPath(),
                    student.getUserId());
            student.setId(existing.get().getId());
        } else {
            String sql = "INSERT INTO students (full_name, group_number, course, photo_path, user_id) VALUES (?, ?, ?, ?, ?)";

            KeyHolder keyHolder = new GeneratedKeyHolder();
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, student.getFullName());
                ps.setString(2, student.getGroupNumber());
                ps.setInt(3, student.getCourse());
                ps.setString(4, student.getPhotoPath());
                ps.setLong(5, student.getUserId());
                return ps;
            }, keyHolder);

            Map<String, Object> keys = keyHolder.getKeys();
            if (keys != null && keys.containsKey("id")) {
                student.setId(((Number) keys.get("id")).longValue());
            }
        }
    }

    @Override
    public Optional<Student> findById(Long id) {
        String sql = "SELECT * FROM students WHERE id = ?";
        return jdbcTemplate.query(sql, rowMapper, id).stream().findFirst();
    }

    @Override
    public Optional<Student> findByUserId(Long userId) {
        String sql = "SELECT * FROM students WHERE user_id = ?";
        return jdbcTemplate.query(sql, rowMapper, userId).stream().findFirst();
    }

    @Override
    public List<Student> findAll() {
        String sql = "SELECT * FROM students";
        return jdbcTemplate.query(sql, rowMapper);
    }
    public void deleteById(Long id) {
        String sql = "DELETE FROM students WHERE id = ?";
        jdbcTemplate.update(sql, id);
    }
    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT COUNT(*) FROM students WHERE id = ?";
        Integer count = jdbcTemplate.queryForObject(sql, Integer.class, id);
        return count != null && count > 0;
    }
    @Override
    public void updateStudent(Student student) {
        String sql = """
        UPDATE students
        SET full_name = ?, group_number = ?, course = ?
        WHERE id = ?
    """;
        jdbcTemplate.update(sql,
                student.getFullName(),
                student.getGroupNumber(),
                student.getCourse(),
                student.getId()
        );
    }
}
