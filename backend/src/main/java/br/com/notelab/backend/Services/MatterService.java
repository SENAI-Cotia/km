package br.com.notelab.backend.Services;

import br.com.notelab.backend.Model.Matter;
import br.com.notelab.backend.Model.User;
import br.com.notelab.backend.Repository.MatterRepository;
import br.com.notelab.backend.Repository.UserRepository;
import org.springframework.stereotype.Service;

@Service
public class MatterService {
    private MatterRepository repository;

    public MatterService(MatterRepository repository) {
        this.repository = repository;
    }

    public Matter createMatter(Matter matter) {
        Matter matterCreated = repository.save(matter);
        return matterCreated;
    }
}

