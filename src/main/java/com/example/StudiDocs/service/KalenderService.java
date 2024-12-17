// src/main/java/com/example/StudiDocs/service/KalenderService.java

package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Kalender;
import com.example.StudiDocs.model.Kalendereintrag;
import com.example.StudiDocs.repository.KalenderRepository;
import com.example.StudiDocs.repository.KalendereintragRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class KalenderService {

    private final KalenderRepository kalenderRepository;
    private final KalendereintragRepository kalendereintragRepository;

    @Autowired
    public KalenderService(KalenderRepository kalenderRepository, KalendereintragRepository kalendereintragRepository) {
        this.kalenderRepository = kalenderRepository;
        this.kalendereintragRepository = kalendereintragRepository;
    }

    public Kalender saveKalender(Kalender kalender) {
        return kalenderRepository.save(kalender);
    }

    @Transactional
    public Kalendereintrag eintragenKalendereintrag(Kalendereintrag kalendereintrag) {
        if (!kalenderRepository.existsById(kalendereintrag.getKalender().getKalenderId())) {
            throw new IllegalArgumentException("Kalender existiert nicht.");
        }

        if (kalendereintrag.getStudent() == null || kalendereintrag.getStudent().getStudentId() == 0) {
            throw new IllegalArgumentException("Ungültige Student-ID.");
        }

        // Weitere Validierungen

        return kalendereintragRepository.save(kalendereintrag);
    }

    public void loescheKalendereintrag(int kalendereintragId) {
        if (kalendereintragRepository.existsById(kalendereintragId)) {
            kalendereintragRepository.deleteById(kalendereintragId);
        } else {
            throw new IllegalArgumentException("Kalendereintrag mit der ID " + kalendereintragId + " existiert nicht.");
        }
    }

    public List<Kalendereintrag> findeKalendereintraegeByKalender(int kalenderId) {
        return kalendereintragRepository.findByKalenderKalenderId(kalenderId);
    }

    public Optional<Kalender> findeKalenderBySeminargruppe(int seminargruppeId) {
        return Optional.ofNullable(kalenderRepository.findBySeminargruppeSeminargruppeId(seminargruppeId));
    }

    public Optional<Kalender> findeKalenderById(int kalenderId) {
        return kalenderRepository.findById(kalenderId);
    }

    public List<Kalendereintrag> filterKalendereintraegeByEventType(int kalenderId, String eventType) {
        List<Kalendereintrag> eintraege = findeKalendereintraegeByKalender(kalenderId);
        return eintraege.stream()
                .filter(eintrag -> eintrag.getEventType().name().equals(eventType))
                .toList();
    }
}
