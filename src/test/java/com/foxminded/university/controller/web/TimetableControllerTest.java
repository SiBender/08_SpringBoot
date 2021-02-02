package com.foxminded.university.controller.web;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import java.time.LocalDate;
import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.foxminded.university.controller.service.LessonService;
import com.foxminded.university.controller.service.TimetableService;
import com.foxminded.university.controller.util.TimetableFormatter;
import com.foxminded.university.model.DateInterval;
import com.foxminded.university.model.Lesson;
import com.foxminded.university.model.Timeslot;
import com.foxminded.university.model.Timetable;

@WebMvcTest(controllers = TimetableController.class)
class TimetableControllerTest {
    
    @Autowired
    private MockMvc mockMvc;

    @Mock
    Model model;
    
    @MockBean
    TimetableService timetableService;

    @MockBean
    LessonService lessonService;
    
    @MockBean
    TimetableFormatter timetableFormatter;
    
    @Autowired
    TimetableController timetableController;

    @Test
    void timetableInfoTest() throws Exception {
        Timetable table = new Timetable();
        table.setDateInterval(new DateInterval(LocalDate.now(), LocalDate.now()));
        Mockito.when(timetableService.getStudentTimetable(anyString(), anyString(), anyInt()))
               .thenReturn(table);
        Mockito.when(lessonService.getAllTimeslots()).thenReturn(Arrays.asList(new Timeslot()));
        
        this.mockMvc.perform(get("/timetable"))
                    .andExpect(view().name("timetable"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("studentId"))
                    .andExpect(model().attributeExists("timetable"))
                    .andExpect(model().attributeExists("timeslots"))
                    .andExpect(model().attributeExists("timemap"));

        verify(timetableService).getStudentTimetable(anyString(), anyString(), anyInt());
        verify(lessonService).getAllTimeslots();
        verify(timetableFormatter).generateFormattedTable(any(Timetable.class), anyList());
    }

    @Test
    void getTimetableByTeacherTest() throws Exception {
        DateInterval dateInterval = new DateInterval(LocalDate.now(), LocalDate.now());
        Timetable timetable = new Timetable();
        timetable.setDateInterval(dateInterval);
        
        Mockito.when(timetableService.getTeacherTimetable(anyString(), anyString(), anyInt()))
               .thenReturn(timetable);
        Mockito.when(lessonService.getAllTimeslots()).thenReturn(Arrays.asList(new Timeslot()));
        
        this.mockMvc.perform(get("/teacherstimetable"))
                    .andExpect(view().name("teacherstimetable"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("teacherId"))
                    .andExpect(model().attributeExists("dateInterval"))
                    .andExpect(model().attributeExists("timeslots"))
                    .andExpect(model().attributeExists("timemap"));

        verify(timetableService).getTeacherTimetable(anyString(), anyString(), anyInt());
        verify(lessonService).getAllTimeslots();
        verify(timetableFormatter).generateFormattedTable(any(Timetable.class), anyList());
    }

    @Test
    void createNewLessonTest() throws Exception {
        this.mockMvc.perform(get("/timetable/addlesson")
                .param("date", "2020-06-18")
                .param("tid", "1")
                .param("timeslot", "1"))
                .andExpect(view().name("timetable/addlesson"))
                .andExpect(status().isOk())
                .andExpect(model().size(5))
                .andExpect(model().attribute("timeslotId", Matchers.equalTo(1)));
        
        verify(lessonService).getAllClassrooms();
        verify(lessonService).getAllTimeslots();
        verify(lessonService).getCoursesByTeacher(1);
    }

    @Test
    void saveNewLessonTest() throws Exception {
        this.mockMvc.perform(post("/timetable/addlesson")
                .param("date", "2020-06-20")
                .param("time.id", "1")
                .param("course.id", "1")
                .param("classroom.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacherstimetable"));

        verify(lessonService).createLesson(anyString(), anyInt(), anyInt(), anyInt());
    }

    @Test
    void getLessonInfoTest() throws Exception {
        Lesson mockLesson = new Lesson();
        mockLesson.setId(1);
        Mockito.when(lessonService.getLessonById(anyInt())).thenReturn(mockLesson);
        
        this.mockMvc.perform(get("/timetable/editlesson")
                .param("id", "1")
                .param("tid", "1"))
                .andExpect(view().name("timetable/editlesson"))
                .andExpect(status().isOk())
                .andExpect(model().size(4))
                .andExpect(model().attribute("lesson", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))));

        verify(lessonService).getLessonById(1);
        verify(lessonService).getAllClassrooms();
        verify(lessonService).getAllTimeslots();
        verify(lessonService).getCoursesByTeacher(1);
    }

    @Test
    void updateLessonTest() throws Exception {
        this.mockMvc.perform(post("/timetable/editlesson")
                    .param("id", "1")
                    .param("date", "2020-06-20")
                    .param("time.id", "3")
                    .param("course.id", "1")
                    .param("classroom.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacherstimetable"));

        verify(lessonService).updateLesson(any(Lesson.class));
    }

    @Test
    void deleteLessonTest() throws Exception {
        this.mockMvc.perform(get("/deletelesson")
                .param("id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacherstimetable"));

        verify(lessonService).deleteLesson(1);
    }
}
