package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Dokument;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface DokumentRepository extends JpaRepository<Dokument, Integer> {
    Optional<Dokument> findById(int id);
    List<Dokument> findByModulModulId(int modulId, Sort sort);
}
