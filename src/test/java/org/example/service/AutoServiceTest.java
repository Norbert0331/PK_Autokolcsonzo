package org.example.service;

import org.example.model.Auto;
import org.example.repository.AutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AutoServiceTest {

    @Mock
    private AutoRepository autoRepository;

    @InjectMocks
    private AutoService autoService;

    private Auto testAuto;

    @BeforeEach
    void setUp() {
        testAuto = new Auto();
        testAuto.setId(1L);
        testAuto.setMarka("Toyota");
        testAuto.setTipus("Corolla");
        testAuto.setEvjarat(2020);
        testAuto.setRendszam("ABC-123");
    }

    @Test
    void getAllAutok_ShouldReturnAllAutos() {
        // Arrange
        Auto auto2 = new Auto();
        auto2.setId(2L);
        auto2.setMarka("Honda");
        auto2.setTipus("Civic");
        auto2.setEvjarat(2019);
        auto2.setRendszam("XYZ-789");

        when(autoRepository.findAll()).thenReturn(Arrays.asList(testAuto, auto2));

        // Act
        List<Auto> autok = autoService.getAllAutok();

        // Assert
        assertThat(autok).hasSize(2);
        assertThat(autok.get(0).getMarka()).isEqualTo("Toyota");
        assertThat(autok.get(1).getMarka()).isEqualTo("Honda");
        verify(autoRepository, times(1)).findAll();
    }

    @Test
    void getAutoById_WhenAutoExists_ShouldReturnAuto() {
        // Arrange
        when(autoRepository.findById(1L)).thenReturn(Optional.of(testAuto));

        // Act
        Optional<Auto> foundAuto = autoService.getAutoById(1L);

        // Assert
        assertThat(foundAuto).isPresent();
        assertThat(foundAuto.get().getMarka()).isEqualTo("Toyota");
        assertThat(foundAuto.get().getTipus()).isEqualTo("Corolla");
        verify(autoRepository, times(1)).findById(1L);
    }

    @Test
    void getAutoById_WhenAutoDoesNotExist_ShouldReturnEmpty() {
        // Arrange
        when(autoRepository.findById(99L)).thenReturn(Optional.empty());

        // Act
        Optional<Auto> foundAuto = autoService.getAutoById(99L);

        // Assert
        assertThat(foundAuto).isEmpty();
        verify(autoRepository, times(1)).findById(99L);
    }

    @Test
    void saveAuto_ShouldReturnSavedAuto() {
        // Arrange
        when(autoRepository.save(any(Auto.class))).thenReturn(testAuto);

        // Act
        Auto savedAuto = autoService.saveAuto(testAuto);

        // Assert
        assertThat(savedAuto).isNotNull();
        assertThat(savedAuto.getMarka()).isEqualTo("Toyota");
        verify(autoRepository, times(1)).save(testAuto);
    }

    @Test
    void deleteAuto_ShouldCallRepositoryDelete() {
        // Arrange
        doNothing().when(autoRepository).deleteById(1L);

        // Act
        autoService.deleteAuto(1L);

        // Assert
        verify(autoRepository, times(1)).deleteById(1L);
    }

    @Test
    void findByMarka_ShouldReturnMatchingAutos() {
        // Arrange
        when(autoRepository.findByMarka("Toyota")).thenReturn(List.of(testAuto));

        // Act
        List<Auto> foundAutos = autoService.findByMarka("Toyota");

        // Assert
        assertThat(foundAutos).hasSize(1);
        assertThat(foundAutos.get(0).getMarka()).isEqualTo("Toyota");
        verify(autoRepository, times(1)).findByMarka("Toyota");
    }

    @Test
    void findByEvjaratGreaterThanEqual_ShouldReturnMatchingAutos() {
        // Arrange
        when(autoRepository.findByEvjaratGreaterThanEqual(2020)).thenReturn(List.of(testAuto));

        // Act
        List<Auto> foundAutos = autoService.findByEvjaratGreaterThanEqual(2020);

        // Assert
        assertThat(foundAutos).hasSize(1);
        assertThat(foundAutos.get(0).getEvjarat()).isEqualTo(2020);
        verify(autoRepository, times(1)).findByEvjaratGreaterThanEqual(2020);
    }
}
