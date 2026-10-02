package com.application.configuration.ia.tools;

import com.application.persistence.entity.comentario.Comentario;
import com.application.persistence.repository.ComentarioRepository;
import com.application.service.interfaces.comentario.ComentarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ComentarioTools {

    private final ComentarioRepository comentarioRepository;
    private final ComentarioService comentarioService;

    private static final int MAX_RESULTADOS = 5;

    @Tool(
            name = "buscar_comentarios_por_historia",
            description = """
                    Busca comentarios asociados a una historia específica mediante su ID.
                    Usa esta herramienta para analizar el feedback de los usuarios sobre una historia.
                    """
    )
    public String buscarComentariosPorHistoria(Long historiaId) {
        List<Comentario> comentarios = comentarioRepository.findAll().stream()
                .filter(c -> c.getHistoria() != null && c.getHistoria().getHistoriaId().equals(historiaId))
                .limit(MAX_RESULTADOS)
                .toList();

        if (comentarios.isEmpty()) {
            return "No se encontraron comentarios para la historia ID: " + historiaId;
        }

        return comentarios.stream()
                .map(this::formatearComentario)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    @Tool(
            name = "listar_comentarios_recientes",
            description = """
                    Muestra los comentarios más recientes dejados por los usuarios.
                    """
    )
    public String listarComentariosRecientes() {
        List<Comentario> comentarios = comentarioRepository.findAll()
                .stream()
                .sorted((c1, c2) -> c2.getFecha().compareTo(c1.getFecha()))
                .limit(MAX_RESULTADOS)
                .toList();

        if (comentarios.isEmpty()) {
            return "No hay comentarios registrados.";
        }

        return comentarios.stream()
                .map(this::formatearComentario)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    private String formatearComentario(Comentario comentario) {
        return """
                ID: %d
                Título: %s
                Mensaje: %s
                Calificación: %d/5
                Fecha: %s
                Usuario: %s %s
                """.formatted(
                comentario.getComentarioId(),
                comentario.getTitulo(),
                comentario.getMensaje(),
                comentario.getCalificacion(),
                comentario.getFecha(),
                comentario.getUsuario() != null ? comentario.getUsuario().getNombres() : "Anónimo",
                comentario.getUsuario() != null ? comentario.getUsuario().getApellidos() : ""
        );
    }
}
