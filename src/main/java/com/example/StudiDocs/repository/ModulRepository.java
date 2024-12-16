package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Modul;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface ModulRepository extends JpaRepository<Modul, Integer> {

    List<Modul> findByStudiengang_StudiengangIdAndSemester(int studiengangId, int semester);
    List<Modul> findByModulname(String modulname);
    List<Modul> findBySemester(int semester);
    Modul save(Modul modul);
}
