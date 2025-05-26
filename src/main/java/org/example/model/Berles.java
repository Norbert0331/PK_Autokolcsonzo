package org.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Column;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.FetchType;
import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "berlesek")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Berles {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String berloNev;
    
    @Column(nullable = false)
    private LocalDate kezdoDatum;
    
    @Column(nullable = false)
    private LocalDate vegDatum;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "auto_id", nullable = false)
    private Auto auto;
}
