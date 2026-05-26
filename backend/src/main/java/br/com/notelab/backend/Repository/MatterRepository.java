package br.com.notelab.backend.Repository;

import br.com.notelab.backend.Model.Matter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatterRepository extends JpaRepository<Matter, Long> {
    List<Matter> findByUserId(Long userId);

    Optional<Matter> findByIdAndUserId(Long id, Long userId);
}
