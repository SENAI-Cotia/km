package br.com.notelab.backend.Repository;

import br.com.notelab.backend.Model.Matter;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MatterRepository extends JpaRepository<Matter, Long> {
    List<Matter> findByUserId(Long userId);
}
