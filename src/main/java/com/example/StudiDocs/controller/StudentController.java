package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Rolle;
import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.service.SeminargruppeService;
import com.example.StudiDocs.service.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;
    private final SeminargruppeService seminargruppeService;

    @Autowired
    public StudentController(StudentService studentService, SeminargruppeService seminargruppeService) {
        this.studentService = studentService;
        this.seminargruppeService = seminargruppeService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerStudent(@RequestBody Student student) {
        try {
            Integer seminargruppeId = student.getSeminargruppeId();
            if (seminargruppeId == null) {
                return ResponseEntity.badRequest().body("Seminargruppe muss ausgewählt werden.");
            }

            Seminargruppe seminargruppe = seminargruppeService.findeSeminargruppeById(seminargruppeId)
                    .orElseThrow(() -> new IllegalArgumentException("Die Seminargruppe mit ID '" + seminargruppeId + "' existiert nicht."));
            student.setSeminargruppe(seminargruppe);

            if (student.getRolle() == null) {
                student.setRolle(Rolle.STUDENT);
            }

            studentService.registriereStudent(student);

            return ResponseEntity.ok("Student erfolgreich registriert.");
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().body("Registrierung fehlgeschlagen: " + e.getMessage());
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Registrierung fehlgeschlagen: " + e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<Iterable<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.findeAlleStudenten());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Student> getStudentById(@PathVariable int id) {
        return studentService.findeStudentById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<Student> getStudentByEmail(@PathVariable String email) {
        return studentService.findeStudentByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Student> updateStudent(@PathVariable int id, @RequestBody Student student) {
        Optional<Student> optionalStudent = studentService.findeStudentById(id);
        if (optionalStudent.isPresent()) {
            Student existingStudent = optionalStudent.get();
            existingStudent.setVorname(student.getVorname());
            existingStudent.setNachname(student.getNachname());
            existingStudent.setEmail(student.getEmail());
            existingStudent.setPasswort(student.getPasswort());

            Integer seminargruppeId = student.getSeminargruppeId();
            if (seminargruppeId != null) {
                Seminargruppe seminargruppe = seminargruppeService.findeSeminargruppeById(seminargruppeId)
                        .orElseThrow(() -> new IllegalArgumentException("Die Seminargruppe mit ID '" + seminargruppeId + "' existiert nicht."));
                existingStudent.setSeminargruppe(seminargruppe);
            }

            return ResponseEntity.ok(studentService.aktualisiereStudent(existingStudent));
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteStudent(@PathVariable int id) {
        if (studentService.loescheStudent(id)) {
            return ResponseEntity.ok("Student erfolgreich gelöscht.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/by-dokument/{dokumentId}")
    public ResponseEntity<Map<String, String>> getStudentByDokument(@PathVariable int dokumentId) {
        return studentService.findStudentByDokument(dokumentId)
                .map(student -> {
                    Map<String, String> studentInfo = new HashMap<>();
                    studentInfo.put("vorname", student.getVorname());
                    studentInfo.put("nachname", student.getNachname());
                    studentInfo.put("email", student.getEmail());
                    return ResponseEntity.ok(studentInfo);
                })
                .orElseGet(() -> ResponseEntity.notFound().build());
    }

    @DeleteMapping("/by-email/{email}")
    public ResponseEntity<String> deleteStudentByEmail(@PathVariable String email) {
        if (studentService.loescheStudentByEmail(email)) {
            return ResponseEntity.ok("Student mit E-Mail " + email + " erfolgreich gelöscht.");
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/current")
    public ResponseEntity<Map<String, String>> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = authentication.getName();
        Optional<Student> opt = studentService.findeStudentByEmail(email);
        if (!opt.isPresent()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Student s = opt.get();
        Map<String, String> out = new HashMap<>();
        out.put("email", s.getEmail());
        out.put("rolle", s.getRolle().name());
        return ResponseEntity.ok(out);
    }
}