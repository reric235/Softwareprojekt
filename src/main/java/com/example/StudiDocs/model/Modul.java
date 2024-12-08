package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Modul {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int modulId;

    @Column(nullable = false, length = 100)
    private String modulname;

    @Column(nullable = false)
    private int semester;

    @Column(nullable = false, length = 100)
    private String studiengang; // Neues Feld für Studiengang

    @OneToMany(mappedBy = "modul", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "modul-dokumente")
    private List<Dokument> dokumente;

    public Modul() {}

    public Modul(String modulname, int semester, String studiengang) {
        this.modulname = modulname;
        this.semester = semester;
        this.studiengang = studiengang;
    }

    public int getModulId() {
        return modulId;
    }

    public void setModulId(int modulId) {
        this.modulId = modulId;
    }

    public String getModulname() {
        return modulname;
    }

    public void setModulname(String modulname) {
        this.modulname = modulname;
    }

    public int getSemester() {
        return semester;
    }

    public void setSemester(int semester) {
        this.semester = semester;
    }

    public String getStudiengang() {
        return studiengang;
    }

    public void setStudiengang(String studiengang) {
        this.studiengang = studiengang;
    }

    public List<Dokument> getDokumente() {
        return dokumente;
    }

    public void setDokumente(List<Dokument> dokumente) {
        this.dokumente = dokumente;
    }

    public void addDokument(Dokument dokument) {
        dokumente.add(dokument);
        dokument.setModul(this);
    }

    public void removeDokument(Dokument dokument) {
        dokumente.remove(dokument);
        dokument.setModul(null);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Modul modul = (Modul) o;
        return modulId == modul.modulId && semester == modul.semester &&
                Objects.equals(modulname, modul.modulname) &&
                Objects.equals(studiengang, modul.studiengang);
    }

    @Override
    public int hashCode() {
        return Objects.hash(modulId, modulname, semester, studiengang);
    }
}
