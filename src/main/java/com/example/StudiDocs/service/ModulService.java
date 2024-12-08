package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.repository.ModulRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ModulService {

    private final ModulRepository modulRepository;

    @Autowired
    public ModulService(ModulRepository modulRepository) {
        this.modulRepository = modulRepository;
    }

    /**
     * Gibt alle Module zurück.
     *
     * @return Liste aller Module.
     */
    public List<Modul> findAll() {
        return modulRepository.findAll();
    }

    // Finden eines Moduls basierend auf Studiengang und Semester
    public List<Modul> findByStudiengangUndSemester(String studiengang, int semester) {
        return modulRepository.findByStudiengangAndSemester(studiengang, semester);
    }

    // Erstellen oder Aktualisieren eines Moduls
    public Modul saveModul(Modul modul) {
        return modulRepository.save(modul);
    }

    // Finden eines Moduls anhand der Modul-ID
    public Optional<Modul> findeModulById(int modulId) {
        return modulRepository.findById(modulId);
    }

    // Löschen eines Moduls basierend auf der Modul-ID
    public void loescheModul(int modulId) {
        if (modulRepository.existsById(modulId)) {
            modulRepository.deleteById(modulId);
        } else {
            throw new IllegalArgumentException("Modul mit der ID " + modulId + " existiert nicht.");
        }
    }

    /**
     * Findet Module für ein bestimmtes Semester.
     *
     * @param semester Das Semester.
     * @return Liste der Module.
     */
    public List<Modul> findBySemester(int semester) {
        return modulRepository.findBySemester(semester);
    }

    /**
     * Findet Module anhand des Modulnamens.
     *
     * @param modulname Der Modulname.
     * @return Liste der Module.
     */
    public List<Modul> findByModulname(String modulname) {
        return modulRepository.findByModulname(modulname);
    }
}
