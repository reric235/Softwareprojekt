package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.repository.StudentRepository;
import com.example.StudiDocs.service.ModulService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ModulControllerTest {

    private ModulService modulService;
    private StudentRepository studentRepository;
    private ModulController controller;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        // Mock-Objekte erstellen
        modulService = Mockito.mock(ModulService.class);
        studentRepository = Mockito.mock(StudentRepository.class);

        // Controller initialisieren
        controller = new ModulController(modulService, studentRepository);

        // MockMvc aufbauen
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        // Mock Authentication setzen
        Authentication authentication = mock(Authentication.class);
        when(authentication.getName()).thenReturn("student@example.com");
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // Mock StudentRepository konfigurieren
        Studiengang studiengang = new Studiengang();
        studiengang.setStudiengangId(1);
        studiengang.setName("Informatik");

        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setStudiengang(studiengang);
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");

        Student student = new Student();
        student.setEmail("student@example.com");
        student.setSeminargruppe(seminargruppe);

        when(studentRepository.findByEmail("student@example.com")).thenReturn(Optional.of(student));
    }

    @AfterEach
    void tearDown() {
        // Sicherheitskontext bereinigen
        SecurityContextHolder.clearContext();
    }

    @Test
    void testGetModuleBySemester_KeineEintraege() throws Exception {
        // ModulService konfigurieren
        when(modulService.findModulesByStudiengangAndSemester(1, 1)).thenReturn(Collections.emptyList());

        // API-Aufruf simulieren und überprüfen
        mockMvc.perform(get("/api/modul/semester/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));

        // Verifikation
        verify(modulService, times(1)).findModulesByStudiengangAndSemester(1, 1);
        verifyNoMoreInteractions(modulService);
    }

    @Test
    void testGetModuleBySemester_EinEintrag() throws Exception {
        // ModulService konfigurieren
        Modul m = new Modul();
        m.setModulname("Mathe1");
        m.setModulId(1);
        m.setSemester(1);
        m.setStudiengang(new Studiengang()); // Optional, je nach Modellstruktur

        when(modulService.findModulesByStudiengangAndSemester(1, 1)).thenReturn(List.of(m));

        // API-Aufruf simulieren und überprüfen
        mockMvc.perform(get("/api/modul/semester/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modulname").value("Mathe1"))
                .andExpect(jsonPath("$[0].modulId").value(1))
                .andExpect(jsonPath("$[0].semester").value(1));

        // Verifikation
        verify(modulService, times(1)).findModulesByStudiengangAndSemester(1, 1);
        verifyNoMoreInteractions(modulService);
    }

    @Test
    void testGetModuleBySemester_MehrereEintraege() throws Exception {
        // ModulService konfigurieren
        Modul m1 = new Modul();
        m1.setModulname("Mathe1");
        m1.setModulId(1);
        m1.setSemester(1);

        Modul m2 = new Modul();
        m2.setModulname("Informatik1");
        m2.setModulId(2);
        m2.setSemester(1);

        when(modulService.findModulesByStudiengangAndSemester(1, 1)).thenReturn(List.of(m1, m2));

        // API-Aufruf simulieren und überprüfen
        mockMvc.perform(get("/api/modul/semester/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modulname").value("Mathe1"))
                .andExpect(jsonPath("$[0].modulId").value(1))
                .andExpect(jsonPath("$[0].semester").value(1))
                .andExpect(jsonPath("$[1].modulname").value("Informatik1"))
                .andExpect(jsonPath("$[1].modulId").value(2))
                .andExpect(jsonPath("$[1].semester").value(1));

        // Verifikation
        verify(modulService, times(1)).findModulesByStudiengangAndSemester(1, 1);
        verifyNoMoreInteractions(modulService);
    }

    @Test
    void testGetModulById_Found() throws Exception {
        // ModulService konfigurieren
        Modul m = new Modul();
        m.setModulId(1);
        m.setModulname("Mathe1");
        m.setSemester(1);
        Studiengang studiengang = new Studiengang();
        studiengang.setName("Informatik");
        m.setStudiengang(studiengang);

        when(modulService.findeModulById(1)).thenReturn(Optional.of(m));

        // API-Aufruf simulieren und überprüfen
        mockMvc.perform(get("/api/modul/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modulname").value("Mathe1"))
                .andExpect(jsonPath("$.semester").value(1))
                .andExpect(jsonPath("$.studiengang.name").value("Informatik"));

        // Verifikation
        verify(modulService, times(1)).findeModulById(1);
        verifyNoMoreInteractions(modulService);
    }

    @Test
    void testGetModulById_NotFound() throws Exception {
        // ModulService konfigurieren
        when(modulService.findeModulById(999)).thenReturn(Optional.empty());

        // API-Aufruf simulieren und überprüfen
        mockMvc.perform(get("/api/modul/999"))
                .andExpect(status().isNotFound());

        // Verifikation
        verify(modulService, times(1)).findeModulById(999);
        verifyNoMoreInteractions(modulService);
    }
}
