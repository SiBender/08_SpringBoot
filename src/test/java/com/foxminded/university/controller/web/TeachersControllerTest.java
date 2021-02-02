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
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.foxminded.university.controller.service.TeachersService;
import com.foxminded.university.model.Course;
import com.foxminded.university.model.Group;

@WebMvcTest(controllers = TeachersController.class)
class TeachersControllerTest {    
    @Autowired
    private MockMvc mockMvc;
    
    @Mock
    Model model;
    
    @MockBean
    TeachersService teachersService;
    
    @Autowired
    TeachersController teachersController;

    @Test
    void teachersInfoTest() throws Exception {
        this.mockMvc.perform(get("/teacher"))
                    .andExpect(view().name("teacher"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(3))
                    .andExpect(model().attributeExists("groups"))
                    .andExpect(model().attributeExists("teachers"))
                    .andExpect(model().attributeExists("course"));

        verify(teachersService).getAllGroups();
        verify(teachersService).getAll();
    }

    @Test
    void addCourseTest() throws Exception {
        this.mockMvc.perform(post("/addcourse")
                    .param("name", "TC")
                    .param("description", "Test course")
                    .param("teacher.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"));

        verify(teachersService).createCourse(anyString(), anyString(), anyInt());
    }

    @Test
    void deleteCourseTest() throws Exception {
        this.mockMvc.perform(get("/deletecourse")
                    .param("id", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"));
        
        verify(teachersService).deleteCourse(3);
    }

    @Test
    void getCourseTest() throws Exception {
        Course mockCourse = new Course();
        mockCourse.setId(1);
        Mockito.when(teachersService.getCourse(anyInt())).thenReturn(mockCourse);
        
        this.mockMvc.perform(get("/teacher/editcourse")
                    .param("id", "1"))
                    .andExpect(view().name("teacher/editcourse"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(2));
        
        verify(teachersService).getCourse(1);
        verify(teachersService).getAll();
    }

    @Test
    void updateCourseTest() throws Exception {
        this.mockMvc.perform(post("/teacher/editcourse")
                    .param("id", "1")
                    .param("name", "TC")
                    .param("descripton", "Test course")
                    .param("teacher.id", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"));
        
        verify(teachersService).updateCourse(any(Course.class));
    }

    @Test
    void deleteGroupsCourseTest() throws Exception {
        this.mockMvc.perform(get("/deletegroupscourse")
                    .param("cid", "1")
                    .param("gid", "1"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/teacher"))
                    .andExpect(model().size(0));
        
        verify(teachersService).deleteGroupsCourse(1, 1);
    }

    @Test
    void getGroupsCoursesInfoTest() throws Exception {
        Group mockGroup = new Group();
        mockGroup.setId(1);
        Mockito.when(teachersService.getGroupById(anyInt())).thenReturn(mockGroup);
        
        this.mockMvc.perform(get("/teacher/assigncourse")
                .param("gid", "1"))
                .andExpect(view().name("teacher/assigncourse"))
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("courses", Matchers.iterableWithSize(0)))
                .andExpect(model().attribute("group", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))));
        
        verify(teachersService).getGroupById(1);
        verify(teachersService).getFreeCourses(any(Group.class));
    }

    @Test
    void assignGroupsCourseTest() throws Exception {
        this.mockMvc.perform(post("/teacher/assigncourse")
                .param("id", "1")
                .param("groupName", "name")
                .param("courses[0].id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/teacher"));

        verify(teachersService).assignGroupsCourse(anyInt(), anyInt());
    }
}
