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

import com.foxminded.university.model.Course;
import com.foxminded.university.model.Teacher;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class CourseRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    CourseRepository courseRepository;
    
    @Test
    void addShouldCreateNewRowInCoursesTableTest() {
        Course course = new Course();
        course.setName("testCourse");
        course.setDescription("testDescr");
        Teacher teacher = new Teacher();
        teacher.setId(1);
        course.setTeacher(teacher);
        
        courseRepository.add(course);
        int expected = 4;
        int current = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM COURSES", Integer.class);
        assertEquals(expected, current);
    }
    
    @Test
    void getByTeacherShouldReturnListOfCoursesTest() {
        Teacher teacher = new Teacher();
        teacher.setId(1);
        List<Course> actual = courseRepository.getByTeacher(teacher);
        assertEquals(3, actual.size());
        assertEquals("Turing machine", actual.get(0).getName());
    }
    
    @Test
    void getByTeacherShouldReturnEmptyListForNonExistingDataTest() {
        Teacher teacher = new Teacher();
        teacher.setId(100);
        List<Course> actual = courseRepository.getByTeacher(teacher);
        assertTrue(actual.isEmpty());
    }

    @ParameterizedTest
    @CsvSource({"1, 'Turing machine', Alan",
                "2, 'Turing-complete languages', Alan",})
    @Transactional(readOnly=true)
    void getByIdShouldReturnCourseObjectTest(int id, String courseName, String teacherName) {
        Course current = courseRepository.getById(id);
        assertEquals(courseName, current.getName());
        assertEquals(teacherName, current.getTeacher().getFirstName());
    }
}
