package com.example.StudiDocs.service;

import com.example.StudiDocs.model.Dokument;
import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.model.Rolle;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.repository.DokumentRepository;
import org.apache.commons.io.FilenameUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.nio.file.*;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class DokumentService {

    @Value("${file.upload-dir}")
    private String uploadDir;

    private final DokumentRepository dokumentRepository;

    @Autowired
    public DokumentService(DokumentRepository dokumentRepository) {
        this.dokumentRepository = dokumentRepository;
    }

    public Dokument hochladenDokument(MultipartFile file, Student student, Modul modul) throws IOException {
        if (file.isEmpty()) {
            throw new IllegalArgumentException("Die Datei ist leer.");
        }

        long size = file.getSize();
        if (size > 2L * 1024 * 1024 * 1024) { // 2GB
            throw new IllegalArgumentException("Dokumentgröße überschreitet das erlaubte Limit von 2GB.");
        }

        Path uploadPath = Paths.get(uploadDir).toAbsolutePath().normalize();
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = FilenameUtils.getName(file.getOriginalFilename());
        String fileName = UUID.randomUUID().toString() + "_" + originalFilename;
        Path filePath = uploadPath.resolve(fileName);

        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

        Dokument dokument = new Dokument();
        dokument.setName(originalFilename);
        dokument.setUploadDatum(new Date());
        dokument.setSize(size);
        dokument.setFilePath(filePath.toString());
        dokument.setStudent(student);
        dokument.setModul(modul);

        return dokumentRepository.save(dokument);
    }

    public void loescheDokument(int dokumentId, Student currentUser) throws IOException {
        Optional<Dokument> optionalDokument = dokumentRepository.findById(dokumentId);
        if (optionalDokument.isPresent()) {
            Dokument dokument = optionalDokument.get();

            boolean isAdmin = currentUser.getRolle() == Rolle.ADMIN;
            boolean isOwner = dokument.getStudent().getEmail().equalsIgnoreCase(currentUser.getEmail());

            if (isAdmin || isOwner) {
                // Absoluter Pfad zum Ordner "resources/Files"
                Path resourcesDirectory = Paths.get("src", "main", "resources", "Files").toAbsolutePath();
                Path filePath = resourcesDirectory.resolve(dokument.getFilePath());

                // Datei löschen
                Files.deleteIfExists(filePath);

                // Dokument aus der Datenbank löschen
                dokumentRepository.deleteById(dokumentId);
            } else {
                throw new IllegalArgumentException("Sie sind nicht berechtigt, dieses Dokument zu löschen.");
            }
        } else {
            throw new IllegalArgumentException("Dokument mit der ID " + dokumentId + " existiert nicht.");
        }
    }



    public List<Dokument> findeDokumenteByModulSortiert(int modulId, String sortParam) {
        Sort sort = Sort.unsorted();
        if (sortParam != null && !sortParam.isEmpty()) {
            String[] parts = sortParam.split(",");
            String field = parts[0];
            Sort.Direction direction = Sort.Direction.ASC;
            if (parts.length > 1 && parts[1].equalsIgnoreCase("desc")) {
                direction = Sort.Direction.DESC;
            }
            sort = Sort.by(direction, field);
        }
        return dokumentRepository.findByModulModulId(modulId, sort);
    }

    public Optional<Dokument> findById(int dokumentId) {
        return dokumentRepository.findById(dokumentId);
    }
}
