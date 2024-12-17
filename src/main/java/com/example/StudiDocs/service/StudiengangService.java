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

    public List<Studiengang> findeAlleStudiengaenge() {
        return studiengangRepository.findAll();
    }

    public Studiengang erstelleStudiengang(Studiengang studiengang) {
        return studiengangRepository.save(studiengang);
    }

    public void loescheStudiengang(int id) {
        if (!studiengangRepository.existsById(id)) {
            throw new IllegalArgumentException("Studiengang mit der ID " + id + " existiert nicht.");
        }
        studiengangRepository.deleteById(id);
    }
}
