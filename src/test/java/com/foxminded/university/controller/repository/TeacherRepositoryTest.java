package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Teacher;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class TeacherRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    TeacherRepository teacherRepository;

    @Test
    void addShoulCreateNewRowInTeachersTableTest() {
        Teacher teacher = new Teacher();
        teacher.setFirstName("testTeacher");
        teacher.setLastName("testTeacher2");
        
        Faculty faculty = new Faculty();
        faculty.setId(1);
        
        teacher.setFaculty(faculty);
        teacherRepository.add(teacher);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM teachers", Integer.class);
        assertEquals(2, actual);
    }

    @ParameterizedTest
    @CsvSource({"1, 1", 
                "100, 0", 
                "5, 0"})
    void getByFacultyShouldReturnListOfTeachersTest(int id, int expectedSize) {
        Faculty faculty = new Faculty();
        faculty.setId(id);
        List<Teacher> actual = teacherRepository.getByFaculty(faculty);
        assertEquals(expectedSize, actual.size());
    }

    @Test
    @Transactional
    void getByIdShouldReturnTeacherObjectTest() {
        Teacher teacher = teacherRepository.getById(1);
        assertEquals("Alan", teacher.getFirstName());
        assertEquals("Turing", teacher.getLastName());
        assertEquals("Turing machine", teacher.getCourses().get(0).getName());
        assertEquals("CS", teacher.getFaculty().getShortName());
    }

    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldReturnNullForNonExistingDataTest(int id) {
        assertNull(teacherRepository.getById(id));
    }
}
