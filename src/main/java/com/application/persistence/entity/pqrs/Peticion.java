package com.application.persistence.entity.pqrs;

import com.application.persistence.entity.pqrs.enums.EEstadoPeticion;
import com.application.persistence.entity.pqrs.enums.ETipoPeticion;
import com.application.persistence.entity.usuario.Usuario;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "peticiones")
public class Peticion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "peticion_id", nullable = false)
    private Long peticionId;

    @Column(name = "tipo")
    @Enumerated(EnumType.STRING)
    private ETipoPeticion tipoPeticion;
    private String asunto;
    private String mensaje;
    private LocalDate fecha;
    @Column(name = "estado")
    @Enumerated(EnumType.STRING)
    private EEstadoPeticion estadoPeticion;
    private boolean activo;

    // Cardinalidad con la tabla usuario (relación bidireccional)
    @ManyToOne
    @JoinColumn(name = "usuario_id",
            referencedColumnName = "usuario_id",
            foreignKey = @ForeignKey(name = "fk_peticion_usuario")
    )
    @JsonIgnore
    private Usuario usuario;
}
