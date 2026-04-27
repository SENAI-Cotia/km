package br.com.notelab.backend.Model;

import jakarta.persistence.*;

import java.sql.Date;

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

    @Column(nullable = true)
    private String noteContent;

    @Column(nullable = false)
    private Date noteEditDate;

    public Note(Long id, Long idCaderno, String name, String noteContent, Date noteEditDate) {
        this.id = id;
        this.idCaderno = idCaderno;
        this.name = name;
        this.noteContent = noteContent;
        this.noteEditDate = noteEditDate;
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

    public Date getNoteEditDate() {
        return noteEditDate;
    }

    public void setNoteEditDate(Date noteEditDate) {
        this.noteEditDate = noteEditDate;
    }
}
