package com.example.StudiDocs.repository;

import com.example.StudiDocs.model.Modul;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModulRepository extends JpaRepository<Modul, Integer> {

    /**
     * Finden eines Moduls basierend auf dem Studiengang und Semester.
     *
     * @param studiengang Der Studiengang, dem das Modul zugeordnet ist.
     * @param semester Das Semester, in dem das Modul angeboten wird.
     * @return Eine Liste der Module, die dem Studiengang und Semester entsprechen.
     */
    List<Modul> findByStudiengangAndSemester(String studiengang, int semester);

    /**
     * Finden eines Moduls anhand seines Namens.
     *
     * @param modulname Der Name des Moduls.
     * @return Das Modul mit dem angegebenen Namen.
     */
    List<Modul> findByModulname(String modulname);

    /**
     * Überprüfen, ob ein Modul mit einem bestimmten Namen existiert.
     *
     * @param modulname Der Name des Moduls.
     * @return true, wenn ein Modul mit diesem Namen existiert, false andernfalls.
     */
    boolean existsByModulname(String modulname);

    List<Modul> findBySemester(int semester);
    /**
     * Speichern eines neuen oder aktualisierten Moduls.
     *
     * @param modul Das zu speichernde Modul.
     * @return Das gespeicherte Modul.
     */
    @Override
    Modul save(Modul modul);
}
