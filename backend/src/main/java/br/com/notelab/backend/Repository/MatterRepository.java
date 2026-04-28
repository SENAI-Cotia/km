package br.com.notelab.backend.Repository;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MatterRepository extends JpaRepository<Matter, Long> {
    List<Matter> Id(Long userId);
}
