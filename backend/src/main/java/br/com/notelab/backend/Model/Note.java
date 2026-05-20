package br.com.notelab.backend.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;

@Entity
@Table(name = "notes")
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long idCaderno;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String noteContent;

    @Column(nullable = false)
    private LocalDate noteEditDate;

    public Note() {
    }

    public Note(Long id, Long idCaderno, String name, String noteContent, LocalDate noteEditDate) {
        this.id = id;
        this.idCaderno = idCaderno;
        this.name = name;
        this.noteContent = noteContent;
        this.noteEditDate = noteEditDate;
    }

    @PrePersist
    @PreUpdate
    protected void updateEditDate() {
        this.noteEditDate = LocalDate.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getIdCaderno() {
        return idCaderno;
    }

    public void setIdCaderno(Long idCaderno) {
        this.idCaderno = idCaderno;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNoteContent() {
        return noteContent;
    }

    public void setNoteContent(String noteContent) {
        this.noteContent = noteContent;
    }

    public LocalDate getNoteEditDate() {
        return noteEditDate;
    }

    public void setNoteEditDate(LocalDate noteEditDate) {
        this.noteEditDate = noteEditDate;
    }
}
