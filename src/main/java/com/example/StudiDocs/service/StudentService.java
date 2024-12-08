package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Rolle;
import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.repository.DokumentRepository;
import com.example.StudiDocs.repository.SeminargruppeRepository;
import com.example.StudiDocs.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.StudiDocs.model.Dokument;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {


    @Autowired
    private SeminargruppeRepository seminargruppeRepository;
    private final StudentRepository studentRepository;
    private final PasswordEncoder passwordEncoder;
    private final DokumentRepository dokumentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository, PasswordEncoder passwordEncoder, DokumentRepository dokumentRepository) {
        this.studentRepository = studentRepository;
        this.passwordEncoder = passwordEncoder;
        this.dokumentRepository = dokumentRepository;
    }

    /**
     * Registriert einen neuen Studenten und speichert ihn in der Datenbank.
     * Das Passwort wird vor der Speicherung gehasht.
     *
     * @param student Der zu registrierende Student.
     * @return Der gespeicherte Student.
     */
    @Transactional
    public Student registriereStudent(Student student) {
        // Überprüfen, ob E-Mail bereits existiert
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ein Student mit dieser E-Mail existiert bereits.");
        }

        String email = student.getEmail();
        if (email == null || !email.endsWith("@ba-sachsen.de")) {
            throw new IllegalArgumentException("Die E-Mail-Adresse muss auf '@ba-sachsen.de' enden.");
        }

        // Passwort hashen
        student.setPasswort(passwordEncoder.encode(student.getPasswort()));

        // Standardmäßig wird ein neuer Student als unverifiziert markiert


        // Setze Standardwert für Rolle, falls nicht gesetzt
        if (student.getRolle() == null) {
            student.setRolle(Rolle.STUDENT);
        }

        student.setVerifiziert(true);

        // Student speichern
        return studentRepository.save(student);
    }

    public Optional<Student> findStudentByDokument(int dokumentId) {
        return dokumentRepository.findById(dokumentId)
                .map(Dokument::getStudent); // Holt den Studenten des Dokuments
    }

    /**
     * Überprüft die Login-Daten eines Studenten.
     *
     * @param email    Die E-Mail des Studenten.
     * @param passwort Das eingegebene Passwort.
     * @return Optional mit dem Studenten, falls die Anmeldedaten korrekt sind.
     */
    public Optional<Student> login(String email, String passwort) {
        return studentRepository.findByEmail(email)
                .filter(student -> passwordEncoder.matches(passwort, student.getPasswort()));
    }

    /**
     * Verifiziert die E-Mail eines Studenten und speichert die Änderung.
     *
     * @param studentId Die ID des zu verifizierenden Studenten.
     * @return true, wenn der Student verifiziert wurde, sonst false.
     */
    @Transactional
    public boolean verifiziereEmail(int studentId) {
        Optional<Student> optionalStudent = studentRepository.findById(studentId);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            student.setVerifiziert(true);
            studentRepository.save(student);
            return true;
        }
        return false;
    }

    /**
     * Findet einen Studenten anhand seiner ID.
     *
     * @param studentId Die ID des Studenten.
     * @return Optional mit dem gefundenen Studenten.
     */
    public Optional<Student> findeStudentById(int studentId) {
        return studentRepository.findById(studentId);
    }

    /**
     * Findet alle Studenten.
     *
     * @return Liste aller Studenten.
     */
    public List<Student> findeAlleStudenten() {
        return studentRepository.findAll();
    }

    /**
     * Findet einen Studenten anhand seiner E-Mail.
     *
     * @param email Die E-Mail des Studenten.
     * @return Optional mit dem gefundenen Studenten.
     */
    public Optional<Student> findeStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    /**
     * Aktualisiert die Daten eines Studenten.
     *
     * @param student Der zu aktualisierende Student.
     * @return Der aktualisierte Student.
     */
    @Transactional
    public Student aktualisiereStudent(Student student) {
        // Überprüfung: Existiert der Student bereits?
        if (!studentRepository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException("Student existiert nicht.");
        }

        // Falls Passwort aktualisiert wird, erneut hashen
        if (student.getPasswort() != null) {
            student.setPasswort(passwordEncoder.encode(student.getPasswort()));
        }

        return studentRepository.save(student);
    }

    /**
     * Validiert, ob ein Passwort den Sicherheitsrichtlinien entspricht.
     * Beispiel: Mindestlänge, Zahlen, Sonderzeichen.
     *
     * @param passwort Das zu überprüfende Passwort.
     * @return true, wenn das Passwort gültig ist, sonst false.
     */
    public boolean istPasswortGueltig(String passwort) {
        return passwort.length() >= 8; // Beispiel: Passwort muss mindestens 8 Zeichen lang sein
    }

    /**
     * Speichert oder aktualisiert einen Studenten.
     *
     * @param student Der zu speichernde oder zu aktualisierende Student.
     * @return Der gespeicherte Student.
     */
    @Transactional
    public Student speichereStudent(Student student) {
        return studentRepository.save(student);
    }

    /**
     * Löscht einen Studenten anhand seiner ID.
     *
     * @param studentId Die ID des zu löschenden Studenten.
     * @return true, wenn der Student erfolgreich gelöscht wurde, sonst false.
     */
    @Transactional
    public boolean loescheStudent(int studentId) {
        if (studentRepository.existsById(studentId)) {
            studentRepository.deleteById(studentId);
            return true;
        }
        return false;
    }

    /**
     * Findet eine Seminargruppe anhand ihrer ID.
     *
     * @param id Die ID der Seminargruppe.
     * @return Optional der gefundenen Seminargruppe.
     */
    public Optional<Seminargruppe> findeSeminargruppeById(Integer id) {
        return seminargruppeRepository.findById(id);
    }

    @Transactional
    public boolean loescheStudentByEmail(String email) {
        Optional<Student> optionalStudent = studentRepository.findByEmail(email);
        if (optionalStudent.isPresent()) {
            Student student = optionalStudent.get();
            studentRepository.delete(student);
            return true;
        }
        return false;
    }

}
