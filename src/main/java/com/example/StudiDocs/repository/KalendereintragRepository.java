package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Kalendereintrag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface KalendereintragRepository extends JpaRepository<Kalendereintrag, Integer> {
    List<Kalendereintrag> findByKalenderKalenderId(int kalenderId);
    List<Kalendereintrag> findByStudentStudentId(int studentId);
    List<Kalendereintrag> findByEintragsdatum(Date eintragsdatum);
    @Override
    Kalendereintrag save(Kalendereintrag kalendereintrag);
}
