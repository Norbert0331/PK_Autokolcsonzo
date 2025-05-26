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
        // Arrange
        Berles berles2 = new Berles();
        berles2.setId(2L);
        berles2.setBerloNev("Teszt Anna");
        berles2.setKezdoDatum(LocalDate.of(2025, 6, 1));
        berles2.setVegDatum(LocalDate.of(2025, 6, 10));
        berles2.setAuto(testAuto);

        when(berlesRepository.findAll()).thenReturn(Arrays.asList(testBerles, berles2));

        // Act
        List<Berles> berlesek = berlesService.getAllBerlesek();

        // Assert
        assertThat(berlesek).hasSize(2);
        assertThat(berlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
        assertThat(berlesek.get(1).getBerloNev()).isEqualTo("Teszt Anna");
        verify(berlesRepository, times(1)).findAll();
    }

    @Test
    void getBerlesById_WhenBerlesExists_ShouldReturnBerles() {
        // Arrange
        when(berlesRepository.findById(1L)).thenReturn(Optional.of(testBerles));

        // Act
        Optional<Berles> foundBerles = berlesService.getBerlesById(1L);

        // Assert
        assertThat(foundBerles).isPresent();
        assertThat(foundBerles.get().getBerloNev()).isEqualTo("Teszt Elek");
        assertThat(foundBerles.get().getKezdoDatum()).isEqualTo(LocalDate.of(2025, 5, 1));
        verify(berlesRepository, times(1)).findById(1L);
    }

    @Test
    void getBerlesById_WhenBerlesDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(berlesRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Berles> foundBerles = berlesService.getBerlesById(99L);

        // Assert
        assertThat(foundBerles).isEmpty();
        verify(berlesRepository, times(1)).findById(99L);
    }

    @Test
    void saveBerles_ShouldReturnSavedBerles() {
        // Arrange
        when(berlesRepository.save(any(Berles.class))).thenReturn(testBerles);

        // Act
        Berles savedBerles = berlesService.saveBerles(testBerles);

        // Assert
        assertThat(savedBerles).isNotNull();
        assertThat(savedBerles.getBerloNev()).isEqualTo("Teszt Elek");
        verify(berlesRepository, times(1)).save(testBerles);
    }

    @Test
    void deleteBerles_ShouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(berlesRepository).deleteById(1L);

        // Act
        berlesService.deleteBerles(1L);

        // Assert
        verify(berlesRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByAuto_ShouldReturnMatchingBerlesek() {
        // Arrange
        when(berlesRepository.findByAuto(testAuto)).thenReturn(List.of(testBerles));

        // Act
        List<Berles> foundBerlesek = berlesService.findByAuto(testAuto);

        // Assert
        assertThat(foundBerlesek).hasSize(1);
        assertThat(foundBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
        verify(berlesRepository, times(1)).findByAuto(testAuto);
    }

    @Test
    void findByBerloNevContaining_ShouldReturnMatchingBerlesek() {
        // Arrange
        when(berlesRepository.findByBerloNevContaining("Elek")).thenReturn(List.of(testBerles));

        // Act
        List<Berles> foundBerlesek = berlesService.findByBerloNevContaining("Elek");

        // Assert
        assertThat(foundBerlesek).hasSize(1);
        assertThat(foundBerlesek.get(0).getBerloNev()).isEqualTo("Teszt Elek");
        verify(berlesRepository, times(1)).findByBerloNevContaining("Elek");
    }

    @Test
    void findByKezdoDatumBetween_ShouldReturnMatchingBerlesek() {
        // Arrange
        LocalDate kezdo = LocalDate.of(2025, 4, 1);
        LocalDate vege = LocalDate.of(2025, 6, 1);
        when(berlesRepository.findByKezdoDatumBetween(kezdo, vege)).thenReturn(List.of(testBerles));

        // Act
        List<Berles> foundBerlesek = berlesService.findByKezdoDatumBetween(kezdo, vege);

        // Assert
        assertThat(foundBerlesek).hasSize(1);
        assertThat(foundBerlesek.get(0).getKezdoDatum()).isEqualTo(LocalDate.of(2025, 5, 1));
        verify(berlesRepository, times(1)).findByKezdoDatumBetween(kezdo, vege);
    }
}
