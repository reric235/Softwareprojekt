package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.service.ModulService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class ModulControllerTest {

    @Test
    void testGetModuleBySemester_KeineEintraege() throws Exception {
        ModulService modulService = Mockito.mock(ModulService.class);
        Mockito.when(modulService.findBySemester(1)).thenReturn(Collections.emptyList());

        ModulController controller = new ModulController(modulService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/modul/semester/1"))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    void testGetModuleBySemester_EinEintrag() throws Exception {
        ModulService modulService = Mockito.mock(ModulService.class);
        Modul m = new Modul();
        m.setModulname("Mathe1");
        Mockito.when(modulService.findBySemester(1)).thenReturn(List.of(m));

        ModulController controller = new ModulController(modulService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/modul/semester/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modulname").value("Mathe1"));
    }

    @Test
    void testGetModuleBySemester_MehrereEintraege() throws Exception {
        ModulService modulService = Mockito.mock(ModulService.class);
        Modul m1 = new Modul();
        m1.setModulname("Mathe1");
        Modul m2 = new Modul();
        m2.setModulname("Informatik1");

        Mockito.when(modulService.findBySemester(1)).thenReturn(List.of(m1, m2));

        ModulController controller = new ModulController(modulService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/modul/semester/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].modulname").value("Mathe1"))
                .andExpect(jsonPath("$[1].modulname").value("Informatik1"));
    }

    @Test
    void testGetModulById_Found() throws Exception {
        ModulService modulService = Mockito.mock(ModulService.class);
        Modul m = new Modul();
        m.setModulId(1);
        m.setModulname("Mathe1");
        m.setSemester(1);
        m.setStudiengang("Informatik");

        Mockito.when(modulService.findeModulById(1)).thenReturn(Optional.of(m));

        ModulController controller = new ModulController(modulService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/modul/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.modulname").value("Mathe1"))
                .andExpect(jsonPath("$.semester").value(1))
                .andExpect(jsonPath("$.studiengang").value("Informatik"));
    }

    @Test
    void testGetModulById_NotFound() throws Exception {
        ModulService modulService = Mockito.mock(ModulService.class);
        Mockito.when(modulService.findeModulById(999)).thenReturn(Optional.empty());

        ModulController controller = new ModulController(modulService);
        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller).build();

        mockMvc.perform(get("/api/modul/999"))
                .andExpect(status().isNotFound());
    }
}
