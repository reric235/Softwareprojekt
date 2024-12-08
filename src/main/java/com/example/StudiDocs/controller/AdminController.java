package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.*;
import com.example.StudiDocs.service.KalenderService;
import com.example.StudiDocs.service.SeminargruppeService;
import com.example.StudiDocs.service.StudentService;
import com.example.StudiDocs.service.StudiengangService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Optional;
import java.util.List;

@RestController
@RequestMapping("/admin")
public class AdminController {

    private final SeminargruppeService seminargruppeService;
    private final StudiengangService studiengangService;
    private final KalenderService kalenderService;
    private final StudentService studentService;

    @Autowired
    public AdminController(SeminargruppeService seminargruppeService, StudiengangService studiengangService, KalenderService kalenderService, StudentService studentService) {
        this.seminargruppeService = seminargruppeService;
        this.studiengangService = studiengangService;
        this.kalenderService = kalenderService;
        this.studentService = studentService;
    }

    @GetMapping("/studiengaenge")
    public List<Studiengang> getAllStudiengaenge() {
        return studiengangService.findeAlleStudiengaenge();
    }

    @PostMapping("/studiengang")
    public ResponseEntity<Studiengang> createStudiengang(@RequestBody Studiengang studiengang) {
        Studiengang neuerStudiengang = studiengangService.erstelleStudiengang(studiengang);
        return ResponseEntity.ok(neuerStudiengang);
    }

    @DeleteMapping("/studiengang/{id}")
    public ResponseEntity<String> deleteStudiengang(@PathVariable int id) {
        studiengangService.loescheStudiengang(id);
        return ResponseEntity.ok("Studiengang erfolgreich gelöscht.");
    }

    @GetMapping("/seminargruppen")
    public List<Seminargruppe> getAllSeminargruppen() {
        return seminargruppeService.findeAlleSeminargruppen();
    }

    @PostMapping("/seminargruppe")
    public ResponseEntity<Seminargruppe> createSeminargruppe(@RequestBody Seminargruppe seminargruppe,
                                                             @RequestParam int studiengangId) {
        Seminargruppe neueSeminargruppe = seminargruppeService.erstelleSeminargruppe(seminargruppe, studiengangId);
        return ResponseEntity.ok(neueSeminargruppe);
    }

    @DeleteMapping("/seminargruppe/{id}")
    public ResponseEntity<String> deleteSeminargruppe(@PathVariable int id) {
        seminargruppeService.loescheSeminargruppe(id);
        return ResponseEntity.ok("Seminargruppe erfolgreich gelöscht.");
    }

    @GetMapping("/seminargruppe/{id}")
    public ResponseEntity<Seminargruppe> getSeminargruppeById(@PathVariable int id) {
        Optional<Seminargruppe> seminargruppe = seminargruppeService.findeSeminargruppeById(id);
        return seminargruppe.map(ResponseEntity::ok)
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @GetMapping("/seminargruppen/byStudiengang")
    public ResponseEntity<List<Seminargruppe>> getSeminargruppenByStudiengang(@RequestParam int studiengangId) {
        List<Seminargruppe> seminargruppen = seminargruppeService.findeSeminargruppenByStudiengang(studiengangId);
        return ResponseEntity.ok(seminargruppen);
    }

    @PostMapping("/kalendereintraege")
    public ResponseEntity<?> createKalendereintrag(
            @RequestParam String beschreibung,
            @RequestParam String eintragsdatum,
            @RequestParam String eventType,
            @RequestParam String startTime,
            @RequestParam String endTime,
            @RequestParam int kalenderId,
            @RequestParam int studentId
    ) {
        try {
            LocalDate date = LocalDate.parse(eintragsdatum);
            LocalTime start = LocalTime.parse(startTime);
            LocalTime end = LocalTime.parse(endTime);

            Optional<Kalender> kalenderOpt = kalenderService.findeKalenderById(kalenderId);
            if (kalenderOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Kalender nicht gefunden.");
            }

            Optional<Student> studentOpt = studentService.findeStudentById(studentId);
            if (studentOpt.isEmpty()) {
                return ResponseEntity.badRequest().body("Student nicht gefunden.");
            }

            Kalendereintrag eintrag = new Kalendereintrag();
            eintrag.setBeschreibung(beschreibung);
            eintrag.setEintragsdatum(date);
            eintrag.setEventType(Enum.valueOf(EventType.class, eventType));
            eintrag.setStartTime(start);
            eintrag.setEndTime(end);
            eintrag.setKalender(kalenderOpt.get());
            eintrag.setStudent(studentOpt.get());

            Kalendereintrag gespeicherterEintrag = kalenderService.eintragenKalendereintrag(eintrag);

            return ResponseEntity.ok(gespeicherterEintrag);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Erstellen des Kalendereintrags: " + e.getMessage());
        }
    }

    @DeleteMapping("/kalendereintraege/{id}")
    public ResponseEntity<?> deleteKalendereintrag(@PathVariable int id) {
        try {
            kalenderService.loescheKalendereintrag(id);
            return ResponseEntity.ok("Kalendereintrag erfolgreich gelöscht.");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Fehler beim Löschen des Kalendereintrags: " + e.getMessage());
        }
    }
}
