package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Objects;

@Entity
public class Kalendereintrag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int kalendereintragId;

    @Column(nullable = false, length = 255)
    private String beschreibung;

    @Column(nullable = false)
    private LocalDate eintragsdatum;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EventType eventType;

    @ManyToOne
    @JoinColumn(name = "kalenderId", nullable = false)
    @JsonBackReference(value = "kalender-eintraege")
    private Kalender kalender;

    @ManyToOne
    @JoinColumn(name = "studentId", nullable = false)
    @JsonIgnore
    private Student student;

    // Konstruktoren
    public Kalendereintrag() {}

    public Kalendereintrag(String beschreibung, LocalDate eintragsdatum, LocalTime startTime, LocalTime endTime, EventType eventType, Kalender kalender, Student student) {
        this.beschreibung = beschreibung;
        this.eintragsdatum = eintragsdatum;
        this.startTime = startTime;
        this.endTime = endTime;
        this.eventType = eventType;
        this.kalender = kalender;
        this.student = student;
    }

    public int getKalendereintragId() {
        return kalendereintragId;
    }

    public void setKalendereintragId(int kalendereintragId) {
        this.kalendereintragId = kalendereintragId;
    }

    public String getBeschreibung() {
        return beschreibung;
    }

    public void setBeschreibung(String beschreibung) {
        this.beschreibung = beschreibung;
    }

    public LocalDate getEintragsdatum() {
        return eintragsdatum;
    }

    public void setEintragsdatum(LocalDate eintragsdatum) {
        this.eintragsdatum = eintragsdatum;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalTime startTime) {
        this.startTime = startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }

    public void setEndTime(LocalTime endTime) {
        this.endTime = endTime;
    }

    public EventType getEventType() {
        return eventType;
    }

    public void setEventType(EventType eventType) {
        this.eventType = eventType;
    }

    public Kalender getKalender() {
        return kalender;
    }

    public void setKalender(Kalender kalender) {
        this.kalender = kalender;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Kalendereintrag that = (Kalendereintrag) o;
        return kalendereintragId == that.kalendereintragId &&
                Objects.equals(beschreibung, that.beschreibung);
    }

    @Override
    public int hashCode() {
        return Objects.hash(kalendereintragId, beschreibung);
    }

    @Override
    public String toString() {
        return "Kalendereintrag{" +
                "kalendereintragId=" + kalendereintragId +
                ", beschreibung='" + beschreibung + '\'' +
                ", eintragsdatum=" + eintragsdatum +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", eventType=" + eventType +
                ", kalender=" + kalender +
                ", student=" + student +
                '}';
    }
}
