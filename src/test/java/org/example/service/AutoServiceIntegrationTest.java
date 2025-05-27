package org.example.service;

import org.example.model.Auto;
import org.example.repository.AutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
public class AutoServiceIntegrationTest {

    @Autowired
    private AutoService autoService;
    
    @Autowired
    private AutoRepository autoRepository;
    
    private Auto toyota;
    private Auto honda;
    private Auto bmw;
    
    @BeforeEach
    void setUp() {
        // Tisztítsuk az adatbázist a teszt előtt
        autoRepository.deleteAll();
        
        // Tesztadatok létrehozása
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
        
        bmw = new Auto();
        bmw.setMarka("BMW");
        bmw.setTipus("X5");
        bmw.setEvjarat(2022);
        bmw.setRendszam("GHI-789");
        autoService.saveAuto(bmw);
    }
    
    @Test
    void testGetAllAutok() {
        // Act
        List<Auto> autok = autoService.getAllAutok();
        
        // Assert
        assertThat(autok).hasSize(3);
        assertThat(autok).extracting(Auto::getMarka).containsExactlyInAnyOrder("Toyota", "Honda", "BMW");
    }
    
    @Test
    void testGetAutoById() {
        // Act
        Optional<Auto> foundAuto = autoService.getAutoById(toyota.getId());
        
        // Assert
        assertThat(foundAuto).isPresent();
        assertThat(foundAuto.get().getMarka()).isEqualTo("Toyota");
        assertThat(foundAuto.get().getTipus()).isEqualTo("Corolla");
    }
    
    @Test
    void testSaveAuto() {
        // Arrange
        Auto audi = new Auto();
        audi.setMarka("Audi");
        audi.setTipus("A4");
        audi.setEvjarat(2021);
        audi.setRendszam("JKL-012");
        
        // Act
        Auto savedAudi = autoService.saveAuto(audi);
        
        // Assert
        assertThat(savedAudi.getId()).isNotNull();
        
        // Ellenőrizzük, hogy tényleg elmentette-e
        Optional<Auto> foundAudi = autoService.getAutoById(savedAudi.getId());
        assertThat(foundAudi).isPresent();
        assertThat(foundAudi.get().getMarka()).isEqualTo("Audi");
    }
    
    @Test
    void testUpdateAuto() {
        // Arrange
        toyota.setEvjarat(2021);
        
        // Act
        Auto updatedToyota = autoService.saveAuto(toyota);
        
        // Assert
        assertThat(updatedToyota.getEvjarat()).isEqualTo(2021);
        
        // Ellenőrizzük az adatbázisban is
        Optional<Auto> foundToyota = autoService.getAutoById(toyota.getId());
        assertThat(foundToyota).isPresent();
        assertThat(foundToyota.get().getEvjarat()).isEqualTo(2021);
    }
    
    @Test
    void testDeleteAuto() {
        // Act
        autoService.deleteAuto(honda.getId());
        
        // Assert
        Optional<Auto> deletedHonda = autoService.getAutoById(honda.getId());
        assertThat(deletedHonda).isEmpty();
        
        // Ellenőrizzük, hogy csak a Honda törlődött
        List<Auto> remainingAutok = autoService.getAllAutok();
        assertThat(remainingAutok).hasSize(2);
        assertThat(remainingAutok).extracting(Auto::getMarka).doesNotContain("Honda");
    }
    
    @Test
    void testFindByMarka() {
        // Act
        List<Auto> toyotas = autoService.findByMarka("Toyota");
        
        // Assert
        assertThat(toyotas).hasSize(1);
        assertThat(toyotas.get(0).getMarka()).isEqualTo("Toyota");
    }
    
    @Test
    void testFindByEvjaratGreaterThanEqual() {
        // Act
        List<Auto> newerCars = autoService.findByEvjaratGreaterThanEqual(2020);
        
        // Assert
        assertThat(newerCars).hasSize(2);
        assertThat(newerCars).extracting(Auto::getMarka).containsExactlyInAnyOrder("Toyota", "BMW");
    }
}
