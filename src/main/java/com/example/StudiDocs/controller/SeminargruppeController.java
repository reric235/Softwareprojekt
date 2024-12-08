package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.service.SeminargruppeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/api/seminargruppen")
public class SeminargruppeController {

    private final SeminargruppeService seminargruppeService;

    @Autowired
    public SeminargruppeController(SeminargruppeService seminargruppeService) {
        this.seminargruppeService = seminargruppeService;
    }

    @GetMapping
    @ResponseBody
    public ResponseEntity<List<Seminargruppe>> getAllSeminargruppen() {
        List<Seminargruppe> seminargruppen = seminargruppeService.findeAlleSeminargruppen();
        return ResponseEntity.ok(seminargruppen);
    }
}