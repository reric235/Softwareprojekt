package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.service.ModulService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modul")
public class ModulController {

    private final ModulService modulService;

    @Autowired
    public ModulController(ModulService modulService) {
        this.modulService = modulService;
    }

    @PostMapping("/save")
    public ResponseEntity<?> saveModul(@RequestBody Modul modul) {
        try {
            Modul savedModul = modulService.saveModul(modul);
            return ResponseEntity.ok(savedModul);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Speichern des Moduls: " + e.getMessage());
        }
    }

    @GetMapping("/{modulId}")
    public ResponseEntity<Modul> findeModulById(@PathVariable int modulId) {
        Optional<Modul> modul = modulService.findeModulById(modulId);
        return modul.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{modulId}")
    public ResponseEntity<String> loescheModul(@PathVariable int modulId) {
        try {
            modulService.loescheModul(modulId);
            return ResponseEntity.ok("Modul erfolgreich gelöscht");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Modul>> getModuleBySemester(@PathVariable int semester) {
        List<Modul> module = modulService.findBySemester(semester);
        return ResponseEntity.ok(module);
    }
}
