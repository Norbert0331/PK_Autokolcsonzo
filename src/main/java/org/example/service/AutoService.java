package org.example.service;

import org.example.model.Auto;
import org.example.repository.AutoRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class AutoService {
    private final AutoRepository autoRepository;

    public AutoService(AutoRepository autoRepository) {
        this.autoRepository = autoRepository;
    }

    @Transactional(readOnly = true)
    public List<Auto> getAllAutok() {
        return autoRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Auto> getAutoById(Long id) {
        return autoRepository.findById(id);
    }

    public Auto saveAuto(Auto auto) {
        return autoRepository.save(auto);
    }

    public void deleteAuto(Long id) {
        autoRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Auto> findByMarka(String marka) {
        return autoRepository.findByMarka(marka);
    }
    
    @Transactional(readOnly = true)
    public List<Auto> findByEvjaratGreaterThanEqual(int evjarat) {
        return autoRepository.findByEvjaratGreaterThanEqual(evjarat);
    }
}
