package org.example.service;

import org.example.model.Auto;
import org.example.model.Berles;
import org.example.repository.AutoRepository;
import org.example.repository.BerlesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class BerlesServiceIntegrationTest {

    @Autowired
    private BerlesService berlesService;
    
    @Autowired
    private AutoService autoService;
    
    @Autowired
    private BerlesRepository berlesRepository;
    
    @Autowired
    private AutoRepository autoRepository;
    
    private Auto toyota;
    private Auto honda;
    private Berles berles1;
    private Berles berles2;
    
    @BeforeEach
    void setUp() {
        berlesRepository.deleteAll();
        autoRepository.deleteAll();

        toyota = new Auto();
        toyota.setMarka("Toyota");
        toyota.setTipus("Corolla");
        toyota.setEvjarat(2020);
        toyota.setRendszam("ABC-123");
        autoService.saveAuto(toyota);
        
        honda = new Auto();
        honda.setMarka("Honda");
        honda.setTipus("Civic");
        honda.setEvjarat(2018);
        honda.setRendszam("DEF-456");
        autoService.saveAuto(honda);

        berles1 = new Berles();
        berles1.setBerloNev("Teszt Elek");
        berles1.setKezdoDatum(LocalDate.of(2025, 6, 1));
        berles1.setVegDatum(LocalDate.of(2025, 6, 10));
        berles1.setAuto(toyota);
        berlesService.saveBerles(berles1);
        
        berles2 = new Berles();
        berles2.setBerloNev("Teszt Anna");
        berles2.setKezdoDatum(LocalDate.of(2025, 7, 1));
        berles2.setVegDatum(LocalDate.of(2025, 7, 15));
        berles2.setAuto(honda);
        berlesService.saveBerles(berles2);
    }
    
    @Test
    void testGetAllBerlesek() {
        List<Berles> berlesek = berlesService.getAllBerlesek();

        assertThat(berlesek).hasSize(2);
        assertThat(berlesek).extracting(Berles::getBerloNev).containsExactlyInAnyOrder("Teszt Elek", "Teszt Anna");
    }
    
    @Test
    void testGetBerlesById() {
        Optional<Berles> foundBerles = berlesService.getBerlesById(berles1.getId());

        assertThat(foundBerles).isPresent();
        assertThat(foundBerles.get().getBerloNev()).isEqualTo("Teszt Elek");
        assertThat(foundBerles.get().getAuto().getMarka()).isEqualTo("Toyota");
    }
    
    @Test
    void testSaveBerles() {
        Berles ujBerles = new Berles();
        ujBerles.setBerloNev("Teszt Béla");
        ujBerles.setKezdoDatum(LocalDate.of(2025, 8, 1));
        ujBerles.setVegDatum(LocalDate.of(2025, 8, 5));
        ujBerles.setAuto(toyota);

        Berles savedBerles = berlesService.saveBerles(ujBerles);

        assertThat(savedBerles.getId()).isNotNull();

        Optional<Berles> foundBerles = berlesService.getBerlesById(savedBerles.getId());
        assertThat(foundBerles).isPresent();
        assertThat(foundBerles.get().getBerloNev()).isEqualTo("Teszt Béla");
        assertThat(foundBerles.get().getAuto().getMarka()).isEqualTo("Toyota");
    }
    
    @Test
    void testUpdateBerles() {
        berles1.setBerloNev("Módosított Elek");

        Berles updatedBerles = berlesService.saveBerles(berles1);

        assertThat(updatedBerles.getBerloNev()).isEqualTo("Módosított Elek");

        Optional<Berles> foundBerles = berlesService.getBerlesById(berles1.getId());
        assertThat(foundBerles).isPresent();
        assertThat(foundBerles.get().getBerloNev()).isEqualTo("Módosított Elek");
    }
    
    @Test
    void testDeleteBerles() {
        berlesService.deleteBerles(berles1.getId());

        Optional<Berles> deletedBerles = berlesService.getBerlesById(berles1.getId());
        assertThat(deletedBerles).isEmpty();

        List<Berles> remainingBerlesek = berlesService.getAllBerlesek();
        assertThat(remainingBerlesek).hasSize(1);
        assertThat(remainingBerlesek).extracting(Berles::getBerloNev).containsExactly("Teszt Anna");
    }
    
    @Test
    void testFindByAuto() {
        List<Berles> toyotaBerlesek = berlesService.findByAuto(toyota);

        assertThat(toyotaBerlesek).hasSize(1);
        assertThat(toyotaBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
    }
    
    @Test
    void testFindByBerloNevContaining() {
        List<Berles> elekBerlesek = berlesService.findByBerloNevContaining("Elek");

        assertThat(elekBerlesek).hasSize(1);
        assertThat(elekBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
    }
    
    @Test
    void testFindByKezdoDatumBetween() {
        LocalDate startDate = LocalDate.of(2025, 5, 1);
        LocalDate endDate = LocalDate.of(2025, 6, 30);
        List<Berles> juniusiBerlesek = berlesService.findByKezdoDatumBetween(startDate, endDate);

        assertThat(juniusiBerlesek).hasSize(1);
        assertThat(juniusiBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
    }
}
