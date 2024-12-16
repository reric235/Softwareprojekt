package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Kalender;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface KalenderRepository extends JpaRepository<Kalender, Integer> {
    Kalender findBySeminargruppeSeminargruppeId(int seminargruppeId);
    @Override
    Kalender save(Kalender kalender);
}
