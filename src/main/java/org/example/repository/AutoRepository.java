package org.example.repository;

import org.example.model.Auto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AutoRepository extends JpaRepository<Auto, Long> {
    List<Auto> findByMarka(String marka);
    List<Auto> findByEvjaratGreaterThanEqual(int evjarat);
}
