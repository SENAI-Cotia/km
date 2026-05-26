package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Caderno;
import br.com.notelab.backend.Repository.CadernoRepository;
import br.com.notelab.backend.Repository.MatterRepository;
import br.com.notelab.backend.Repository.NoteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
public class CadernoService {

    private final CadernoRepository cadernoRepository;
    private final MatterRepository matterRepository;
    private final NoteRepository noteRepository;

    public CadernoService(
            CadernoRepository cadernoRepository,
            MatterRepository matterRepository,
            NoteRepository noteRepository
    ) {
        this.cadernoRepository = cadernoRepository;
        this.matterRepository = matterRepository;
        this.noteRepository = noteRepository;
    }

    public Caderno createCaderno(Caderno caderno) {
        validateCreate(caderno);

        if (!matterRepository.existsById(caderno.getMatterId())) {
            throw new NoSuchElementException("Materia nao encontrada");
        }

        caderno.setId(null);
        caderno.setName(caderno.getName().trim());
        caderno.setDescription(caderno.getDescription().trim());
        return cadernoRepository.save(caderno);
    }

    public Caderno createCadernoForUser(Caderno caderno, Long userId) {
        if (caderno == null) {
            throw new RuntimeException("Caderno nao informado");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        caderno.setUserId(userId);
        validateCreate(caderno);

        matterRepository.findByIdAndUserId(caderno.getMatterId(), userId)
                .orElseThrow(() -> new NoSuchElementException("Materia nao encontrada"));

        caderno.setId(null);
        caderno.setName(caderno.getName().trim());
        caderno.setDescription(caderno.getDescription().trim());
        return cadernoRepository.save(caderno);
    }

    public Caderno updateCaderno(Long id, Caderno caderno) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        validateUpdate(caderno);

        Caderno existingCaderno = cadernoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Caderno nao encontrado"));

        existingCaderno.setName(caderno.getName().trim());
        existingCaderno.setDescription(caderno.getDescription().trim());
        return cadernoRepository.save(existingCaderno);
    }

    @Transactional
    public void deleteCaderno(Long id) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        Caderno caderno = cadernoRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Caderno nao encontrado"));

        deleteCadernoCascade(caderno);
    }

    @Transactional
    public void deleteCadernoForUser(Long id, Long userId) {
        Caderno caderno = getCadernoByIdAndUserId(id, userId);
        deleteCadernoCascade(caderno);
    }

    private void deleteCadernoCascade(Caderno caderno) {
        noteRepository.deleteByIdCaderno(caderno.getId());
        cadernoRepository.delete(caderno);
    }

    public List<Caderno> listByUser(Long userId) {
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        return cadernoRepository.findByUserId(userId);
    }

    public List<Caderno> listByMatter(Long matterId) {
        if (matterId == null) {
            throw new RuntimeException("matterId e obrigatorio");
        }

        return cadernoRepository.findByMatterId(matterId);
    }

    public List<Caderno> listByMatterAndUser(Long matterId, Long userId) {
        if (matterId == null) {
            throw new RuntimeException("matterId e obrigatorio");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        return cadernoRepository.findByMatterIdAndUserId(matterId, userId);
    }

    public Caderno getCadernoByIdAndUserId(Long id, Long userId) {
        if (id == null) {
            throw new RuntimeException("id e obrigatorio");
        }
        if (userId == null) {
            throw new RuntimeException("userId e obrigatorio");
        }

        return cadernoRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new NoSuchElementException("Caderno nao encontrado"));
    }

    private void validateCreate(Caderno caderno) {
        validateCaderno(caderno);
        if (caderno.getUserId() == null) {
            throw new RuntimeException("userId e obrigatorio");
        }
        if (caderno.getMatterId() == null) {
            throw new RuntimeException("matterId e obrigatorio");
        }
    }

    private void validateUpdate(Caderno caderno) {
        validateCaderno(caderno);
    }

    private void validateCaderno(Caderno caderno) {
        if (caderno == null) {
            throw new RuntimeException("Caderno nao informado");
        }
        if (caderno.getName() == null || caderno.getName().isBlank()) {
            throw new RuntimeException("name e obrigatorio");
        }
        if (caderno.getDescription() == null || caderno.getDescription().isBlank()) {
            throw new RuntimeException("description e obrigatorio");
        }
    }
}
