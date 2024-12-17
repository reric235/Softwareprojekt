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

    public Modul saveModul(Modul modul) {
        return modulRepository.save(modul);
    }

    public Optional<Modul> findeModulById(int modulId) {
        return modulRepository.findById(modulId);
    }

    public List<Modul> findBySemester(int semester) {
        return modulRepository.findBySemester(semester);
    }

    public List<Modul> findModulesByStudiengangAndSemester(int studiengangId, int semester) {
        return modulRepository.findByStudiengang_StudiengangIdAndSemester(studiengangId, semester);
    }
}
