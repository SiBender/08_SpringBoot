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

import com.foxminded.university.model.Timeslot;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class TimeslotRepositoryTest {
    @Autowired
    JdbcTemplate jdbcTemplate;
    
    @Autowired
    TimeslotRepository timeslotRepository;

    @Test
    void getAllShouldReturnListOfTimeslotsTest() {
        List<Timeslot> actual = timeslotRepository.getAll();
        assertEquals(6, actual.size());
    }

    @ParameterizedTest
    @CsvSource({"1, '09:00 - 10:30'", 
                "2, '10:30 - 12:00'", 
                "3, '12:30 - 14:00'"})
    void getByIdShouldReturnTimeslotObjectTest(int id, String expectedDescription) {
        Timeslot timeslot = timeslotRepository.getById(id);
        assertEquals(expectedDescription, timeslot.getDescription());
    }

    @ParameterizedTest
    @CsvSource({"100", "200", "-1000"})
    void getByIdShouldReturnNullForNonExistingDataTest(int id) {
        assertNull(timeslotRepository.getById(id));
    }
}
