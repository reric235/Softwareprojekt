package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Studiengang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StudiengangRepository extends JpaRepository<Studiengang, Integer> {
}
