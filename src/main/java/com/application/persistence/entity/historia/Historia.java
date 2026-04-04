package com.application.persistence.entity.historia;

import com.application.persistence.entity.comentario.Comentario;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "historia")
public class Historia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "historia_id")
    private Long historiaId;

    @Column(nullable = false)
    private String imagen;
    @Column(columnDefinition = "TEXT", nullable = false)
    private String titulo;
    @Column(columnDefinition = "LONGTEXT", nullable = false)
    private String descripcion;
    @Column(name = "historia_completa", columnDefinition = "LONGTEXT", nullable = false)
    private String historiaCompleta;
    @Column(nullable = false)
    private LocalDate fecha;
    @Column(name = "is_enabled")
    private boolean activo;

    // Cardinalidad con la tabla comentario (relación bidireccional)
    @Builder.Default
    @OneToMany(mappedBy = "historia", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Comentario> comentarios = new HashSet<>();

}