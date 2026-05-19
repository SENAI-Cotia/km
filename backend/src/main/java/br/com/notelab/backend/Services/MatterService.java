package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Repository.MatterRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MatterService {

    private final MatterRepository repository;

    public MatterService(MatterRepository repository) {
        this.repository = repository;
    }

    public Matter createMatter(Matter matter) {
        if (matter == null) {
            throw new RuntimeException("Materia nao informada");
        }
        if (matter.getUserId() == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        if (matter.getName() == null || matter.getName().isBlank()) {
            throw new RuntimeException("name e obrigatorio");
        }

        matter.setId(null);
        matter.setName(matter.getName().trim());
        return repository.save(matter);
    }

    public List<Matter> listAllMatters() {
        return repository.findAll();
    }

    public Matter updateMatter(Long id, Matter matter) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        validateMatterName(matter);

        Matter existingMatter = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Materia nao encontrada"));

        existingMatter.setName(matter.getName().trim());
        return repository.save(existingMatter);
    }

    public List<Matter> getMattersByUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        return repository.findByUserId(userId);
    }

    public void deleteMatter(Long id) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        if (!repository.existsById(id)) {
            throw new NoSuchElementException("Materia nao encontrada");
        }

        repository.deleteById(id);
    }

    private void validateMatterName(Matter matter) {
        if (matter == null) {
            throw new RuntimeException("Materia nao informada");
        }
        if (matter.getName() == null || matter.getName().isBlank()) {
            throw new RuntimeException("name e obrigatorio");
        }
    }
}
