package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.service.SeminargruppeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class SeminargruppeControllerTest {

    private SeminargruppeService seminargruppeService;
    private SeminargruppeController seminargruppeController;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        seminargruppeService = mock(SeminargruppeService.class);
        seminargruppeController = new SeminargruppeController(seminargruppeService);
        mockMvc = MockMvcBuilders.standaloneSetup(seminargruppeController).build();
    }

    @Test
    void testGetAllSeminargruppen_Success() throws Exception {
        Seminargruppe sg1 = new Seminargruppe();
        sg1.setSeminargruppeId(1);
        sg1.setName("Gruppe A");
        // Initialize Studiengang to prevent serialization issues
        Studiengang studiengang1 = new Studiengang();
        studiengang1.setStudiengangId(101);
        studiengang1.setName("Studiengang X");
        sg1.setStudiengang(studiengang1);

        Seminargruppe sg2 = new Seminargruppe();
        sg2.setSeminargruppeId(2);
        sg2.setName("Gruppe B");
        Studiengang studiengang2 = new Studiengang();
        studiengang2.setStudiengangId(102);
        studiengang2.setName("Studiengang Y");
        sg2.setStudiengang(studiengang2);

        when(seminargruppeService.findeAlleSeminargruppen()).thenReturn(List.of(sg1, sg2));

        mockMvc.perform(get("/api/seminargruppen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].seminargruppeId").value(1))
                .andExpect(jsonPath("$[0].name").value("Gruppe A"))
                .andExpect(jsonPath("$[0].studiengang.studiengangId").value(101))
                .andExpect(jsonPath("$[0].studiengang.name").value("Studiengang X"))
                .andExpect(jsonPath("$[1].seminargruppeId").value(2))
                .andExpect(jsonPath("$[1].name").value("Gruppe B"))
                .andExpect(jsonPath("$[1].studiengang.studiengangId").value(102))
                .andExpect(jsonPath("$[1].studiengang.name").value("Studiengang Y"));
    }

    @Test
    void testCreateSeminargruppe_Success() throws Exception {
        Seminargruppe neueSeminargruppe = new Seminargruppe();
        neueSeminargruppe.setSeminargruppeId(1);
        neueSeminargruppe.setName("Gruppe A");
        // Initialize Studiengang to prevent serialization issues
        Studiengang studiengang = new Studiengang();
        studiengang.setStudiengangId(1);
        studiengang.setName("Studiengang X");
        neueSeminargruppe.setStudiengang(studiengang);

        when(seminargruppeService.erstelleSeminargruppe(ArgumentMatchers.any(Seminargruppe.class), ArgumentMatchers.anyInt()))
                .thenReturn(neueSeminargruppe);

        String jsonPayload = """
            {
                "seminargruppe": { "name": "Gruppe A" },
                "studiengangId": 1
            }
        """;

        mockMvc.perform(post("/api/seminargruppen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.seminargruppeId").value(1))
                .andExpect(jsonPath("$.name").value("Gruppe A"))
                .andExpect(jsonPath("$.studiengang.studiengangId").value(1))
                .andExpect(jsonPath("$.studiengang.name").value("Studiengang X"));
    }

    @Test
    void testCreateSeminargruppe_Failure_MissingFields() throws Exception {
        String jsonPayload = """
            {
                "seminargruppe": { "name": "Gruppe A" }
            }
        """;

        mockMvc.perform(post("/api/seminargruppen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Name und StudiengangId müssen angegeben werden."));
    }

    @Test
    void testCreateSeminargruppe_Failure_ServiceError() throws Exception {
        when(seminargruppeService.erstelleSeminargruppe(any(Seminargruppe.class), anyInt()))
                .thenThrow(new IllegalArgumentException("Fehler beim Erstellen der Seminargruppe."));

        String jsonPayload = """
            {
                "seminargruppe": { "name": "Gruppe A" },
                "studiengangId": 1
            }
        """;

        mockMvc.perform(post("/api/seminargruppen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(jsonPayload))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Fehler beim Erstellen der Seminargruppe."));
    }
}