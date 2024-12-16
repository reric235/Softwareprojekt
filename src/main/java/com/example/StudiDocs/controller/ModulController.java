package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.repository.StudentRepository;
import com.example.StudiDocs.service.ModulService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/modul")
public class ModulController {

    private final ModulService modulService;
    private final StudentRepository studentRepository;

    @Autowired
    public ModulController(ModulService modulService, StudentRepository studentRepository) {
        this.modulService = modulService;
        this.studentRepository = studentRepository;
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

    @GetMapping("/semester/{semester}")
    public ResponseEntity<List<Modul>> getModulesBySemester(@PathVariable int semester) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUserEmail = authentication.getName(); // Annahme: Username ist die Email

        Optional<Student> studentOptional = studentRepository.findByEmail(currentUserEmail);
        if (studentOptional.isEmpty()) {
            return ResponseEntity.status(401).body(null); // Unauthorized
        }
        Student student = studentOptional.get();

        // Sicherstellen, dass Seminargruppe und Studiengang nicht null sind
        if (student.getSeminargruppe() == null || student.getSeminargruppe().getStudiengang() == null) {
            return ResponseEntity.badRequest().body(null); // Bad Request
        }

        int studiengangId = student.getSeminargruppe().getStudiengang().getStudiengangId();

        List<Modul> modules = modulService.findModulesByStudiengangAndSemester(studiengangId, semester);

        return ResponseEntity.ok(modules);
    }
}
