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
import static org.mockito.ArgumentMatchers.anyString;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.foxminded.university.controller.service.StudentsService;
import com.foxminded.university.model.Student;

@WebMvcTest(controllers = StudentsController.class)
class StudentsControllerTest {
    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    Model model;
    
    @MockBean
    StudentsService studentsService;
    
    @Autowired
    StudentsController studentsController;
    
    @Order(0)
    @Test
    void studentsInfoTest() throws Exception {
        this.mockMvc.perform(get("/student"))
                    .andExpect(view().name("student"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(4))
                    .andExpect(model().attributeExists("groups"))
                    .andExpect(model().attributeExists("student"))
                    .andExpect(model().attributeExists("groupsCount"))
                    .andExpect(model().attributeExists("studentsCount"));
        
        verify(studentsService).getAllGroups();
    }

    @Order(3)
    @Test
    void addStudentTest() throws Exception {
        this.mockMvc.perform(post("/addstudent")
                    .param("lastName", "Smith")
                    .param("firstName", "John")
                    .param("group.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/student"));
        
        verify(studentsService).addStudent(anyString(), anyString(), anyInt());
    }

    @Order(0)
    @Test
    void getStudentTest() throws Exception {
        Student mockStudent = new Student();
        mockStudent.setId(1);
        mockStudent.setFirstName("John");
        Mockito.when(studentsService.getStudent(anyInt())).thenReturn(mockStudent);
        
        this.mockMvc.perform(get("/student/editstudent")
                    .param("id", "1"))
                    .andExpect(view().name("student/editstudent"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(2))
                    .andExpect(model().attribute("student", 
                            Matchers.hasProperty("id", Matchers.equalTo(1))))
                    .andExpect(model().attribute("student", 
                            Matchers.hasProperty("firstName", Matchers.equalTo("John"))));

        verify(studentsService).getStudent(1);
    }

    @Order(5)
    @Test
    void updateStudentTest() throws Exception {
        this.mockMvc.perform(post("/student/editstudent")
                    .param("id", "1")
                    .param("firsName", "New-John")
                    .param("lastName", "New-Smith")
                    .param("group.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/student"));
        
        verify(studentsService).update(any(Student.class));
    }

    @Order(6)
    @Test
    void deleteStudentTest() throws Exception {
        this.mockMvc.perform(get("/deletestudent")
                    .param("id", "10"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/student"))
                    .andExpect(model().size(0));
        
        verify(studentsService).deleteStudent(10);
    }
}
