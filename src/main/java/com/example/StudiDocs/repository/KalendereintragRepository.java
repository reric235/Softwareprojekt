package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Kalendereintrag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KalendereintragRepository extends JpaRepository<Kalendereintrag, Integer> {

    // Finden von Kalendereinträgen für ein bestimmtes Kalender anhand der Kalender-ID
    List<Kalendereintrag> findByKalenderKalenderId(int kalenderId);

    // Finden von Kalendereinträgen eines bestimmten Studenten anhand der Student-ID
    List<Kalendereintrag> findByStudentStudentId(int studentId);

    // Finden von Kalendereinträgen basierend auf einem bestimmten Datum
    List<Kalendereintrag> findByEintragsdatum(Date eintragsdatum);

    // Speichern eines neuen oder aktualisierten Kalendereintrags
    @Override
    Kalendereintrag save(Kalendereintrag kalendereintrag);
}
