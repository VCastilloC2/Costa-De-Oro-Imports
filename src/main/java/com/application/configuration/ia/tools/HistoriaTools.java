package com.application.configuration.ia.tools;

import com.application.persistence.entity.historia.Historia;
import com.application.persistence.repository.HistoriaRepository;
import com.application.service.interfaces.historia.HistoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class HistoriaTools {

    private final HistoriaRepository historiaRepository;
    private final HistoriaService historiaService;

    private static final int MAX_RESULTADOS = 5;

    @Tool(
            name = "buscar_historias",
            description = """
                    Busca historias o blogs del sistema mediante palabras clave en el título o descripción.
                    """
    )
    public String buscarHistorias(String query) {
        if (query == null || query.isBlank()) {
            return "La palabra de búsqueda es requerida.";
        }

        List<Historia> historias = historiaRepository.findAll().stream()
                .filter(h -> h.getTitulo().toLowerCase().contains(query.toLowerCase()) ||
                             h.getDescripcion().toLowerCase().contains(query.toLowerCase()))
                .limit(MAX_RESULTADOS)
                .toList();

        if (historias.isEmpty()) {
            return "No se encontraron historias relacionadas con: " + query;
        }

        return historias.stream()
                .map(this::formatearHistoria)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    @Tool(
            name = "listar_historias_activas",
            description = """
                    Lista las historias activas publicadas en el sistema.
                    """
    )
    public String listarHistoriasActivas() {
        List<Historia> historias = historiaRepository.findAll().stream()
                .filter(Historia::isActivo)
                .limit(MAX_RESULTADOS)
                .toList();

        if (historias.isEmpty()) {
            return "No hay historias activas.";
        }

        return historias.stream()
                .map(this::formatearHistoria)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    private String formatearHistoria(Historia historia) {
        return """
                ID: %d
                Título: %s
                Fecha: %s
                Estado: %s
                Descripción breve: %s
                """.formatted(
                historia.getHistoriaId(),
                historia.getTitulo(),
                historia.getFecha(),
                historia.isActivo() ? "ACTIVO" : "INACTIVO",
                historia.getDescripcion().substring(0, Math.min(historia.getDescripcion().length(), 100)) + "..."
        );
    }
}
