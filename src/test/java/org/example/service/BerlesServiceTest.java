package org.example.service;

import org.example.model.Auto;
import org.example.model.Berles;
import org.example.repository.BerlesRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BerlesServiceTest {

    @Mock
    private BerlesRepository berlesRepository;

    @InjectMocks
    private BerlesService berlesService;

    private Berles testBerles;
    private Auto testAuto;

    @BeforeEach
    void setUp() {
        testAuto = new Auto();
        testAuto.setId(1L);
        testAuto.setMarka("Toyota");
        testAuto.setTipus("Corolla");
        testAuto.setEvjarat(2020);
        testAuto.setRendszam("ABC-123");

        testBerles = new Berles();
        testBerles.setId(1L);
        testBerles.setBerloNev("Teszt Elek");
        testBerles.setKezdoDatum(LocalDate.of(2025, 5, 1));
        testBerles.setVegDatum(LocalDate.of(2025, 5, 10));
        testBerles.setAuto(testAuto);
    }

    @Test
    void getAllBerlesek_ShouldReturnAllBerlesek() {
        Berles berles2 = new Berles();
        berles2.setId(2L);
        berles2.setBerloNev("Teszt Anna");
        berles2.setKezdoDatum(LocalDate.of(2025, 6, 1));
        berles2.setVegDatum(LocalDate.of(2025, 6, 10));
        berles2.setAuto(testAuto);

        when(berlesRepository.findAll()).thenReturn(Arrays.asList(testBerles, berles2));

        List<Berles> berlesek = berlesService.getAllBerlesek();

        assertThat(berlesek).hasSize(2);
        assertThat(berlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
        assertThat(berlesek.get(1).getBerloNev()).isEqualTo("Teszt Anna");
        verify(berlesRepository, times(1)).findAll();
    }

    @Test
    void getBerlesById_WhenBerlesExists_ShouldReturnBerles() {
        when(berlesRepository.findById(1L)).thenReturn(Optional.of(testBerles));

        Optional<Berles> foundBerles = berlesService.getBerlesById(1L);

        assertThat(foundBerles).isPresent();
        assertThat(foundBerles.get().getBerloNev()).isEqualTo("Teszt Elek");
        assertThat(foundBerles.get().getKezdoDatum()).isEqualTo(LocalDate.of(2025, 5, 1));
        verify(berlesRepository, times(1)).findById(1L);
    }

    @Test
    void getBerlesById_WhenBerlesDoesNotExist_ShouldReturnEmpty() {
        when(berlesRepository.findById(99L)).thenReturn(Optional.empty());

        Optional<Berles> foundBerles = berlesService.getBerlesById(99L);

        assertThat(foundBerles).isEmpty();
        verify(berlesRepository, times(1)).findById(99L);
    }

    @Test
    void saveBerles_ShouldReturnSavedBerles() {
        when(berlesRepository.save(any(Berles.class))).thenReturn(testBerles);

        Berles savedBerles = berlesService.saveBerles(testBerles);

        assertThat(savedBerles).isNotNull();
        assertThat(savedBerles.getBerloNev()).isEqualTo("Teszt Elek");
        verify(berlesRepository, times(1)).save(testBerles);
    }

    @Test
    void deleteBerles_ShouldCallRepositoryDelete() {
        doNothing().when(berlesRepository).deleteById(1L);

        berlesService.deleteBerles(1L);

        verify(berlesRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByAuto_ShouldReturnMatchingBerlesek() {
        when(berlesRepository.findByAuto(testAuto)).thenReturn(List.of(testBerles));

        List<Berles> foundBerlesek = berlesService.findByAuto(testAuto);

        assertThat(foundBerlesek).hasSize(1);
        assertThat(foundBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
        verify(berlesRepository, times(1)).findByAuto(testAuto);
    }

    @Test
    void findByBerloNevContaining_ShouldReturnMatchingBerlesek() {
        when(berlesRepository.findByBerloNevContaining("Elek")).thenReturn(List.of(testBerles));

        List<Berles> foundBerlesek = berlesService.findByBerloNevContaining("Elek");

        assertThat(foundBerlesek).hasSize(1);
        assertThat(foundBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
        verify(berlesRepository, times(1)).findByBerloNevContaining("Elek");
    }

    @Test
    void findByKezdoDatumBetween_ShouldReturnMatchingBerlesek() {
        LocalDate kezdo = LocalDate.of(2025, 4, 1);
        LocalDate vege = LocalDate.of(2025, 6, 1);
        when(berlesRepository.findByKezdoDatumBetween(kezdo, vege)).thenReturn(List.of(testBerles));

        List<Berles> foundBerlesek = berlesService.findByKezdoDatumBetween(kezdo, vege);

        assertThat(foundBerlesek).hasSize(1);
        assertThat(foundBerlesek.get(0).getKezdoDatum()).isEqualTo(LocalDate.of(2025, 5, 1));
        verify(berlesRepository, times(1)).findByKezdoDatumBetween(kezdo, vege);
    }

    @Test
    void isAutoAvailable_WhenNoOverlappingBookings_ShouldReturnTrue() {
        // Arrange
        Auto auto = new Auto();
        auto.setId(2L);
        auto.setMarka("Honda");
        auto.setTipus("Civic");
        auto.setRendszam("XYZ-789");

        LocalDate kezdoDatum = LocalDate.of(2025, 7, 1);
        LocalDate vegDatum = LocalDate.of(2025, 7, 10);

        // Meglévő bérlések az autóhoz (nincs átfedés)
        Berles existingBerles = new Berles();
        existingBerles.setId(3L);
        existingBerles.setAuto(auto);
        existingBerles.setBerloNev("Kovács János");
        existingBerles.setKezdoDatum(LocalDate.of(2025, 8, 1));
        existingBerles.setVegDatum(LocalDate.of(2025, 8, 10));

        when(berlesRepository.findByAuto(auto)).thenReturn(List.of(existingBerles));

        // Act
        boolean isAvailable = berlesService.isAutoAvailable(auto, kezdoDatum, vegDatum);

        // Assert
        assertThat(isAvailable).isTrue();
        verify(berlesRepository, times(1)).findByAuto(auto);
    }

    @Test
    void isAutoAvailable_WhenOverlappingBookingExists_ShouldReturnFalse() {
        // Arrange
        Auto auto = new Auto();
        auto.setId(2L);
        auto.setMarka("Honda");
        auto.setTipus("Civic");
        auto.setRendszam("XYZ-789");

        LocalDate kezdoDatum = LocalDate.of(2025, 7, 5);
        LocalDate vegDatum = LocalDate.of(2025, 7, 15);

        // Meglévő bérlés átfedéssel
        Berles existingBerles = new Berles();
        existingBerles.setId(3L);
        existingBerles.setAuto(auto);
        existingBerles.setBerloNev("Kovács János");
        existingBerles.setKezdoDatum(LocalDate.of(2025, 7, 1));
        existingBerles.setVegDatum(LocalDate.of(2025, 7, 10));

        when(berlesRepository.findByAuto(auto)).thenReturn(List.of(existingBerles));

        // Act
        boolean isAvailable = berlesService.isAutoAvailable(auto, kezdoDatum, vegDatum);

        // Assert
        assertThat(isAvailable).isFalse();
        verify(berlesRepository, times(1)).findByAuto(auto);
    }

    @Test
    void isAutoAvailable_WithExcludedId_WhenNoOtherOverlappingBookings_ShouldReturnTrue() {
        // Arrange
        Auto auto = new Auto();
        auto.setId(2L);
        auto.setMarka("Honda");
        auto.setTipus("Civic");
        auto.setRendszam("XYZ-789");

        Long excludedBerlesId = 3L;
        LocalDate kezdoDatum = LocalDate.of(2025, 7, 5);
        LocalDate vegDatum = LocalDate.of(2025, 7, 15);

        // Ez a bérlés átfedésben lenne, de ezt kizárjuk az ellenőrzésből
        Berles existingBerles = new Berles();
        existingBerles.setId(excludedBerlesId);
        existingBerles.setAuto(auto);
        existingBerles.setBerloNev("Kovács János");
        existingBerles.setKezdoDatum(LocalDate.of(2025, 7, 1));
        existingBerles.setVegDatum(LocalDate.of(2025, 7, 10));

        when(berlesRepository.findByAuto(auto)).thenReturn(List.of(existingBerles));

        // Act
        boolean isAvailable = berlesService.isAutoAvailable(auto, kezdoDatum, vegDatum, excludedBerlesId);

        // Assert
        assertThat(isAvailable).isTrue();
        verify(berlesRepository, times(1)).findByAuto(auto);
    }

    @Test
    void isAutoAvailable_WithExcludedId_WhenOtherOverlappingBookingExists_ShouldReturnFalse() {
        // Arrange
        Auto auto = new Auto();
        auto.setId(2L);
        auto.setMarka("Honda");
        auto.setTipus("Civic");
        auto.setRendszam("XYZ-789");

        Long excludedBerlesId = 3L;
        LocalDate kezdoDatum = LocalDate.of(2025, 7, 5);
        LocalDate vegDatum = LocalDate.of(2025, 7, 15);

        // Ez a bérlés átfedésben lenne, de ezt kizárjuk az ellenőrzésből
        Berles excludedBerles = new Berles();
        excludedBerles.setId(excludedBerlesId);
        excludedBerles.setAuto(auto);
        excludedBerles.setBerloNev("Kovács János");
        excludedBerles.setKezdoDatum(LocalDate.of(2025, 7, 1));
        excludedBerles.setVegDatum(LocalDate.of(2025, 7, 10));

        // Ez a bérlés is átfedésben van, és nincs kizárva
        Berles otherOverlappingBerles = new Berles();
        otherOverlappingBerles.setId(4L);
        otherOverlappingBerles.setAuto(auto);
        otherOverlappingBerles.setBerloNev("Nagy Péter");
        otherOverlappingBerles.setKezdoDatum(LocalDate.of(2025, 7, 12));
        otherOverlappingBerles.setVegDatum(LocalDate.of(2025, 7, 20));

        when(berlesRepository.findByAuto(auto)).thenReturn(List.of(excludedBerles, otherOverlappingBerles));

        // Act
        boolean isAvailable = berlesService.isAutoAvailable(auto, kezdoDatum, vegDatum, excludedBerlesId);

        // Assert
        assertThat(isAvailable).isFalse();
        verify(berlesRepository, times(1)).findByAuto(auto);
    }
}
