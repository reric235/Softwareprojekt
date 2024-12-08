package com.example.StudiDocs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class StudentenratController {

    @GetMapping("/studentenrat")
    public String showStudentenratPage() {
        return "studentenrat";
    }
}
