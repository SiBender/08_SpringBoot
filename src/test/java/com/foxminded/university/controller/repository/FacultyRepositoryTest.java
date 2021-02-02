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

import com.foxminded.university.model.Faculty;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class FacultyRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    FacultyRepository facultyRepository;

    @Test
    void addShouldCreateNewRowInFacultiesTableTest() {
        Faculty faculty = new Faculty();
        faculty.setShortName("GF");
        faculty.setFullName("Griffindor");
        facultyRepository.add(faculty);
        
        int actual = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM faculties", Integer.class);
        assertEquals(3, actual);
    }

    @Test
    void getAllShouldReturnListOfFacultiesTest() {
        List<Faculty> actulaFaculties = facultyRepository.getAll();
        assertEquals(2, actulaFaculties.size());
        assertEquals("CS", actulaFaculties.get(0).getShortName());
    }

    @ParameterizedTest
    @CsvSource({"1, 'CS', 'Computer Science'",
                "2, 'BA', 'Ballet Art'",})
    void getByIdShouldReturnFacultyObjectTest(int id, String shortName, String fullname) {
        Faculty faculty = facultyRepository.getById(id);
        assertEquals(shortName, faculty.getShortName());
        assertEquals(fullname, faculty.getFullName());
    }
}
