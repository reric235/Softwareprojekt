package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Studiengang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface SeminargruppeRepository extends JpaRepository<Seminargruppe, Integer> {

    // Methode zum Finden einer Seminargruppe anhand ihres Namens
    Optional<Seminargruppe> findByName(String name);

    // Methode zum Finden von Seminargruppen basierend auf dem Studiengang
    List<Seminargruppe> findByStudiengang(Studiengang studiengang);  // Korrektur
    List<Seminargruppe> findByStudiengangStudiengangId(int studiengangId);

    // Alle Seminargruppen abrufen
    List<Seminargruppe> findAll();
}
