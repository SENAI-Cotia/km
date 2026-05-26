package br.com.notelab.backend.Repository;

import br.com.notelab.backend.Model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByIdCaderno(Long idCaderno);

    Optional<Note> findByIdAndIdCaderno(Long id, Long idCaderno);

    void deleteByIdCaderno(Long idCaderno);

    void deleteByIdCadernoIn(List<Long> idCadernos);
}
