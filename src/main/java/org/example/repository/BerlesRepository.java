package org.example.repository;

import org.example.model.Berles;
import org.example.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface BerlesRepository extends JpaRepository<Berles, Long> {
    List<Berles> findByAuto(Auto auto);
    List<Berles> findByBerloNevContaining(String berloNev);
    List<Berles> findByKezdoDatumBetween(LocalDate kezdo, LocalDate vege);
}
