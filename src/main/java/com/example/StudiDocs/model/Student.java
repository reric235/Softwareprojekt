package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@Entity
public class Student {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studentId;

    @Column(nullable = false)
    private String vorname;

    @Column(nullable = false)
    private String nachname;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String passwort;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Rolle rolle = Rolle.STUDENT;

    @Column(nullable = false)
    private boolean verifiziert = false;

    @ManyToOne
    @JoinColumn(name = "seminargruppeId", nullable = false)
    @JsonBackReference(value = "seminargruppe-studenten")
    private Seminargruppe seminargruppe;;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Kalendereintrag> kalendereintraege;

    @Transient
    private Integer seminargruppeId;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "student-dokument")
    private List<Dokument> dokumente;

    // Standard-Konstruktor
    public Student() {
        this.rolle = Rolle.STUDENT;
    }

    public Student(String vorname, String nachname, String email, String passwort, int seminargruppeId, Rolle rolle) {
        this.vorname = vorname;
        this.nachname = nachname;
        this.email = email;
        this.passwort = passwort;
        this.seminargruppeId = seminargruppeId;
        this.rolle = rolle;
    }

    public int getStudentId() {
        return studentId;
    }

    public void setStudentId(int studentId) {
        this.studentId = studentId;
    }

    public String getVorname() {
        return vorname;
    }

    public void setVorname(String vorname) {
        this.vorname = vorname;
    }

    public String getNachname() {
        return nachname;
    }

    public void setNachname(String nachname) {
        this.nachname = nachname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPasswort() {
        return passwort;
    }

    public void setPasswort(String passwort) {
        this.passwort = passwort;
    }

    public boolean isVerifiziert() {
        return verifiziert;
    }

    public void setVerifiziert(boolean verifiziert) {
        this.verifiziert = verifiziert;
    }

    public Rolle getRolle() {
        return rolle;
    }

    public void setRolle(Rolle rolle) {
        if (rolle != null) {
            this.rolle = rolle;
        } else {
            this.rolle = Rolle.STUDENT;
        }
    }

    public Seminargruppe getSeminargruppe() {
        return seminargruppe;
    }

    public void setSeminargruppe(Seminargruppe seminargruppe) {
        this.seminargruppe = seminargruppe;
    }

    public boolean getVerifiziert() {
        return verifiziert;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Student student = (Student) o;
        return studentId == student.studentId &&
                Objects.equals(email, student.email) &&
                Objects.equals(vorname, student.vorname) &&
                Objects.equals(nachname, student.nachname);
    }

    @Override
    public int hashCode() {
        return Objects.hash(studentId, email, vorname, nachname);
    }
    public Integer getSeminargruppeId() {
        return seminargruppeId;
    }

    public void setSeminargruppeId(Integer seminargruppeId) {
        this.seminargruppeId = seminargruppeId;
    }

    @Override
    public String toString() {
        return "Student{" +
                "studentId=" + studentId +
                ", email='" + email + '\'' +
                ", vorname='" + vorname + '\'' +
                ", nachname='" + nachname + '\'' +
                '}';
    }
}