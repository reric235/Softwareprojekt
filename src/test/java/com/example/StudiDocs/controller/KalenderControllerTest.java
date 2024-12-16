package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.*;
import com.example.StudiDocs.service.KalenderService;
import com.example.StudiDocs.service.SeminargruppeService;
import com.example.StudiDocs.service.StudentService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
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

    /**
     * Testet das erfolgreiche Erstellen eines Kalendereintrags.
     */
    @Test
    void testCreateKalendereintrag_Success() throws Exception {
        // JSON-Payload für den Test
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        // Mocking SeminargruppeService.findeSeminargruppeById
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        when(seminargruppeService.findeSeminargruppeById(1)).thenReturn(Optional.of(seminargruppe));

        // Mocking KalenderService.findeKalenderBySeminargruppe
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        // Mocking StudentService.findeStudentByEmail
        Student student = new Student();
        student.setStudentId(1);
        student.setEmail("student@example.com");
        student.setSeminargruppe(seminargruppe);
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        // Mocking KalenderService.eintragenKalendereintrag
        when(kalenderService.eintragenKalendereintrag(any(Kalendereintrag.class))).thenReturn(null); // Rückgabe wird vom Controller ignoriert

        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Durchführung des POST-Requests
        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isOk())
                .andExpect(content().string("Kalendereintrag erfolgreich erstellt"));
    }

    /**
     * Testet das Erstellen eines Kalendereintrags mit fehlenden Feldern.
     */
    @Test
    void testCreateKalendereintrag_Failure_MissingFields() throws Exception {
        // JSON-Payload ohne "beschreibung"
        String jsonContent = "{ \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Durchführung des POST-Requests
        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Alle Felder müssen ausgefüllt sein."));
    }

    /**
     * Testet das Erstellen eines Kalendereintrags, bei dem die Startzeit nach der Endzeit liegt.
     */
    @Test
    void testCreateKalendereintrag_Failure_StartTimeAfterEndTime() throws Exception {
        // JSON-Payload mit Startzeit nach Endzeit
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"16:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Durchführung des POST-Requests
        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Startzeit muss vor der Endzeit liegen."));
    }

    /**
     * Testet das Erstellen eines Kalendereintrags mit einer nicht existierenden Seminargruppe.
     */
    @Test
    void testCreateKalendereintrag_Failure_SeminargruppeNotFound() throws Exception {
        // JSON-Payload mit nicht existierender seminargruppeId
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 999 }";

        // Mocking SeminargruppeService.findeSeminargruppeById
        when(seminargruppeService.findeSeminargruppeById(999)).thenReturn(Optional.empty());

        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Durchführung des POST-Requests
        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Seminargruppe mit der ID 999 existiert nicht."));
    }

    /**
     * Testet das Erstellen eines Kalendereintrags, bei dem kein Kalender für die Seminargruppe gefunden wird.
     */
    @Test
    void testCreateKalendereintrag_Failure_KalenderNotFound() throws Exception {
        // JSON-Payload
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        // Mocking SeminargruppeService.findeSeminargruppeById
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        when(seminargruppeService.findeSeminargruppeById(1)).thenReturn(Optional.of(seminargruppe));

        // Mocking KalenderService.findeKalenderBySeminargruppe
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.empty());

        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Durchführung des POST-Requests
        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Kein Kalender für die Seminargruppe gefunden."));
    }

    /**
     * Testet das Erstellen eines Kalendereintrags, bei dem der Student nicht gefunden wird.
     */
    @Test
    void testCreateKalendereintrag_Failure_StudentNotFound() throws Exception {
        // JSON-Payload
        String jsonContent = "{ \"beschreibung\": \"Neuer Termin\", \"eintragsdatum\": \"2024-12-16\", \"eventType\": \"PRUEFUNG\", \"startTime\": \"14:00\", \"endTime\": \"15:00\", \"seminargruppeId\": 1 }";

        // Mocking SeminargruppeService.findeSeminargruppeById
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        when(seminargruppeService.findeSeminargruppeById(1)).thenReturn(Optional.of(seminargruppe));

        // Mocking KalenderService.findeKalenderBySeminargruppe
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        // Mocking StudentService.findeStudentByEmail
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.empty());

        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Durchführung des POST-Requests
        mockMvc.perform(post("/kalender/kalendereintrag")
                        .principal(principal)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonContent))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Student nicht gefunden"));
    }

    /**
     * Testet das erfolgreiche Löschen eines Kalendereintrags.
     */
    @Test
    void testDeleteKalendereintrag_Success() throws Exception {
        // Mocking KalenderService.loescheKalendereintrag
        doNothing().when(kalenderService).loescheKalendereintrag(1);

        // Durchführung des DELETE-Requests
        mockMvc.perform(delete("/kalender/kalendereintrag/1"))
                .andExpect(status().isNoContent());
    }

    /**
     * Testet das Löschen eines nicht existierenden Kalendereintrags.
     */
    @Test
    void testDeleteKalendereintrag_Failure_NotFound() throws Exception {
        // Mocking KalenderService.loescheKalendereintrag mit Exception
        doThrow(new IllegalArgumentException("Kalendereintrag existiert nicht."))
                .when(kalenderService).loescheKalendereintrag(999);

        // Durchführung des DELETE-Requests
        mockMvc.perform(delete("/kalender/kalendereintrag/999"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Kalendereintrag existiert nicht."));
    }

    /**
     * Testet das erfolgreiche Laden von Kalendereinträgen.
     */
    @Test
    void testLadeKalendereintraege_Success() throws Exception {
        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Mocking StudentService.findeStudentByEmail
        Student student = new Student();
        student.setStudentId(1);
        student.setEmail("student@example.com");
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        student.setSeminargruppe(seminargruppe);
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        // Mocking KalenderService.findeKalenderBySeminargruppe
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        // Mocking KalenderService.findeKalendereintraegeByKalender
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

        // Durchführung des GET-Requests
        mockMvc.perform(get("/kalender/kalendereintraege")
                        .principal(principal))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].beschreibung").value("Prüfung 1"))
                .andExpect(jsonPath("$[0].eventType").value("PRUEFUNG"))
                .andExpect(jsonPath("$[1].beschreibung").value("Prüfung 2"))
                .andExpect(jsonPath("$[1].eventType").value("PRUEFUNG"));
    }

    /**
     * Testet das Laden von Kalendereinträgen, wenn der Student nicht gefunden wird.
     */
    @Test
    void testLadeKalendereintraege_Failure_StudentNotFound() throws Exception {
        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Mocking StudentService.findeStudentByEmail mit leerem Optional
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.empty());

        // Durchführung des GET-Requests
        mockMvc.perform(get("/kalender/kalendereintraege")
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    /**
     * Testet das Laden von Kalendereinträgen, wenn kein Kalender für die Seminargruppe gefunden wird.
     */
    @Test
    void testLadeKalendereintraege_Failure_KalenderNotFound() throws Exception {
        // Mocking Principal.getName()
        when(principal.getName()).thenReturn("student@example.com");

        // Mocking StudentService.findeStudentByEmail
        Student student = new Student();
        student.setStudentId(1);
        student.setEmail("student@example.com");
        Seminargruppe seminargruppe = new Seminargruppe();
        seminargruppe.setSeminargruppeId(1);
        seminargruppe.setName("Gruppe A");
        student.setSeminargruppe(seminargruppe);
        when(studentService.findeStudentByEmail("student@example.com")).thenReturn(Optional.of(student));

        // Mocking KalenderService.findeKalenderBySeminargruppe mit leerem Optional
        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.empty());

        // Durchführung des GET-Requests
        mockMvc.perform(get("/kalender/kalendereintraege")
                        .principal(principal))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(""));
    }

    /**
     * Testet das erfolgreiche Abrufen von Kalendereinträgen eines bestimmten Kalenders.
     */
    @Test
    void testFindeKalendereintraegeByKalender_Success() throws Exception {
        // Mocking KalenderService.findeKalendereintraegeByKalender
        Kalendereintrag eintrag1 = new Kalendereintrag();
        eintrag1.setKalendereintragId(1);
        eintrag1.setBeschreibung("Prüfung 1");
        eintrag1.setEventType(EventType.PRUEFUNG);

        Kalendereintrag eintrag2 = new Kalendereintrag();
        eintrag2.setKalendereintragId(2);
        eintrag2.setBeschreibung("Prüfung 2");
        eintrag2.setEventType(EventType.PRUEFUNG);

        when(kalenderService.findeKalendereintraegeByKalender(1)).thenReturn(List.of(eintrag1, eintrag2));

        // Durchführung des GET-Requests
        mockMvc.perform(get("/kalender/1/eintraege"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].beschreibung").value("Prüfung 1"))
                .andExpect(jsonPath("$[0].eventType").value("PRUEFUNG"))
                .andExpect(jsonPath("$[1].beschreibung").value("Prüfung 2"))
                .andExpect(jsonPath("$[1].eventType").value("PRUEFUNG"));
    }

    /**
     * Testet das Abrufen von Kalendereinträgen eines bestimmten Kalenders, wenn keine Einträge vorhanden sind.
     */
    @Test
    void testFindeKalendereintraegeByKalender_NoEntries() throws Exception {
        // Mocking KalenderService.findeKalendereintraegeByKalender mit leerer Liste
        when(kalenderService.findeKalendereintraegeByKalender(1)).thenReturn(Collections.emptyList());

        // Durchführung des GET-Requests
        mockMvc.perform(get("/kalender/1/eintraege"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    /**
     * Testet das Abrufen von Kalendereinträgen eines bestimmten Kalenders, wenn ein Fehler auftritt.
     */
    @Test
    void testFindeKalendereintraegeByKalender_Failure_NoEntries() throws Exception {
        // Mocking KalenderService.findeKalendereintraegeByKalender mit Exception
        when(kalenderService.findeKalendereintraegeByKalender(999))
                .thenThrow(new IllegalArgumentException("Keine Kalendereinträge gefunden."));

        // Durchführung des GET-Requests
        mockMvc.perform(get("/kalender/999/eintraege"))
                .andExpect(status().isNotFound())
                .andExpect(content().json("[]"));
    }

    /**
     * Testet das erfolgreiche Abrufen eines Kalenders anhand der Seminargruppen-ID.
     */
    @Test
    void testFindeKalenderBySeminargruppe_Success() throws Exception {
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);

        when(kalenderService.findeKalenderBySeminargruppe(1)).thenReturn(Optional.of(kalender));

        mockMvc.perform(get("/kalender/seminargruppe/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kalenderId").value(1));
    }

    /**
     * Testet das Abrufen eines Kalenders anhand einer nicht existierenden Seminargruppen-ID.
     */
    @Test
    void testFindeKalenderBySeminargruppe_NotFound() throws Exception {
        when(kalenderService.findeKalenderBySeminargruppe(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/kalender/seminargruppe/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Testet das erfolgreiche Abrufen eines Kalenders anhand der Kalender-ID.
     */
    @Test
    void testFindeKalenderById_Success() throws Exception {
        Kalender kalender = new Kalender();
        kalender.setKalenderId(1);


        when(kalenderService.findeKalenderById(1)).thenReturn(Optional.of(kalender));

        mockMvc.perform(get("/kalender/id/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kalenderId").value(1));
    }

    /**
     * Testet das Abrufen eines Kalenders anhand einer nicht existierenden Kalender-ID.
     */
    @Test
    void testFindeKalenderById_NotFound() throws Exception {
        when(kalenderService.findeKalenderById(999)).thenReturn(Optional.empty());

        mockMvc.perform(get("/kalender/id/999"))
                .andExpect(status().isNotFound());
    }

    /**
     * Testet das erfolgreiche Filtern von Kalendereinträgen nach Event-Typ.
     */
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


    @Test
    void testGetAllSeminargruppen_Success() throws Exception {
        Seminargruppe sg1 = new Seminargruppe();
        sg1.setSeminargruppeId(1);
        sg1.setName("Gruppe A");

        Seminargruppe sg2 = new Seminargruppe();
        sg2.setSeminargruppeId(2);
        sg2.setName("Gruppe B");

        when(seminargruppeService.findeAlleSeminargruppen()).thenReturn(List.of(sg1, sg2));

        mockMvc.perform(get("/kalender/seminargruppen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Gruppe A"))
                .andExpect(jsonPath("$[1].name").value("Gruppe B"));
    }
}
