package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Repository.MatterRepository;
import org.springframework.stereotype.Service;

import java.util.List;

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
}
