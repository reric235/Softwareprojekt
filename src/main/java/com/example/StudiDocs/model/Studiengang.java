package com.example.StudiDocs.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.util.List;

@Entity
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Studiengang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int studiengangId;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(mappedBy = "studiengang", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Modul> module;

    public Studiengang() {
    }

    public Studiengang(int studiengangId, String name) {
        this.studiengangId = studiengangId;
        this.name = name;
    }

    public void setStudiengangId(int studiengangId) {
        this.studiengangId = studiengangId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getStudiengangId() {
        return studiengangId;
    }

}
