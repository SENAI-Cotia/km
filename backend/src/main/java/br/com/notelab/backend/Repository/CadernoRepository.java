package br.com.notelab.backend.Repository;

import br.com.notelab.backend.Model.Caderno;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CadernoRepository extends JpaRepository<Caderno, Long> {
    List<Caderno> findByUserId(Long userId);

    List<Caderno> findByMatterId(Long matterId);

    List<Caderno> findByMatterIdAndUserId(Long matterId, Long userId);

    Optional<Caderno> findByIdAndUserId(Long id, Long userId);
}
