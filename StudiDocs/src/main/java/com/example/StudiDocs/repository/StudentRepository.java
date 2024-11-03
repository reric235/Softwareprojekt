package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;


public interface StudentRepository extends JpaRepository<Student, Long> {
}