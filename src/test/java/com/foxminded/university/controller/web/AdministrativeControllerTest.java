package com.foxminded.university.controller.web;

import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.atLeast;


import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.ui.Model;

import com.foxminded.university.controller.service.AdministrativeService;
import com.foxminded.university.model.Faculty;
import com.foxminded.university.model.Teacher;
import com.foxminded.university.model.Group;

@WebMvcTest(controllers = AdministrativeController.class)
class AdministrativeControllerTest {      
    @Autowired
    private MockMvc mockMvc;
    
    @MockBean
    AdministrativeService administrativeService;
   
    @Mock
    Model model;

    @Autowired
    AdministrativeController administrativeController;

    @Test
    void getInfoShouldCallAdministrativeServiceAndReturnCorrectViewNameTest() throws Exception {
        this.mockMvc.perform(get("/admin"))
                    .andExpect(view().name("admin"))
                    .andExpect(status().isOk())
                    .andExpect(model().size(6))
                    .andExpect(model().attributeExists("faculty"))
                    .andExpect(model().attributeExists("group"))
                    .andExpect(model().attributeExists("teacher"))
                    .andExpect(model().attributeExists("faculties"))
                    .andExpect(model().attributeExists("groups"))
                    .andExpect(model().attributeExists("teachers"));

        Faculty faculty = new Faculty();
        faculty.setId(1);
        Mockito.when(administrativeService.getAllFaculties()).thenReturn(Arrays.asList(faculty));
        
        administrativeController.getInfo(model);

        verify(model).addAttribute(anyString(), any(Faculty.class));
        verify(model).addAttribute(anyString(), any(Teacher.class));
        verify(model).addAttribute(anyString(), any(Group.class));
        verify(administrativeService, atLeast(1)).getAllFaculties();
        verify(administrativeService).getGroupsByFaculty(anyInt());
        verify(administrativeService).getTeachersByFaculty(anyInt());    
    }

    @Test
    void addFacultyTest() throws Exception {
        this.mockMvc.perform(post("/addfaculty")
                    .param("shortName", "testName")
                    .param("fullName", "testFullName"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"));

        verify(administrativeService).createFaculty("testName", "testFullName");
    }

    @Test
    void deleteFacultyTest() throws Exception {
        this.mockMvc.perform(get("/deletefaculty")
                    .param("id", "3"))
                    .andExpect(status().is3xxRedirection())
                    .andExpect(redirectedUrl("/admin"))
                    .andExpect(model().size(0));
        
        verify(administrativeService).deleteFaculty(3); 
    }

    @Test
    void getFacultyByIdTest() throws Exception {
        Faculty mockFaculty = new Faculty();
        mockFaculty.setId(1);
        Mockito.when(administrativeService.getFacultyById(anyInt())).thenReturn(mockFaculty);
        
        this.mockMvc.perform(get("/admin/editfaculty")
                .param("id", "1"))
                .andExpect(view().name("admin/editfaculty"))
                .andExpect(status().isOk())
                .andExpect(model().size(1));
        
        verify(administrativeService).getFacultyById(1);     
    }

    @Test
    void updateFacultyTest() throws Exception {
        this.mockMvc.perform(post("/admin/editfaculty")
                .param("id", "1")
                .param("shortName", "CS")
                .param("fullName", "Computer science"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        verify(administrativeService).updateFaculty(any(Faculty.class));   
    }

    @Test
    void addTeacherTest() throws Exception {
        this.mockMvc.perform(post("/addteacher")
                .param("firstName", "Alan")
                .param("lastName", "Turing")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(administrativeService).createTeacher(anyString(), anyString(), anyInt());   
    }

    @Test
    void deleteTeacherTest() throws Exception {
        this.mockMvc.perform(get("/deleteteacher")
                .param("id", "3"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        verify(administrativeService).deleteTeacher(anyInt());    
    }

    @Test
    void getTeacherByIdTest() throws Exception {
        Teacher mockTeacher = new Teacher();
        mockTeacher.setId(1);
        Mockito.when(administrativeService.getTeacherById(anyInt())).thenReturn(mockTeacher);
        
        this.mockMvc.perform(get("/admin/editteacher")
                .param("id", "1"))
                .andExpect(view().name("admin/editteacher"))
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("teacher", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))));
        
        verify(administrativeService).getTeacherById(anyInt());
        verify(administrativeService).getAllFaculties();    
    }

    @Test
    void updateTeacherTest() throws Exception {
        this.mockMvc.perform(post("/admin/editteacher")
                .param("firstName", "Alan")
                .param("lastName", "Turing")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));

        verify(administrativeService).updateTeacher(any(Teacher.class));    
    }

    @Test
    void addGroupTest() throws Exception {
        this.mockMvc.perform(post("/addgroup")
                .param("groupName", "Alan")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        verify(administrativeService).createGroup(anyString(), anyInt());    
    }

    @Test
    void deleteGroupTest() throws Exception {
        this.mockMvc.perform(get("/deletegroup")
                .param("id", "10"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        verify(administrativeService).deleteGroupById(anyInt());   
    }

    @Test
    void getGroupByIdTest() throws Exception {
        Group mockGroup = new Group();
        mockGroup.setId(1);
        Mockito.when(administrativeService.getGroupById(anyInt())).thenReturn(mockGroup);
        
        this.mockMvc.perform(get("/admin/editgroup")
                .param("id", "1"))
                .andExpect(view().name("admin/editgroup"))
                .andExpect(status().isOk())
                .andExpect(model().size(2))
                .andExpect(model().attribute("group", 
                        Matchers.hasProperty("id", Matchers.equalTo(1))));

        verify(administrativeService).getGroupById(anyInt());
        verify(administrativeService).getAllFaculties();   
    }

    @Test
    void updateGroupTest() throws Exception {
        this.mockMvc.perform(post("/admin/editgroup")
                .param("id", "1")
                .param("groupName", "cs-22")
                .param("faculty.id", "1"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin"));
        
        verify(administrativeService).updateGroup(any(Group.class));    
    }
}
