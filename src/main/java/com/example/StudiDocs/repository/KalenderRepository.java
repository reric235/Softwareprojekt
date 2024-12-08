package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Kalender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KalenderRepository extends JpaRepository<Kalender, Integer> {

    // Methode zum Finden eines Kalenders anhand der Seminargruppen-ID
    Kalender findBySeminargruppeSeminargruppeId(int seminargruppeId);

    // Speichern eines neuen oder aktualisierten Kalenders
    @Override
    Kalender save(Kalender kalender);
}
