package com.application.service.implementation;

import com.application.persistence.entity.categoria.Categoria;
import com.application.persistence.entity.historia.Historia;
import com.application.persistence.entity.producto.Producto;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.repository.CategoriaRepository;
import com.application.persistence.repository.HistoriaRepository;
import com.application.persistence.repository.ProductoRepository;
import com.application.persistence.repository.UsuarioRepository;
import com.application.presentation.dto.SearchItemResponse;
import com.application.presentation.dto.SearchResponse;
import com.application.service.interfaces.SearchService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final UsuarioRepository usuarioRepository;
    private final HistoriaRepository historiaRepository;

    @Override
    public SearchResponse buscarUniversal(String query, String tipo, int limit) {
        SearchResponse response = SearchResponse.builder()
                .productos(new ArrayList<>())
                .categorias(new ArrayList<>())
                .clientes(new ArrayList<>())
                .proveedores(new ArrayList<>())
                .blog(new ArrayList<>())
                .query(query)
                .success(false)
                .build();

        if (query == null || query.trim().isEmpty()) {
            response.setSuccess(false);
            response.setMessage("Búsqueda vacía");
            response.setTotalResults(0);
            return response;
        }

        try {
            if (tipo == null || tipo.equalsIgnoreCase("productos")) {
                List<Producto> productos = productoRepository
                        .findByNombreContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
                                query, query, query)
                        .stream()
                        .filter(Producto::isActivo)
                        .limit(limit)
                        .collect(Collectors.toList());

                response.setProductos(productos.stream()
                        .map(this::mapProductoToSearchResult)
                        .collect(Collectors.toList()));
            }

            if (tipo == null || tipo.equalsIgnoreCase("categorias")) {
                List<Categoria> categorias = categoriaRepository
                        .findByNombreContainingIgnoreCase(query)
                        .stream()
                        .limit(limit)
                        .collect(Collectors.toList());

                response.setCategorias(categorias.stream()
                        .map(this::mapCategoriaToSearchResult)
                        .collect(Collectors.toList()));
            }

            if (tipo == null || tipo.equalsIgnoreCase("clientes")) {
                List<Usuario> clientes = usuarioRepository.buscarClientes(query)
                        .stream()
                        .limit(limit)
                        .collect(Collectors.toList());

                response.setClientes(clientes.stream()
                        .map(this::mapClienteToSearchResult)
                        .collect(Collectors.toList()));
            }

            if (tipo == null || tipo.equalsIgnoreCase("proveedores")) {
                List<Usuario> proveedores = usuarioRepository.buscarProveedores(query)
                        .stream()
                        .limit(limit)
                        .collect(Collectors.toList());

                response.setProveedores(proveedores.stream()
                        .map(this::mapProveedorToSearchResult)
                        .collect(Collectors.toList()));
            }

            if (tipo == null || tipo.equalsIgnoreCase("blog")) {
                List<Historia> historias = historiaRepository
                        .findByTituloContainingIgnoreCaseOrDescripcionContainingIgnoreCase(query, query)
                        .stream()
                        .filter(Historia::isActivo)
                        .limit(limit)
                        .collect(Collectors.toList());

                response.setBlog(historias.stream()
                        .map(this::mapBlogToSearchResult)
                        .collect(Collectors.toList()));
            }

            int total = response.getProductos().size()
                    + response.getCategorias().size()
                    + response.getClientes().size()
                    + response.getProveedores().size()
                    + response.getBlog().size();

            response.setTotalResults(total);
            response.setSuccess(true);
            response.setMessage("Búsqueda completada");

        } catch (Exception e) {
            log.error("Error en búsqueda universal", e);
            response.setSuccess(false);
            response.setMessage("Error en la búsqueda: " + e.getMessage());
            response.setTotalResults(0);
        }

        return response;
    }

    // ====== MAPEOS A DTO PARA RESULTADOS DE BÚSQUEDA ======

    private SearchItemResponse mapProductoToSearchResult(Producto p) {
        return SearchItemResponse.builder()
                .id(p.getProductoId())
                .type("producto")
                .titulo(p.getNombre())
                .subtitulo(p.getMarca() + " - " + p.getPais())
                .descripcion(truncar(p.getDescripcion(), 100))
                .imagen(p.getImagen())
                .precio(p.getPrecio())
                .stock(p.getStock())
                .url("/ver?id=" + p.getCodigoProducto())
                .badge(p.getStock() > 0 ? "En Stock" : "Agotado")
                .badgeClass(p.getStock() > 0 ? "badge-success" : "badge-danger")
                .build();
    }

    private SearchItemResponse mapCategoriaToSearchResult(Categoria c) {
        return SearchItemResponse.builder()
                .id(c.getCategoriaId())
                .type("categoria")
                .titulo(c.getNombre())
                .subtitulo("Categoría")
                .descripcion(truncar(c.getDescripcion(), 100))
                .icono("ri-list-unordered")
                .url("/productos?categoria=" + c.getCategoriaId())
                .badge("Categoría")
                .badgeClass("badge-info")
                .build();
    }

    private SearchItemResponse mapClienteToSearchResult(Usuario c) {
        return SearchItemResponse.builder()
                .id(c.getUsuarioId())
                .type("cliente")
                .titulo(safe(c.getNombres()) + " " + safe(c.getApellidos()))
                .subtitulo(safe(c.getCorreo()))
                .descripcion("Teléfono: " + safe(c.getTelefono()))
                .icono("ri-user-line")
                .url("/admin/clientes/" + c.getUsuarioId())
                .badge("Cliente")
                .badgeClass("badge-primary")
                .build();
    }

    private SearchItemResponse mapProveedorToSearchResult(Usuario p) {
        String razonSocial = p.getEmpresa() != null ? safe(p.getEmpresa().getRazonSocial()) : "";
        return SearchItemResponse.builder()
                .id(p.getUsuarioId())
                .type("proveedor")
                .titulo(razonSocial.isEmpty() ? safe(p.getNombres()) : razonSocial)
                .subtitulo(safe(p.getCorreo()))
                .descripcion("Teléfono: " + safe(p.getTelefono()))
                .icono("ri-store-2-line")
                .url("/admin/provedores/" + p.getUsuarioId())
                .badge("Proveedor")
                .badgeClass("badge-warning")
                .build();
    }

    private SearchItemResponse mapBlogToSearchResult(Historia h) {
        return SearchItemResponse.builder()
                .id(h.getHistoriaId())
                .type("blog")
                .titulo(h.getTitulo())
                .subtitulo("Artículo de Blog")
                .descripcion(truncar(h.getDescripcion(), 100))
                .imagen(h.getImagen())
                .icono("ri-news-line")
                .url("/blog/info?id=" + h.getHistoriaId())
                .badge("Blog")
                .badgeClass("badge-secondary")
                .build();
    }

    // ====== UTILIDADES ======

    private String truncar(String texto, int max) {
        if (texto == null) return "";
        if (texto.length() <= max) return texto;
        return texto.substring(0, max) + "...";
    }

    private String safe(String value) {
        return value == null ? "" : value;
    }

}
