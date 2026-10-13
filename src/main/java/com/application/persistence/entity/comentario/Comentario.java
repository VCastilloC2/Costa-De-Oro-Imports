package com.application.persistence.entity.comentario;

import com.application.persistence.entity.historia.Historia;
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
@Table(name = "comentario")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "comentario_id")
    private Long comentarioId;
    @Column(length = 100, nullable = false)
    private String titulo;
    @Column(length = 1000, nullable = false)
    private String mensaje;
    @Column(nullable = false)
    private int calificacion;
    @Column(nullable = false)
    private LocalDate fecha;
    private boolean activo;

    // Cardinalidad con la tabla usuario (relación unidireccional)
    @ManyToOne
    @JoinColumn(name = "usuario_id", referencedColumnName = "usuario_id", foreignKey = @ForeignKey(name = "fk_comentario_usuario"))
    @JsonIgnore
    private Usuario usuario;

    // Cardinalidad con la tabla historia (relación bidireccional)
    @ManyToOne
    @JoinColumn(name = "historia_id", referencedColumnName = "historia_id", foreignKey = @ForeignKey(name = "fk_comentario_historia"))
    @JsonIgnore
    private Historia historia;

    // Agregar usuario a comentario y viceversa (bidirectional)
    public void addUsuario(Usuario usuario) {
        this.setUsuario(usuario);
        usuario.getComentarios().add(this);
    }

    // Eliminar usuario de comentario y viceversa (bidirectional)
    public void deleteUsuario(Usuario usuario) {
        this.setUsuario(null);
        usuario.getComentarios().remove(this);
    }

    // Agregar historia a comentario y viceversa (bidirectional)
    public void addHistoria(Historia historia) {
        this.setHistoria(historia);
        historia.getComentarios().add(this);
    }

    // Eliminar historia de comentario y viceversa (bidirectional)
    public void deleteHistoria(Historia historia) {
        this.setHistoria(null);
        historia.getComentarios().remove(this);
    }

}