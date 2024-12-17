package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.service.StudiengangService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/studiengaenge")
public class StudiengangController {

    private final StudiengangService studiengangService;

    @Autowired
    public StudiengangController(StudiengangService studiengangService) {
        this.studiengangService = studiengangService;
    }

    @GetMapping
    public ResponseEntity<List<Studiengang>> getAllStudiengaenge() {
        List<Studiengang> studiengaenge = studiengangService.findeAlleStudiengaenge();
        return ResponseEntity.ok(studiengaenge);
    }
}
