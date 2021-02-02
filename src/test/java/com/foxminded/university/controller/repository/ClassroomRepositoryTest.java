package com.foxminded.university.controller.repository;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.test.context.jdbc.Sql;

import com.foxminded.university.model.Classroom;

@Sql(scripts = "classpath:testDatabase.sql")
@SpringBootTest
class ClassroomRepositoryTest {
    @Autowired
    ClassroomRepository classroomRepository;

    @Test
    void addShouldAddOneRowInClassroomTableTest() {
        Classroom classroom = new Classroom();
        classroom.setNumber("555");
        classroom.setCapacity(123);
        
        classroomRepository.add(classroom);
        
        int expected = 3;
        int actual = classroomRepository.getAll().size();
        assertEquals(expected, actual);
    }

    @Test
    void getAllShouldReturnArrayListOfClassroomsTest() {
        List<Classroom> actual = classroomRepository.getAll();
        assertEquals(2, actual.size());
        assertEquals(1, actual.get(0).getId());
    }

    @ParameterizedTest
    @CsvSource({"101A, 1, 30",
                "102A, 2, 300"})
    void getBynameShouldReturnClassroomObjectTest(String number, int expectedId, int expectedCapacity) {
        Classroom current = classroomRepository.getByNumber(number);
        assertEquals(expectedId, current.getId());
        assertEquals(expectedCapacity, current.getCapacity());
    }

    @ParameterizedTest
    @CsvSource({"1, 101A",
                "2, 102A"})
    void getByIdShouldReturnClassroomObjectTest(int id, String expectedNumber) {
        assertEquals(expectedNumber, classroomRepository.getById(id).getNumber());
    }

    @Test
    void getByNumberShouldThrouwExceptionForNonExistingDataTest() {
        Throwable thrown = assertThrows(EmptyResultDataAccessException.class, () -> {
            classroomRepository.getByNumber("zzzzz");
        });
    }
}
