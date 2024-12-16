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

    @Transactional
    public Student registriereStudent(Student student) {
        if (studentRepository.findByEmail(student.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Ein Student mit dieser E-Mail existiert bereits.");
        }
        String email = student.getEmail();
        if (email == null || !email.endsWith("@ba-sachsen.de")) {
            throw new IllegalArgumentException("Die E-Mail-Adresse muss auf '@ba-sachsen.de' enden.");
        }
        student.setPasswort(passwordEncoder.encode(student.getPasswort()));

        if (student.getRolle() == null) {
            student.setRolle(Rolle.STUDENT);
        }

        student.setVerifiziert(true);

        return studentRepository.save(student);
    }

    public Optional<Student> findStudentByDokument(int dokumentId) {
        return dokumentRepository.findById(dokumentId)
                .map(Dokument::getStudent);
    }

    public Optional<Student> login(String email, String passwort) {
        return studentRepository.findByEmail(email)
                .filter(student -> passwordEncoder.matches(passwort, student.getPasswort()));
    }

    public Optional<Student> findeStudentById(int studentId) {
        return studentRepository.findById(studentId);
    }

    public List<Student> findeAlleStudenten() {
        return studentRepository.findAll();
    }

    public Optional<Student> findeStudentByEmail(String email) {
        return studentRepository.findByEmail(email);
    }

    @Transactional
    public Student aktualisiereStudent(Student student) {
        if (!studentRepository.existsById(student.getStudentId())) {
            throw new IllegalArgumentException("Student existiert nicht.");
        }

        if (student.getPasswort() != null) {
            student.setPasswort(passwordEncoder.encode(student.getPasswort()));
        }

        return studentRepository.save(student);
    }

    public boolean istPasswortGueltig(String passwort) {
        return passwort.length() >= 8;
    }

    @Transactional
    public boolean loescheStudent(int studentId) {
        if (studentRepository.existsById(studentId)) {
            studentRepository.deleteById(studentId);
            return true;
        }
        return false;
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