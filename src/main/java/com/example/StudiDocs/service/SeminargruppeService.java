package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Kalender;
import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.repository.SeminargruppeRepository;
import com.example.StudiDocs.repository.StudiengangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class SeminargruppeService {

    private final SeminargruppeRepository seminargruppeRepository;
    private final StudiengangRepository studiengangRepository;
    private final KalenderService kalenderService;

    @Autowired
    public SeminargruppeService(SeminargruppeRepository seminargruppeRepository, StudiengangRepository studiengangRepository, KalenderService kalenderService) {
        this.seminargruppeRepository = seminargruppeRepository;
        this.studiengangRepository = studiengangRepository;
        this.kalenderService = kalenderService;
    }

    @Transactional
    public Seminargruppe erstelleSeminargruppe(Seminargruppe seminargruppe, int studiengangId) {
        Optional<Studiengang> studiengangOptional = studiengangRepository.findById(studiengangId);
        if (studiengangOptional.isEmpty()) {
            throw new IllegalArgumentException("Studiengang mit der ID " + studiengangId + " existiert nicht.");
        }

        seminargruppe.setStudiengang(studiengangOptional.get());
        Seminargruppe savedSeminargruppe = seminargruppeRepository.save(seminargruppe);

        // Kalender erstellen und speichern
        Kalender kalender = new Kalender();
        kalender.setSeminargruppe(savedSeminargruppe);
        kalenderService.saveKalender(kalender);

        return savedSeminargruppe;
    }

    /**
     * Findet eine Seminargruppe anhand ihres Namens.
     *
     * @param name Der Name der Seminargruppe.
     * @return Optional mit der gefundenen Seminargruppe.
     */
    public Optional<Seminargruppe> findeSeminargruppeByName(String name) {
        return seminargruppeRepository.findByName(name);
    }

    /**
     * Sucht alle Seminargruppen, die zu einem bestimmten Studiengang gehören.
     *
     * @param studiengangId Die ID des Studiengangs.
     * @return Liste der Seminargruppen.
     */
    public List<Seminargruppe> findeSeminargruppenByStudiengang(int studiengangId) {
        Optional<Studiengang> studiengangOptional = studiengangRepository.findById(studiengangId);
        if (studiengangOptional.isEmpty()) {
            throw new IllegalArgumentException("Studiengang mit der ID " + studiengangId + " existiert nicht.");
        }
        return seminargruppeRepository.findByStudiengang(studiengangOptional.get());
    }
    public Optional<Seminargruppe> findeSeminargruppeById(int id) {
        return seminargruppeRepository.findById(id);
    }

    /**
     * Gibt alle Seminargruppen zurück.
     *
     * @return Liste aller Seminargruppen.
     */
    public List<Seminargruppe> findeAlleSeminargruppen() {
        return seminargruppeRepository.findAll();
    }

    /**
     * Aktualisiert eine bestehende Seminargruppe.
     *
     * @param seminargruppe Die Seminargruppe mit aktualisierten Daten.
     * @return Die aktualisierte Seminargruppe.
     */
    public Seminargruppe aktualisiereSeminargruppe(Seminargruppe seminargruppe) {
        if (!seminargruppeRepository.existsById(seminargruppe.getSeminargruppeId())) {
            throw new IllegalArgumentException("Seminargruppe mit der ID " + seminargruppe.getSeminargruppeId() + " existiert nicht.");
        }
        return seminargruppeRepository.save(seminargruppe);
    }

    /**
     * Löscht eine Seminargruppe anhand ihrer ID.
     *
     * @param seminargruppeId Die ID der zu löschenden Seminargruppe.
     */
    public void loescheSeminargruppe(int seminargruppeId) {
        if (!seminargruppeRepository.existsById(seminargruppeId)) {
            throw new IllegalArgumentException("Seminargruppe mit der ID " + seminargruppeId + " existiert nicht.");
        }
        seminargruppeRepository.deleteById(seminargruppeId);
    }


}
