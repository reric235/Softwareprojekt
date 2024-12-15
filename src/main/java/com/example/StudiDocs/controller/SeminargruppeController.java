package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.service.SeminargruppeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

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

    @PostMapping
    public ResponseEntity<?> createSeminargruppe(@RequestBody Map<String, Object> payload) {
        try {
            Map<String, Object> seminargruppeMap = (Map<String, Object>) payload.get("seminargruppe");
            String name = (String) seminargruppeMap.get("name");
            Integer studiengangId = (Integer) payload.get("studiengangId");

            if (name == null || studiengangId == null) {
                throw new IllegalArgumentException("Name und StudiengangId müssen angegeben werden.");
            }

            Seminargruppe seminargruppe = new Seminargruppe();
            seminargruppe.setName(name);

            seminargruppeService.erstelleSeminargruppe(seminargruppe, studiengangId);
            return ResponseEntity.ok("Seminargruppe erfolgreich erstellt.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}