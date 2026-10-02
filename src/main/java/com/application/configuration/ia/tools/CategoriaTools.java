package com.application.configuration.ia.tools;

import com.application.persistence.entity.categoria.Categoria;
import com.application.persistence.repository.CategoriaRepository;
import com.application.service.interfaces.categoria.CategoriaService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class CategoriaTools {

    private final CategoriaRepository categoriaRepository;
    private final CategoriaService categoriaService;

    private static final int MAX_RESULTADOS = 5;

    @Tool(
            name = "buscar_categoria_por_nombre",
            description = """
                    Busca categorías por nombre o palabra clave.
                    Usa esta herramienta cuando el usuario quiera:
                    buscar, filtrar, encontrar o ver categorías existentes.
                    Ejemplos:
                    - buscar categoría de cervezas artesanales
                    - ¿qué categorías hay de vinos?
                    - buscar categorías que contengan 'rubio'
                    """
    )
    public String buscarCategoriaPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return "El nombre de la categoría es requerido.";
        }

        List<Categoria> categorias = categoriaRepository.findByNombreContainingIgnoreCase(nombre)
                .stream()
                .limit(MAX_RESULTADOS)
                .toList();

        if (categorias.isEmpty()) {
            return "No se encontraron categorías que coincidan con: " + nombre;
        }

        return categorias.stream()
                .map(this::formatearCategoria)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    @Tool(
            name = "listar_categorias_activas",
            description = """
                    Lista todas las categorías que están activas en el sistema.
                    Usa esta herramienta para mostrar el catálogo general de categorías.
                    Máximo 10 resultados.
                    """
    )
    public String listarCategoriasActivas() {
        List<Categoria> categorias = categoriaRepository.findByActivoTrue()
                .stream()
                .limit(10)
                .toList();

        if (categorias.isEmpty()) {
            return "No hay categorías activas en el sistema.";
        }

        return categorias.stream()
                .map(this::formatearCategoria)
                .collect(Collectors.joining("\n-------------------------\n"));
    }

    @Tool(
            name = "obtener_detalle_categoria",
            description = """
                    Obtiene la información detallada de una categoría mediante su ID.
                    Muestra nombre, descripción y estado.
                    """
    )
    public String obtenerDetalleCategoria(Long categoriaId) {
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
        if (categoria == null) {
            return "No se encontró la categoría con ID: " + categoriaId;
        }

        return formatearCategoria(categoria);
    }

    @Tool(
            name = "contar_productos_por_categoria",
            description = """
                    Obtiene la cantidad de productos asociados a una categoría específica.
                    Usa esta herramienta para saber qué tan poblada está una categoría.
                    """
    )
    public String contarProductosPorCategoria(Long categoriaId) {
        long count = categoriaRepository.countProductosByCategoriaId(categoriaId);
        Categoria categoria = categoriaRepository.findById(categoriaId).orElse(null);
        String nombre = (categoria != null) ? categoria.getNombre() : "ID " + categoriaId;

        return "La categoría '" + nombre + "' tiene " + count + " productos asociados.";
    }

    private String formatearCategoria(Categoria categoria) {
        return """
                ID: %d
                Nombre: %s
                Descripción: %s
                Estado: %s
                """.formatted(
                categoria.getCategoriaId(),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isActivo() ? "ACTIVO" : "INACTIVO"
        );
    }
}
