package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {

    // Methode zum Finden eines Studenten anhand der E-Mail
    Optional<Student> findByEmail(String email);
    // Speichere einen neuen oder aktualisierten Studenten
    @Override
    Student save(Student student);


}
