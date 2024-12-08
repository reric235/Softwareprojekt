package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.repository.StudiengangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudiengangService {

    private final StudiengangRepository studiengangRepository;

    @Autowired
    public StudiengangService(StudiengangRepository studiengangRepository) {
        this.studiengangRepository = studiengangRepository;
    }

    /**
     * Gibt alle Studiengänge zurück.
     *
     * @return Liste aller Studiengänge.
     */
    public List<Studiengang> findeAlleStudiengaenge() {
        return studiengangRepository.findAll();
    }

    /**
     * Sucht einen Studiengang anhand seiner ID.
     *
     * @param id Die ID des Studiengangs.
     * @return Optional mit dem gefundenen Studiengang.
     */
    public Optional<Studiengang> findeStudiengangById(int id) {
        return studiengangRepository.findById(id);
    }

    /**
     * Erstellt einen neuen Studiengang.
     *
     * @param studiengang Der zu erstellende Studiengang.
     * @return Der gespeicherte Studiengang.
     */
    public Studiengang erstelleStudiengang(Studiengang studiengang) {
        return studiengangRepository.save(studiengang);
    }

    /**
     * Löscht einen Studiengang anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Studiengangs.
     */
    public void loescheStudiengang(int id) {
        if (!studiengangRepository.existsById(id)) {
            throw new IllegalArgumentException("Studiengang mit der ID " + id + " existiert nicht.");
        }
        studiengangRepository.deleteById(id);
    }
}
