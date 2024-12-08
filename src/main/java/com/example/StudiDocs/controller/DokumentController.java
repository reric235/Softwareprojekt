package com.example.StudiDocs.controller;

import com.example.StudiDocs.model.Dokument;
import com.example.StudiDocs.model.Modul;
import com.example.StudiDocs.model.Student;
import com.example.StudiDocs.service.DokumentService;
import com.example.StudiDocs.service.ModulService;
import com.example.StudiDocs.service.StudentService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/dokumente")
public class DokumentController {

    private final DokumentService dokumentService;
    private final StudentService studentService;
    private final ModulService modulService;

    @Autowired
    public DokumentController(DokumentService dokumentService, StudentService studentService, ModulService modulService) {
        this.dokumentService = dokumentService;
        this.studentService = studentService;
        this.modulService = modulService;
    }

    @PostMapping("/upload")
    public ResponseEntity<String> hochladenDokument(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "modulId", required = false) Integer modulId,
            Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Student> studentOpt = studentService.findeStudentByEmail(email);
            if (!studentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student nicht gefunden.");
            }
            Student student = studentOpt.get();

            Modul modul = null;
            if (modulId != null) {
                Optional<Modul> modulOpt = modulService.findeModulById(modulId);
                if (modulOpt.isPresent()) {
                    modul = modulOpt.get();
                } else {
                    return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Modul nicht gefunden.");
                }
            }

            dokumentService.hochladenDokument(file, student, modul);
            return ResponseEntity.status(HttpStatus.CREATED).body("Dokument erfolgreich hochgeladen.");
        } catch (IllegalArgumentException | IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @DeleteMapping("/{dokumentId}")
    public ResponseEntity<String> loescheDokument(@PathVariable int dokumentId, Authentication authentication) {
        try {
            String email = authentication.getName();
            Optional<Student> studentOpt = studentService.findeStudentByEmail(email);
            if (!studentOpt.isPresent()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Student nicht gefunden.");
            }
            Student student = studentOpt.get();

            dokumentService.loescheDokument(dokumentId, student);
            return ResponseEntity.ok("Dokument erfolgreich gelöscht.");
        } catch (IllegalArgumentException | IOException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }

    @GetMapping("/modul/{modulId}")
    public ResponseEntity<List<Dokument>> findeDokumenteByModul(
            @PathVariable int modulId,
            @RequestParam(value = "sort", required = false) String sortParam) {
        try {
            List<Dokument> dokumente = dokumentService.findeDokumenteByModulSortiert(modulId, sortParam);
            return ResponseEntity.ok(dokumente);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @GetMapping("/download/{dokumentId}")
    public ResponseEntity<Resource> downloadDokument(@PathVariable int dokumentId) {
        Optional<Dokument> optionalDokument = dokumentService.findById(dokumentId);
        if (!optionalDokument.isPresent()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Dokument dokument = optionalDokument.get();
        Path filePath = Paths.get(dokument.getFilePath());
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (!resource.exists() || !resource.isReadable()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }

            String filename = dokument.getName();

            return ResponseEntity.ok()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .header(HttpHeaders.CONTENT_TYPE, "application/octet-stream")
                    .body(resource);
        } catch (MalformedURLException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}