package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import java.util.List;
import java.util.Objects;

@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
@Entity
public class Seminargruppe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int seminargruppeId;

    @Column(unique = true, nullable = false)
    private String name;

    @ManyToOne
    @JoinColumn(name = "studiengangId", nullable = false)
    private Studiengang studiengang;

    @OneToMany(mappedBy = "seminargruppe", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference(value = "seminargruppe-studenten")
    private List<Student> studenten;

    public Seminargruppe() {}

    public int getSeminargruppeId() {
        return seminargruppeId;
    }

    public void setSeminargruppeId(int seminargruppeId) {
        this.seminargruppeId = seminargruppeId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Studiengang getStudiengang() {
        return studiengang;
    }

    public void setStudiengang(Studiengang studiengang) {
        this.studiengang = studiengang;
    }

    public List<Student> getStudenten() {
        return studenten;
    }

    public void setStudenten(List<Student> studenten) {
        this.studenten = studenten;
    }

    public void addStudent(Student student) {
        if (!studenten.contains(student)) {
            studenten.add(student);
            student.setSeminargruppe(this);
        }
    }

    public void removeStudent(Student student) {
        if (studenten.contains(student)) {
            studenten.remove(student);
            student.setSeminargruppe(null);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Seminargruppe that = (Seminargruppe) o;
        return seminargruppeId == that.seminargruppeId &&
                Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(seminargruppeId, name);
    }

    @Override
    public String toString() {
        return "Seminargruppe{" +
                "seminargruppeId=" + seminargruppeId +
                ", name='" + name + '\'' +
                '}';
    }
}