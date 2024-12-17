package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Dokument;
import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.model.Rolle;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.service.DokumentService;
import com.example.StudiDocs.service.ModulService;
import com.example.StudiDocs.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class DokumentControllerTest {

    private DokumentService dokumentService;
    private StudentService studentService;
    private ModulService modulService;
    private DokumentController dokumentController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        dokumentService = Mockito.mock(DokumentService.class);
        studentService = Mockito.mock(StudentService.class);
        modulService = Mockito.mock(ModulService.class);
        dokumentController = new DokumentController(dokumentService, studentService, modulService);
        mockMvc = MockMvcBuilders.standaloneSetup(dokumentController).build();
    }

    @Test
    void testHochladenDokument_Success() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.pdf", "application/pdf", "Test Inhalt".getBytes());

        // Mock Authentication
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("admin@ba-sachsen.de");

        Student student = new Student();
        student.setStudentId(1);
        student.setRolle(Rolle.ADMIN);

        Modul modul = new Modul();
        modul.setModulId(1);
        modul.setModulname("Test Modul");

        when(studentService.findeStudentByEmail("admin@ba-sachsen.de")).thenReturn(Optional.of(student));
        when(modulService.findeModulById(1)).thenReturn(Optional.of(modul));

        mockMvc.perform(multipart("/api/dokumente/upload")
                        .file(file)
                        .param("modulId", "1")
                        .with(request -> {
                            request.setUserPrincipal(authentication);
                            return request;
                        }))
                .andExpect(status().isCreated())
                .andExpect(content().string("Dokument erfolgreich hochgeladen."));

        verify(dokumentService, times(1)).hochladenDokument(any(), eq(student), eq(modul));
    }



    @Test
    void testLoescheDokument_Success() throws Exception {
        Authentication authentication = Mockito.mock(Authentication.class);
        when(authentication.getName()).thenReturn("student@ba-sachsen.de");

        Student student = new Student();
        student.setStudentId(1);

        when(studentService.findeStudentByEmail("student@ba-sachsen.de")).thenReturn(Optional.of(student));

        mockMvc.perform(delete("/api/dokumente/1").principal(authentication))
                .andExpect(status().isOk())
                .andExpect(content().string("Dokument erfolgreich gelöscht."));

        verify(dokumentService, times(1)).loescheDokument(1, student);
    }

    @Test
    void testFindeDokumenteByModul_Success() throws Exception {
        Dokument dokument1 = new Dokument();
        dokument1.setDokumentId(1);
        dokument1.setName("Dokument 1");

        Dokument dokument2 = new Dokument();
        dokument2.setDokumentId(2);
        dokument2.setName("Dokument 2");

        when(dokumentService.findeDokumenteByModulSortiert(1, null)).thenReturn(List.of(dokument1, dokument2));

        mockMvc.perform(get("/api/dokumente/modul/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Dokument 1"))
                .andExpect(jsonPath("$[1].name").value("Dokument 2"));
    }

    @Test
    void testDownloadDokument_Success() throws Exception {
        Dokument dokument = new Dokument();
        dokument.setDokumentId(1);
        dokument.setName("test.pdf");
        dokument.setFilePath("uploads/test.pdf");

        Path filePath = Paths.get(dokument.getFilePath());
        Files.createDirectories(filePath.getParent());
        Files.write(filePath, "Test Inhalt".getBytes());

        when(dokumentService.findById(1)).thenReturn(Optional.of(dokument));

        mockMvc.perform(get("/api/dokumente/download/1"))
                .andExpect(status().isOk())
                .andExpect(header().string("Content-Disposition", "attachment; filename=\"test.pdf\""))
                .andExpect(header().string("Content-Type", "application/octet-stream"));

        Files.deleteIfExists(filePath);
    }

    @Test
    void testDownloadDokument_NotFound() throws Exception {
        when(dokumentService.findById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/api/dokumente/download/999"))
                .andExpect(status().isNotFound());
    }
}
