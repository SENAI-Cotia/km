package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Repository.CadernoRepository;
import br.com.notelab.backend.Repository.MatterRepository;
import br.com.notelab.backend.Repository.NoteRepository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class MatterService {

    private static final String HEX_COLOR_PATTERN = "^#[0-9A-Fa-f]{6}$";
    private final MatterRepository repository;
    private final CadernoRepository cadernoRepository;
    private final NoteRepository noteRepository;

    public MatterService(
            MatterRepository repository,
            CadernoRepository cadernoRepository,
            NoteRepository noteRepository
    ) {
        this.repository = repository;
        this.cadernoRepository = cadernoRepository;
        this.noteRepository = noteRepository;
    }

    public Matter createMatter(Matter matter) {
        validateCreate(matter);

        matter.setId(null);
        matter.setName(matter.getName().trim());
        matter.setColor(normalizeColor(matter.getColor()));
        return repository.save(matter);
    }

    public Matter createMatterForUser(Matter matter, Long userId) {
        if (matter == null) {
            throw new RuntimeException("Materia nao informada");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        matter.setUserId(userId);
        return createMatter(matter);
    }

    private void validateCreate(Matter matter) {
        if (matter == null) {
            throw new RuntimeException("Materia nao informada");
        }
        if (matter.getUserId() == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        if (matter.getName() == null || matter.getName().isBlank()) {
            throw new RuntimeException("name e obrigatorio");
        }
    }

    public List<Matter> listAllMatters() {
        return repository.findAll().stream()
                .peek(this::ensureColor)
                .toList();
    }

    public Matter updateMatter(Long id, Matter matter) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        validateMatterName(matter);

        Matter existingMatter = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Materia nao encontrada"));

        existingMatter.setName(matter.getName().trim());
        if (matter.getColor() != null) {
            existingMatter.setColor(normalizeColor(matter.getColor()));
        } else {
            ensureColor(existingMatter);
        }
        return repository.save(existingMatter);
    }

    public List<Matter> getMattersByUserId(Long userId) {
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        return repository.findByUserId(userId).stream()
                .peek(this::ensureColor)
                .toList();
    }

    public Matter getMatterByIdAndUserId(Long id, Long userId) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        Matter matter = repository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Materia nao encontrada"));
        ensureColor(matter);
        return matter;
    }

    @Transactional
    public void deleteMatter(Long id) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        Matter matter = repository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Materia nao encontrada"));

        deleteMatterCascade(matter);
    }

    @Transactional
    public void deleteMatterForUser(Long id, Long userId) {
        Matter matter = getMatterByIdAndUserId(id, userId);
        deleteMatterCascade(matter);
    }

    private void deleteMatterCascade(Matter matter) {
        List<Caderno> cadernos = cadernoRepository.findByMatterIdAndUserId(matter.getId(), matter.getUserId());
        List<Long> cadernoIds = cadernos.stream()
                .map(Caderno::getId)
                .toList();

        if (!cadernoIds.isEmpty()) {
            noteRepository.deleteByIdCadernoIn(cadernoIds);
            cadernoRepository.deleteAll(cadernos);
        }

        repository.delete(matter);
    }

    private void validateMatterName(Matter matter) {
        if (matter == null) {
            throw new RuntimeException("Materia nao informada");
        }
        if (matter.getName() == null || matter.getName().isBlank()) {
            throw new RuntimeException("name e obrigatorio");
        }
    }

    private String normalizeColor(String color) {
        if (color == null || color.isBlank()) {
            return Matter.DEFAULT_COLOR;
        }

        String normalized = color.trim();
        if (!normalized.matches(HEX_COLOR_PATTERN)) {
            throw new RuntimeException("color deve ser hexadecimal");
        }

        return normalized.toUpperCase();
    }

    private void ensureColor(Matter matter) {
        if (matter.getColor() == null || matter.getColor().isBlank()) {
            matter.setColor(Matter.DEFAULT_COLOR);
        }
    }
}
