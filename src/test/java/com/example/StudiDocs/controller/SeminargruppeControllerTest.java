package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Seminargruppe;
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

        Seminargruppe sg2 = new Seminargruppe();
        sg2.setSeminargruppeId(2);
        sg2.setName("Gruppe B");

        when(seminargruppeService.findeAlleSeminargruppen()).thenReturn(List.of(sg1, sg2));

        mockMvc.perform(get("/api/seminargruppen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].name").value("Gruppe A"))
                .andExpect(jsonPath("$[1].name").value("Gruppe B"));
    }

    /**
     * Testet das erfolgreiche Erstellen einer Seminargruppe.
     */
    @Test
    void testCreateSeminargruppe_Success() throws Exception {
        Seminargruppe neueSeminargruppe = new Seminargruppe();
        neueSeminargruppe.setSeminargruppeId(1);
        neueSeminargruppe.setName("Gruppe A");

        when(seminargruppeService.erstelleSeminargruppe(ArgumentMatchers.any(Seminargruppe.class), ArgumentMatchers.eq(1)))
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
                .andExpect(jsonPath("$.name").value("Gruppe A"));
    }

    /**
     * Testet das Erstellen einer Seminargruppe mit fehlenden Feldern.
     */
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

    /**
     * Testet das Erstellen einer Seminargruppe mit einem Fehler im Service.
     */
    @Test
    void testCreateSeminargruppe_Failure_ServiceError() throws Exception {
        when(seminargruppeService.erstelleSeminargruppe(any(Seminargruppe.class), eq(1)))
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
