package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "dokument")
public class Dokument {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dokumentId", nullable = false, updatable = false)
    private int dokumentId;

    @Column(nullable = false, length = 100)
    private String name;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(nullable = false)
    private Date uploadDatum;

    @Column(nullable = false)
    private long size;

    @Column(name = "file_path", nullable = false, length = 255)
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    @JsonBackReference(value = "student-dokument")
    private Student student;

    @ManyToOne
    @JoinColumn(name = "modulId")
    @JsonBackReference(value = "modul-dokumente")
    private Modul modul;

    public Dokument() {}

    public Dokument(String name, Date uploadDatum, long size, String filePath, Student student, Modul modul) {
        this.name = name;
        this.uploadDatum = uploadDatum;
        this.size = size;
        this.filePath = filePath;
        this.student = student;
        this.modul = modul;
    }

    public int getDokumentId() {
        return dokumentId;
    }

    public void setDokumentId(int dokumentId) {
        this.dokumentId = dokumentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Date getUploadDatum() {
        return uploadDatum;
    }

    public void setUploadDatum(Date uploadDatum) {
        this.uploadDatum = uploadDatum;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Modul getModul() {
        return modul;
    }

    public void setModul(Modul modul) {
        this.modul = modul;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Dokument dokument = (Dokument) o;
        return dokumentId == dokument.dokumentId && Objects.equals(name, dokument.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(dokumentId, name);
    }
}
