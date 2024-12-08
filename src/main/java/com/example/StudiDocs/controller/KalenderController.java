package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.*;
import com.example.StudiDocs.service.KalenderService;
import com.example.StudiDocs.service.SeminargruppeService;
import com.example.StudiDocs.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

@RestController
@RequestMapping("/kalender")
public class KalenderController {

    private final KalenderService kalenderService;
    private final StudentService studentService;
    private final SeminargruppeService seminargruppeService;

    @Autowired
    public KalenderController(KalenderService kalenderService, StudentService studentService, SeminargruppeService seminargruppeService) {
        this.kalenderService = kalenderService;
        this.studentService = studentService;
        this.seminargruppeService = seminargruppeService;
    }

    @GetMapping("/kalendereintraege")
    public ResponseEntity<List<Kalendereintrag>> ladeKalendereintraege(Principal principal) {
        try {
            String email = principal.getName();
            Student student = studentService.findeStudentByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Student nicht gefunden"));

            Seminargruppe seminargruppe = student.getSeminargruppe();
            Kalender kalender = kalenderService.findeKalenderBySeminargruppe(seminargruppe.getSeminargruppeId())
                    .orElseThrow(() -> new IllegalArgumentException("Kein Kalender gefunden"));

            List<Kalendereintrag> eintraege = kalenderService.findeKalendereintraegeByKalender(kalender.getKalenderId());

            System.out.println("Geladene Kalendereinträge für Seminargruppe '" + seminargruppe.getName() + "':");
            eintraege.forEach(eintrag -> System.out.println(eintrag));

            return ResponseEntity.ok(eintraege);
        } catch (Exception e) {
            System.err.println("Fehler beim Laden der Kalendereinträge: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
    }

    @PostMapping("/kalendereintrag")
    public ResponseEntity<?> createKalendereintrag(@RequestBody Map<String, Object> payload, Principal principal) {
        try {
            System.out.println("Payload received: " + payload);

            String beschreibung = (String) payload.get("beschreibung");
            String eintragsdatumStr = (String) payload.get("eintragsdatum");
            String eventTypeStr = (String) payload.get("eventType");
            String startTimeStr = (String) payload.get("startTime");
            String endTimeStr = (String) payload.get("endTime");
            Integer seminargruppeId = (Integer) payload.get("seminargruppeId"); // Neues Feld

            System.out.println("Beschreibung: " + beschreibung);
            System.out.println("Eintragsdatum: " + eintragsdatumStr);
            System.out.println("EventType: " + eventTypeStr);
            System.out.println("StartTime: " + startTimeStr);
            System.out.println("EndTime: " + endTimeStr);
            System.out.println("SeminargruppeId: " + seminargruppeId);

            if (beschreibung == null || eintragsdatumStr == null || eventTypeStr == null || startTimeStr == null || endTimeStr == null || seminargruppeId == null) {
                throw new IllegalArgumentException("Alle Felder müssen ausgefüllt sein.");
            }

            LocalDate eintragsdatum = LocalDate.parse(eintragsdatumStr);
            LocalTime startTime = LocalTime.parse(startTimeStr);
            LocalTime endTime = LocalTime.parse(endTimeStr);

            if (!startTime.isBefore(endTime)) {
                throw new IllegalArgumentException("Startzeit muss vor der Endzeit liegen.");
            }

            Optional<Seminargruppe> optionalSeminargruppe = seminargruppeService.findeSeminargruppeById(seminargruppeId);
            if (optionalSeminargruppe.isEmpty()) {
                throw new IllegalArgumentException("Seminargruppe mit der ID " + seminargruppeId + " existiert nicht.");
            }

            Seminargruppe seminargruppe = optionalSeminargruppe.get();
            Kalender kalender = kalenderService.findeKalenderBySeminargruppe(seminargruppe.getSeminargruppeId())
                    .orElseThrow(() -> new IllegalArgumentException("Kein Kalender für die Seminargruppe gefunden."));

            String email = principal.getName();
            Student student = studentService.findeStudentByEmail(email)
                    .orElseThrow(() -> new IllegalArgumentException("Student nicht gefunden"));

            Kalendereintrag kalendereintrag = new Kalendereintrag();
            kalendereintrag.setBeschreibung(beschreibung);
            kalendereintrag.setEintragsdatum(eintragsdatum);
            kalendereintrag.setStartTime(startTime);
            kalendereintrag.setEndTime(endTime);
            kalendereintrag.setEventType(EventType.valueOf(eventTypeStr));
            kalendereintrag.setKalender(kalender);
            kalendereintrag.setStudent(student);

            System.out.println("Erstellter Kalendereintrag: " + kalendereintrag);

            kalenderService.eintragenKalendereintrag(kalendereintrag);

            return ResponseEntity.ok("Kalendereintrag erfolgreich erstellt");
        } catch (Exception e) {
            System.err.println("Fehler beim Erstellen des Kalendereintrags: " + e.getMessage());
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/kalendereintrag/{id}")
    public ResponseEntity<?> loeschenKalendereintrag(@PathVariable("id") int kalendereintragId) {
        try {
            kalenderService.loescheKalendereintrag(kalendereintragId);
            return ResponseEntity.noContent().build();
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/{kalenderId}/eintraege")
    public ResponseEntity<List<Kalendereintrag>> findeKalendereintraegeByKalender(@PathVariable int kalenderId) {
        try {
            List<Kalendereintrag> eintraege = kalenderService.findeKalendereintraegeByKalender(kalenderId);
            return ResponseEntity.ok(eintraege);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Collections.emptyList());
        }
    }

    @GetMapping("/seminargruppe/{seminargruppeId}")
    public ResponseEntity<Kalender> findeKalenderBySeminargruppe(@PathVariable int seminargruppeId) {
        Optional<Kalender> kalender = kalenderService.findeKalenderBySeminargruppe(seminargruppeId);
        return kalender.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/id/{kalenderId}")
    public ResponseEntity<Kalender> findeKalenderById(@PathVariable int kalenderId) {
        Optional<Kalender> kalender = kalenderService.findeKalenderById(kalenderId);
        return kalender.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).build());
    }

    @GetMapping("/{kalenderId}/filter")
    public ResponseEntity<List<Kalendereintrag>> filterKalendereintraegeByEventType(
            @PathVariable int kalenderId, @RequestParam String eventType) {
        try {
            List<Kalendereintrag> gefilterteEintraege = kalenderService.filterKalendereintraegeByEventType(kalenderId, eventType);
            return ResponseEntity.ok(gefilterteEintraege);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body(List.of());
        }
    }

    @GetMapping("/seminargruppen")
    public ResponseEntity<List<Seminargruppe>> getAllSeminargruppen() {
        List<Seminargruppe> seminargruppen = seminargruppeService.findeAlleSeminargruppen();
        return ResponseEntity.ok(seminargruppen);
    }
}