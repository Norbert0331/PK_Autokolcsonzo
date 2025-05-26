package org.example.service;

import org.example.model.Auto;
import org.example.model.Berles;
import org.example.repository.BerlesRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class BerlesService {
    private final BerlesRepository berlesRepository;

    public BerlesService(BerlesRepository berlesRepository) {
        this.berlesRepository = berlesRepository;
    }

    @Transactional(readOnly = true)
    public List<Berles> getAllBerlesek() {
        return berlesRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Optional<Berles> getBerlesById(Long id) {
        return berlesRepository.findById(id);
    }

    public Berles saveBerles(Berles berles) {
        return berlesRepository.save(berles);
    }

    public void deleteBerles(Long id) {
        berlesRepository.deleteById(id);
    }
    
    @Transactional(readOnly = true)
    public List<Berles> findByAuto(Auto auto) {
        return berlesRepository.findByAuto(auto);
    }
    
    @Transactional(readOnly = true)
    public List<Berles> findByBerloNevContaining(String berloNev) {
        return berlesRepository.findByBerloNevContaining(berloNev);
    }
    
    @Transactional(readOnly = true)
    public List<Berles> findByKezdoDatumBetween(LocalDate kezdo, LocalDate vege) {
        return berlesRepository.findByKezdoDatumBetween(kezdo, vege);
    }
}
