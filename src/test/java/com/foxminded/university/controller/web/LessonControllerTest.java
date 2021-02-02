package com.foxminded.university.controller.web;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;

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
import com.foxminded.university.model.Classroom;
import com.foxminded.university.model.Timeslot;

@WebMvcTest(controllers = LessonController.class)
class LessonControllerTest { 
    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    Model model;
    
    @MockBean
    LessonService lessonService;
    
    @Autowired
    LessonController lessonController;
    
    @Test
    void lessonsInfoTest() throws Exception {
        this.mockMvc.perform(get("/lesson"))
                    .andExpect(view().name("lesson"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("classrooms"))
                    .andExpect(model().attributeExists("classroom"))
                    .andExpect(model().attributeExists("timeslots"))
                    .andExpect(model().attributeExists("timeslot"));
        
        verify(lessonService).getAllClassrooms();
        verify(lessonService).getAllTimeslots();
    }

    @Test
    void addClassroomTest() throws Exception {
        this.mockMvc.perform(post("/addclassroom")
                    .param("number", "555AAA")
                    .param("capacity", "1000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        verify(lessonService).addClassroom(any(Classroom.class));
    }

    @Test
    void deleteClassroomTest() throws Exception {
        this.mockMvc.perform(get("/deleteclassroom")
                    .param("id", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"))
                    .andExpect(model().size(0));
        
        verify(lessonService).deleteClassroom(3);
    }

    @Test
    void getClassroomTest() throws Exception {
        Classroom mockClassroom = new Classroom();
        mockClassroom.setId(1);
        mockClassroom.setNumber("101A");
        Mockito.when(lessonService.getClassroomById(anyInt())).thenReturn(mockClassroom);
        
        this.mockMvc.perform(get("/lesson/editclassroom")
                    .param("id", "1"))
                    .andExpect(view().name("lesson/editclassroom"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(1))
                    .andExpect(model().attribute("classroom", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))))
                    .andExpect(model().attribute("classroom", 
                            Matchers.hasProperty("number", Matchers.equalTo("101A"))));

        verify(lessonService).getClassroomById(1);
    }

    @Test
    void updateClassroomTest() throws Exception {
        this.mockMvc.perform(post("/lesson/editclassroom")
                    .param("id", "1")
                    .param("number", "555AAA")
                    .param("capacity", "1000"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        verify(lessonService).updateClassroom(any(Classroom.class));
    }

    @Test
    void addTimeslotTest() throws Exception {
        this.mockMvc.perform(post("/addtimeslot")
                    .param("description", "01:00 - 02:00"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        verify(lessonService).addTimeslot(any(Timeslot.class));
    }

    @Test
    void getTimeslotTest() throws Exception {
        Timeslot mockTimeslot = new Timeslot();
        mockTimeslot.setId(1);
        Mockito.when(lessonService.getTimeslotById(anyInt())).thenReturn(mockTimeslot);
        
        this.mockMvc.perform(get("/lesson/edittimeslot")
                    .param("id", "1"))
                    .andExpect(view().name("lesson/edittimeslot"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(1))
                    .andExpect(model().attribute("timeslot", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))));

        verify(lessonService).getTimeslotById(1);
    }

    @Test
    void updateTimeslotTest() throws Exception {
        this.mockMvc.perform(post("/lesson/editclassroom")
                    .param("id", "1")
                    .param("description", "01:00 - 02:00"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"));
        
        lessonController.updateTimeslot(new Timeslot(), model);
        verify(lessonService).updateTimeslot(any(Timeslot.class));
    }

    @Test
    void deleteTimeslotTest() throws Exception {
        this.mockMvc.perform(get("/deletetimeslot")
                    .param("id", "10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/lesson"))
                    .andExpect(model().size(0));
        
        verify(lessonService).deleteTimeslot(10);
    }
}
