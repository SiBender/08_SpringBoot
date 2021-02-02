package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Group;
import com.foxminded.university.model.Student;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class StudentRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    StudentRepository studentRepository;

    @Test
    void addShoulCreateNewRowInStudentsTableTest() {
        Group group = new Group();
        group.setId(1);
        
        Student student = new Student();
        student.setFirstName("testFirstName");
        student.setLastName("testLastname");
        student.setGroup(group);
        studentRepository.add(student);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM students", Integer.class);
        assertEquals(3, actual);
    }

    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldReturnNullForNonExistingDataTest(int id) {
        assertNull(studentRepository.getById(id));
    }

    @Test
    @Transactional
    void getByIdShouldReturnStudentObjectTest() {
        Student actual = studentRepository.getById(1);
        assertEquals("John", actual.getFirstName());
        assertEquals("Smith", actual.getLastName());
        assertEquals("cs-20", actual.getGroup().getGroupName());
    }
}
