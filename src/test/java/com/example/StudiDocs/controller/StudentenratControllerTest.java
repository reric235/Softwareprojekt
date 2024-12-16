package com.example.StudiDocs.controller;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.InternalResourceViewResolver;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class StudentenratControllerTest {

    @Test
    void testShowStudentenratPage() throws Exception {
        StudentenratController controller = new StudentenratController();

        // Simple ViewResolver configuration for tests
        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
        viewResolver.setPrefix("/templates/");
        viewResolver.setSuffix(".html");

        MockMvc mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setViewResolvers(viewResolver)
                .build();

        mockMvc.perform(get("/studentenrat"))
                .andExpect(status().isOk())
                .andExpect(view().name("studentenrat"));
    }
}
