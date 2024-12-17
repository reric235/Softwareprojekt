package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.service.SeminargruppeService;
import com.example.StudiDocs.service.StudentService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentControllerTest {

    @Test
    void testGetAllStudents_KeineEintraege() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Mockito.when(studentService.findeAlleStudenten()).thenReturn(Collections.emptyList());

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetAllStudents_EineEintragung() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Student st = new Student();
        st.setVorname("Max");
        st.setNachname("Mustermann");
        st.setEmail("max@ba-sachsen.de");

        Mockito.when(studentService.findeAlleStudenten()).thenReturn(List.of(st));

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].vorname").value("Max"))
                .andExpect(jsonPath("$[0].nachname").value("Mustermann"))
                .andExpect(jsonPath("$[0].email").value("max@ba-sachsen.de"));
    }

    @Test
    void testGetAllStudents_MehrereEintraege() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Student st1 = new Student();
        st1.setVorname("Max");
        st1.setNachname("Mustermann");
        st1.setEmail("max@ba-sachsen.de");

        Student st2 = new Student();
        st2.setVorname("Erika");
        st2.setNachname("Musterfrau");
        st2.setEmail("erika@ba-sachsen.de");

        Mockito.when(studentService.findeAlleStudenten()).thenReturn(List.of(st1, st2));

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].email").value("max@ba-sachsen.de"))
                .andExpect(jsonPath("$[1].email").value("erika@ba-sachsen.de"));
    }

    @Test
    void testGetStudentById_Vorhanden() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Student st = new Student();
        st.setVorname("Max");
        st.setNachname("Mustermann");
        st.setEmail("max@ba-sachsen.de");
        st.setStudentId(1);

        Mockito.when(studentService.findeStudentById(1)).thenReturn(Optional.of(st));

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("max@ba-sachsen.de"));
    }

    @Test
    void testGetStudentById_NichtVorhanden() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Mockito.when(studentService.findeStudentById(999)).thenReturn(Optional.empty());

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteStudent_Vorhanden() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Mockito.when(studentService.loescheStudent(1)).thenReturn(true);

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(delete("/api/students/1"))
                .andExpect(status().isOk())
                .andExpect(content().string("Student erfolgreich gelöscht."));
    }

    @Test
    void testDeleteStudent_NichtVorhanden() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Mockito.when(studentService.loescheStudent(999)).thenReturn(false);

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(delete("/api/students/999"))
                .andExpect(status().isNotFound());
    }


    @Test
    void testGetStudentByEmail_Vorhanden() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Student st = new Student();
        st.setEmail("max@ba-sachsen.de");
        Mockito.when(studentService.findeStudentByEmail("max@ba-sachsen.de")).thenReturn(Optional.of(st));

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students/email/max@ba-sachsen.de"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value("max@ba-sachsen.de"));
    }

    @Test
    void testGetStudentByEmail_NichtVorhanden() throws Exception {
        StudentService studentService = Mockito.mock(StudentService.class);
        SeminargruppeService seminargruppeService = Mockito.mock(SeminargruppeService.class);

        Mockito.when(studentService.findeStudentByEmail("noone@ba-sachsen.de")).thenReturn(Optional.empty());

        StudentController controller = new StudentController(studentService, seminargruppeService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/students/email/noone@ba-sachsen.de"))
                .andExpect(status().isNotFound());
    }
}
