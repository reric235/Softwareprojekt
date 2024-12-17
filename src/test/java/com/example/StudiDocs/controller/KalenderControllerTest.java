package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.*;
import com.example.StudiDocs.service.KalenderService;
import com.example.StudiDocs.service.SeminargruppeService;
import com.example.StudiDocs.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class KalenderControllerTest {

    private KalenderService kalenderService;
    private StudentService studentService;
    private SeminargruppeService seminargruppeService;
    private KalenderController kalenderController;
    private MockMvc mockMvc;
    private Principal principal;

    @BeforeEach
    void setUp() {
        kalenderService = mock(KalenderService.class);
        studentService = mock(StudentService.class);
        seminargruppeService = mock(SeminargruppeService.class);

        kalenderController = new KalenderController(kalenderService, studentService, seminargruppeService);
        mockMvc = MockMvcBuilders.standaloneSetup(kalenderController).build();

        principal = mock(Principal.class);
    }

    @Test
    void testCreateKalendereintrag_Success() throws Exception {
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        when(seminargruppeService.findeSeminargruppeById(1)).thenReturn(Optional.of(seminargruppe));

        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        Student student = new Student();
        student.setStudentId(1);
        student.setEmail("student@example.com");
        student.setSeminargruppe(seminargruppe);
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        when(kalenderService.eintragenKalendereintrag(any(Kalendereintrag.class))).thenReturn(null); // Rückgabe wird vom Controller ignoriert

        when(principal.getName()).thenReturn("student@example.com");

        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().string("Kalendereintrag erfolgreich erstellt"));
    }

    @Test
    void testCreateKalendereintrag_Failure_MissingFields() throws Exception {
        String jsonContent = "{ \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        when(principal.getName()).thenReturn("student@example.com");

        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Alle Felder müssen ausgefüllt sein."));
    }

    @Test
    void testCreateKalendereintrag_Failure_StartTimeAfterEndTime() throws Exception {
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"16:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        when(principal.getName()).thenReturn("student@example.com");

        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Startzeit muss vor der Endzeit liegen."));
    }

    @Test
    void testCreateKalendereintrag_Failure_SeminargruppeNotFound() throws Exception {
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 999 }";

        when(seminargruppeService.findeSeminargruppeById(999)).thenReturn(Optional.empty());

        when(principal.getName()).thenReturn("student@example.com");

        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Seminargruppe mit der ID 999 existiert nicht."));
    }

    @Test
    void testCreateKalendereintrag_Failure_KalenderNotFound() throws Exception {
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        when(seminargruppeService.findeSeminargruppeById(1)).thenReturn(Optional.of(seminargruppe));

        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.empty());

        when(principal.getName()).thenReturn("student@example.com");

        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Kein Kalender für die Seminargruppe gefunden."));
    }

    @Test
    void testCreateKalendereintrag_Failure_StudentNotFound() throws Exception {
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        when(seminargruppeService.findeSeminargruppeById(1)).thenReturn(Optional.of(seminargruppe));

        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.empty());

        when(principal.getName()).thenReturn("student@example.com");

        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Student nicht gefunden"));
    }

    @Test
    void testDeleteKalendereintrag_Success() throws Exception {
        doNothing().when(kalenderService).loescheKalendereintrag(1);

        mockMvc.perform(delete("/kalender/kalendereintrag/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void testDeleteKalendereintrag_Failure_NotFound() throws Exception {
        doThrow(new IllegalArgumentException("Kalendereintrag existiert nicht."))
                .when(kalenderService).loescheKalendereintrag(999);

        mockMvc.perform(delete("/kalender/kalendereintrag/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Kalendereintrag existiert nicht."));
    }

    @Test
    void testLadeKalendereintraege_Success() throws Exception {
        when(principal.getName()).thenReturn("student@example.com");

        Student student = new Student();
        student.setStudentId(1);
        student.setEmail("student@example.com");
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        student.setSeminargruppe(seminargruppe);
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        Kalendereintrag eintrag1 = new Kalendereintrag();
        eintrag1.setKalendereintragId(1);
        eintrag1.setBeschreibung("Prüfung 1");
        eintrag1.setEintragsdatum(LocalDate.parse("2024-12-16"));
        eintrag1.setStartTime(LocalTime.parse("10:00"));
        eintrag1.setEndTime(LocalTime.parse("12:00"));
        eintrag1.setEventType(EventType.PRUEFUNG);
        eintrag1.setKalender(kalender);
        eintrag1.setStudent(student);

        Kalendereintrag eintrag2 = new Kalendereintrag();
        eintrag2.setKalendereintragId(2);
        eintrag2.setBeschreibung("Prüfung 2");
        eintrag2.setEintragsdatum(LocalDate.parse("2024-12-17"));
        eintrag2.setStartTime(LocalTime.parse("14:00"));
        eintrag2.setEndTime(LocalTime.parse("16:00"));
        eintrag2.setEventType(EventType.PRUEFUNG);
        eintrag2.setKalender(kalender);
        eintrag2.setStudent(student);

        when(kalenderService.findeKalendereintraegeByKalender(1)).thenReturn(List.of(eintrag1, eintrag2));

        mockMvc.perform(get("/kalender/kalendereintraege")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].beschreibung").value("Prüfung 1"))
                .andExpect(jsonPath("$[0].eventType").value("PRUEFUNG"))
                .andExpect(jsonPath("$[1].beschreibung").value("Prüfung 2"))
                .andExpect(jsonPath("$[1].eventType").value("PRUEFUNG"));
    }

    @Test
    void testLadeKalendereintraege_Failure_StudentNotFound() throws Exception {
        when(principal.getName()).thenReturn("student@example.com");

        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.empty());
        mockMvc.perform(get("/kalender/kalendereintraege")
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    void testLadeKalendereintraege_Failure_KalenderNotFound() throws Exception {
        when(principal.getName()).thenReturn("student@example.com");

        Student student = new Student();
        student.setStudentId(1);
        student.setEmail("student@example.com");
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        student.setSeminargruppe(seminargruppe);
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.empty());

        mockMvc.perform(get("/kalender/kalendereintraege")
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    @Test
    void testFindeKalendereintraegeByKalender_Success() throws Exception {

        Kalendereintrag eintrag1 = new Kalendereintrag();
        eintrag1.setKalendereintragId(1);
        eintrag1.setBeschreibung("Prüfung 1");
        eintrag1.setEventType(EventType.PRUEFUNG);

        Kalendereintrag eintrag2 = new Kalendereintrag();
        eintrag2.setKalendereintragId(2);
        eintrag2.setBeschreibung("Prüfung 2");
        eintrag2.setEventType(EventType.PRUEFUNG);

        when(kalenderService.findeKalendereintraegeByKalender(1)).thenReturn(List.of(eintrag1, eintrag2));

        mockMvc.perform(get("/kalender/1/eintraege"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].beschreibung").value("Prüfung 1"))
                .andExpect(jsonPath("$[0].eventType").value("PRUEFUNG"))
                .andExpect(jsonPath("$[1].beschreibung").value("Prüfung 2"))
                .andExpect(jsonPath("$[1].eventType").value("PRUEFUNG"));
    }

    @Test
    void testFindeKalendereintraegeByKalender_NoEntries() throws Exception {
        when(kalenderService.findeKalendereintraegeByKalender(1)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/kalender/1/eintraege"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testFindeKalendereintraegeByKalender_Failure_NoEntries() throws Exception {
        when(kalenderService.findeKalendereintraegeByKalender(999))
                .thenThrow(new IllegalArgumentException("Keine Kalendereinträge gefunden."));

        mockMvc.perform(get("/kalender/999/eintraege"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
    }

    @Test
    void testFindeKalenderBySeminargruppe_Success() throws Exception {
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);

        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        mockMvc.perform(get("/kalender/seminargruppe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kalenderId").value(1));
    }

    @Test
    void testFindeKalenderBySeminargruppe_NotFound() throws Exception {
        when(kalenderService.findeKalenderBySeminargruppe(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/kalender/seminargruppe/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFindeKalenderById_Success() throws Exception {
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);


        when(kalenderService.findeKalenderById(1)).thenReturn(Optional.of(kalender));

        mockMvc.perform(get("/kalender/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kalenderId").value(1));
    }

    @Test
    void testFindeKalenderById_NotFound() throws Exception {
        when(kalenderService.findeKalenderById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/kalender/id/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testFilterKalendereintraegeByEventType_Success() throws Exception {
        Kalendereintrag eintrag1 = new Kalendereintrag();
        eintrag1.setKalendereintragId(1);
        eintrag1.setBeschreibung("Prüfung 1");
        eintrag1.setEventType(EventType.PRUEFUNG);

        List<Kalendereintrag> gefilterteEintraege = List.of(eintrag1);

        when(kalenderService.filterKalendereintraegeByEventType(1, "PRUEFUNG")).thenReturn(gefilterteEintraege);

        mockMvc.perform(get("/kalender/1/filter")
                        .param("eventType", "PRUEFUNG"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].beschreibung").value("Prüfung 1"))
                .andExpect(jsonPath("$[0].eventType").value("PRUEFUNG"));
    }
}