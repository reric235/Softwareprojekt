package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.service.StudiengangService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudiengangControllerTest {

    @Test
    void testGetAllStudiengaenge_EineEintragung() throws Exception {
        StudiengangService studiengangService = Mockito.mock(StudiengangService.class);
        Studiengang sg = new Studiengang();
        sg.setName("Informatik");
        Mockito.when(studiengangService.findeAlleStudiengaenge()).thenReturn(List.of(sg));

        StudiengangController controller = new StudiengangController(studiengangService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/studiengaenge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Informatik"));
    }

    @Test
    void testGetAllStudiengaenge_KeineEintraege() throws Exception {
        StudiengangService studiengangService = Mockito.mock(StudiengangService.class);
        Mockito.when(studiengangService.findeAlleStudiengaenge()).thenReturn(Collections.emptyList());

        StudiengangController controller = new StudiengangController(studiengangService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/studiengaenge"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetAllStudiengaenge_MehrereEintraege() throws Exception {
        StudiengangService studiengangService = Mockito.mock(StudiengangService.class);
        Studiengang sg1 = new Studiengang();
        sg1.setName("Informatik");
        Studiengang sg2 = new Studiengang();
        sg2.setName("BWL");

        Mockito.when(studiengangService.findeAlleStudiengaenge()).thenReturn(List.of(sg1, sg2));

        StudiengangController controller = new StudiengangController(studiengangService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/studiengaenge"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Informatik"))
                .andExpect(jsonPath("$[1].name").value("BWL"));
    }


}
