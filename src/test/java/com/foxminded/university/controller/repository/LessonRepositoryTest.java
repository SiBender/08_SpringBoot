package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.jdbc.Sql;

import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Timeslot;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class LessonRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    LessonRepository lessonRepository;

    @Test
    void addShouldCreateNewRowInLessonsTable() {      
        LocalDate date = LocalDate.now();
        Timeslot time = new Timeslot();
        time.setId(1);
        Course course = new Course();
        course.setId(1);
        Classroom classroom = new Classroom();
        classroom.setId(1);
        
        Lesson lesson = new Lesson();
        lesson.setDate(date);
        lesson.setTime(time);
        lesson.setCourse(course);
        lesson.setClassroom(classroom);
        lessonRepository.add(lesson);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM lessons", Integer.class);
        assertEquals(3, actual);
    }

    @ParameterizedTest
    @CsvSource({"100, 1, 1",
                "1, 100, 1",
                "1, 1, 100"})
    void addShouldThrouwExceptionForIncorrectDataTest(int timeslotId, int courseId, int classroomId) {
        LocalDate date = LocalDate.now();
        Timeslot time = new Timeslot();
        time.setId(timeslotId);
        Course course = new Course();
        course.setId(courseId);
        Classroom classroom = new Classroom();
        classroom.setId(classroomId);
        
        Lesson lesson = new Lesson();
        lesson.setDate(date);
        lesson.setTime(time);
        lesson.setCourse(course);
        lesson.setClassroom(classroom);
        
        Throwable thrown = assertThrows(DataIntegrityViolationException.class, () -> {
            lessonRepository.add(lesson);
        });        
    }
}
