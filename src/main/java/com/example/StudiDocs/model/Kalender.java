package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Kalender {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int kalenderId;

    @OneToOne
    @JoinColumn(name = "seminargruppeId", unique = true, nullable = false)
    @JsonBackReference
    private Seminargruppe seminargruppe;

    @OneToMany(mappedBy = "kalender", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "kalender-eintraege")
    private List<Kalendereintrag> kalendereintraege;

    public Kalender() {}

    public Kalender(Seminargruppe seminargruppe) {
        this.seminargruppe = seminargruppe;
    }

    public int getKalenderId() {
        return kalenderId;
    }

    public void setKalenderId(int kalenderId) {
        this.kalenderId = kalenderId;
    }

    public Seminargruppe getSeminargruppe() {
        return seminargruppe;
    }

    public void setSeminargruppe(Seminargruppe seminargruppe) {
        this.seminargruppe = seminargruppe;
    }

    public List<Kalendereintrag> getKalendereintraege() {
        return kalendereintraege;
    }

    public void setKalendereintraege(List<Kalendereintrag> kalendereintraege) {
        this.kalendereintraege = kalendereintraege;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kalender kalender = (Kalender) o;
        return kalenderId == kalender.kalenderId &&
                Objects.equals(seminargruppe, kalender.seminargruppe);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kalenderId, seminargruppe);
    }

    @Override
    public String toString() {
        return "Kalender{" +
                "kalenderId=" + kalenderId +
                ", seminargruppe=" + seminargruppe +
                '}';
    }
}