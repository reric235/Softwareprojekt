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

        Kalender kalender = new Kalender();
        kalender.setSeminargruppe(savedSeminargruppe);
        kalenderService.saveKalender(kalender);

        return savedSeminargruppe;
    }

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

    public List<Seminargruppe> findeAlleSeminargruppen() {
        return seminargruppeRepository.findAll();
    }

    public void loescheSeminargruppe(int seminargruppeId) {
        if (!seminargruppeRepository.existsById(seminargruppeId)) {
            throw new IllegalArgumentException("Seminargruppe mit der ID " + seminargruppeId + " existiert nicht.");
        }
        seminargruppeRepository.deleteById(seminargruppeId);
    }
}
