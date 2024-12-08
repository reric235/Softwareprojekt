package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.model.Seminargruppe;
import com.example.StudiDocs.model.Studiengang;
import com.example.StudiDocs.repository.ModulRepository;
import com.example.StudiDocs.repository.SeminargruppeRepository;
import com.example.StudiDocs.repository.StudiengangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AdminService {

    private final ModulRepository modulRepository;
    private final SeminargruppeRepository seminargruppeRepository;
    private final StudiengangRepository studiengangRepository;

    @Autowired
    public AdminService(ModulRepository modulRepository, SeminargruppeRepository seminargruppeRepository, StudiengangRepository studiengangRepository) {
        this.modulRepository = modulRepository;
        this.seminargruppeRepository = seminargruppeRepository;
        this.studiengangRepository = studiengangRepository;
    }

    /**
     * Erstellt ein neues Modul und speichert es in der Datenbank.
     *
     * @param modul Das Modul, das erstellt werden soll.
     * @return Das erstellte Modul.
     */
    public Modul createModul(Modul modul) {
        if (modul.getModulname() == null || modul.getModulname().isEmpty()) {
            throw new IllegalArgumentException("Der Modulname darf nicht leer sein.");
        }
        if (modul.getSemester() <= 0) {
            throw new IllegalArgumentException("Das Semester muss größer als 0 sein.");
        }
        return modulRepository.save(modul);
    }

    /**
     * Löscht ein Modul anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Moduls.
     */
    public void deleteModul(int id) {
        if (!modulRepository.existsById(id)) {
            throw new IllegalArgumentException("Modul mit der ID " + id + " existiert nicht.");
        }
        modulRepository.deleteById(id);
    }

    /**
     * Erstellt eine neue Seminargruppe und speichert sie in der Datenbank.
     *
     * @param seminargruppe Die Seminargruppe, die erstellt werden soll.
     * @param studiengangId Die ID des zugehörigen Studiengangs.
     * @return Die erstellte Seminargruppe.
     */
    public Seminargruppe createSeminargruppe(Seminargruppe seminargruppe, int studiengangId) {
        Optional<Studiengang> studiengangOptional = studiengangRepository.findById(studiengangId);
        if (studiengangOptional.isEmpty()) {
            throw new IllegalArgumentException("Studiengang mit der ID " + studiengangId + " existiert nicht.");
        }
        seminargruppe.setStudiengang(studiengangOptional.get());

        return seminargruppeRepository.save(seminargruppe);
    }

    /**
     * Löscht eine Seminargruppe anhand ihrer ID.
     *
     * @param id Die ID der zu löschenden Seminargruppe.
     */
    public void deleteSeminargruppe(int id) {
        if (!seminargruppeRepository.existsById(id)) {
            throw new IllegalArgumentException("Seminargruppe mit der ID " + id + " existiert nicht.");
        }
        seminargruppeRepository.deleteById(id);
    }

    /**
     * Gibt eine Liste aller Module zurück.
     *
     * @return Liste aller Module.
     */
    public List<Modul> getAllModule() {
        return modulRepository.findAll();
    }

    /**
     * Gibt eine Liste aller Seminargruppen zurück.
     *
     * @return Liste aller Seminargruppen.
     */
    public List<Seminargruppe> getAllSeminargruppen() {
        return seminargruppeRepository.findAll();
    }

    /**
     * Gibt eine Liste aller Studiengänge zurück.
     *
     * @return Liste aller Studiengänge.
     */
    public List<Studiengang> getAllStudiengaenge() {
        return studiengangRepository.findAll();
    }

    /**
     * Erstellt einen neuen Studiengang.
     *
     * @param studiengang Der Studiengang, der erstellt werden soll.
     * @return Der erstellte Studiengang.
     */
    public Studiengang createStudiengang(Studiengang studiengang) {
        if (studiengang.getName() == null || studiengang.getName().isEmpty()) {
            throw new IllegalArgumentException("Der Name des Studiengangs darf nicht leer sein.");
        }
        return studiengangRepository.save(studiengang);
    }

    /**
     * Löscht einen Studiengang anhand seiner ID.
     *
     * @param id Die ID des zu löschenden Studiengangs.
     */
    public void deleteStudiengang(int id) {
        if (!studiengangRepository.existsById(id)) {
            throw new IllegalArgumentException("Studiengang mit der ID " + id + " existiert nicht.");
        }
        studiengangRepository.deleteById(id);
    }
}
