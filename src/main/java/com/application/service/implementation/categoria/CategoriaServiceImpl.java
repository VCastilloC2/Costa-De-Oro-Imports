package com.application.service.implementation.categoria;

import com.application.persistence.entity.categoria.Categoria;
import com.application.persistence.entity.categoria.SubCategoria;
import com.application.persistence.repository.CategoriaRepository;
import com.application.persistence.repository.SubCategoriaRepository;
import com.application.presentation.dto.categoria.request.CategoriaCreateRequest;
import com.application.presentation.dto.categoria.response.CategoriaProductoResponse;
import com.application.presentation.dto.categoria.response.CategoriaResponse;
import com.application.presentation.dto.categoria.response.SubCategoriaProductoResponse;
import com.application.presentation.dto.categoria.response.SubCategoriaResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.categoria.CategoriaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {

    private final CategoriaRepository categoriaRepository;
    private final SubCategoriaRepository subCategoriaRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Obtiene una categoría por su ID.
     * Este método es de uso interno para otros métodos del servicio.
     *
     * @param id ID de la categoría a buscar
     * @return La entidad categoria encontrada
     * @throws EntityNotFoundException si la categoría no existe
     */
    @Override
    public Categoria getCategoriaById(Long id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("La categoria con id: " + id + " no existe o ha sido eliminada"));
    }

    /**
     * Obtiene una categoría como DTO de respuesta por su ID.
     * Este método es para mostrar los datos de la categoria en EditarCategoria.html
     *
     * @param id ID de la categoría a buscar
     * @return DTO con la información de la categoria, incluyendo subcategorías y cantidad de productos.
     */
    @Override
    public CategoriaResponse getCategoriaResponseById(Long id) {
        Categoria categoria = this.getCategoriaById(id);
        return this.toResponse(categoria);
    }

    /**
     * Obtiene todas las categorías registradas (activas e inactivas).
     * Uso común para el panel administrativo.
     *
     * @return Lista de DTOs con nombre, descripción y cantidad de packs de las
     *         categorías
     */
    @Override
    public List<CategoriaResponse> getCategorias() {
        List<Categoria> categorias = categoriaRepository.findAll();
        return categorias.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene todas las categorías activas.
     * Uso para mostrar categorías disponibles en la sección de Productos.
     *
     * @return Lista de DTOs con las categorías activas
     */
    @Override
    public List<CategoriaResponse> getCategoriasActivas() {
        List<Categoria> categoriasActivas = categoriaRepository.findByActivoTrue();
        return categoriasActivas.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene las categorías activas.
     * Para uso del select de categoría principal en los formularios de crear y editar producto
     *
     * @return Lista de DTOs con las categorías activas (Solo se incluye id y nombre de la categoría)
     */
    @Override
    public List<CategoriaProductoResponse> getCategoriasProducto() {
        return categoriaRepository.findByActivoTrue()
                .stream()
                .map(categoria -> new CategoriaProductoResponse(
                        categoria.getCategoriaId(),
                        categoria.getNombre()
                ))
                .toList();
    }

    /**
     * Obtiene las Subcategorías activas.
     * Para uso del select de categoría secundaria en los formularios de crear y editar producto
     *
     * @param categoriaId ID de la categoria principal
     * @return Lista de DTOs con las Subcategorías activas (Solo se incluye id, nombre de la Subcategoría y id de la categoria principal)
     */
    @Override
    public List<SubCategoriaProductoResponse> getSubCategoriasActivasByCategoriaId(Long categoriaId) {
        return subCategoriaRepository.findByCategoriaCategoriaIdAndCategoriaActivoTrue(categoriaId)
                .stream()
                .map(subCategoria -> new SubCategoriaProductoResponse(
                        subCategoria.getSubCategoriaId(),
                        subCategoria.getNombre(),
                        subCategoria.getCategoria().getCategoriaId()
                ))
                .toList();
    }

    /**
     * Método para crear una categoria y subcategorias a partir de un DTO de creación.
     *
     * @param categoriaRequest DTO con los datos de la categoría y las subcategorias
     * @return Respuesta con mensaje de confirmación
     */
    @Override
    @Transactional
    public BaseResponse createCategoria(@NotNull CategoriaCreateRequest categoriaRequest) {

        String imagen = this.cloudinaryService.subirImagen(categoriaRequest.imagen(), "imagen-categoria");
        Categoria categoria = Categoria.builder()
                .imagen(imagen)
                .nombre(categoriaRequest.nombre())
                .descripcion(categoriaRequest.descripcion())
                .activo(categoriaRequest.activo())
                .build();

        for (String nombreSubCategoria : categoriaRequest.subcategorias()) {
            SubCategoria subCategoria = SubCategoria.builder()
                    .nombre(nombreSubCategoria)
                    .build();
            categoria.addSubCategoria(subCategoria);
        }

        categoriaRepository.save(categoria);

        return new BaseResponse("Categoria creada exitosamente", true);
    }

    /**
     * Método para actualizar una categoría y subcategorias a partir de un DTO de creación.
     *
     * @param categoriaRequest DTO con los nuevos datos de la categoría y las subcategorias
     * @param id               ID de la categoría a actualizar
     * @return DTO con mensaje de éxito
     * @throws EntityNotFoundException si la categoría no existe
     */
    @Override
    @Transactional
    public BaseResponse updateCategoria(@NotNull CategoriaCreateRequest categoriaRequest, Long id) {

        Categoria categoria = this.getCategoriaById(id);

        String imagen = this.cloudinaryService.subirImagen(categoriaRequest.imagen(), "imagen-categoria");

        categoria.setImagen(imagen);
        categoria.setNombre(categoriaRequest.nombre());
        categoria.setDescripcion(categoriaRequest.descripcion());
        categoria.setActivo(categoriaRequest.activo());

        for (SubCategoria subCategoria : new HashSet<>(categoria.getSubCategorias())) {
            categoria.deleteSubCategoria(subCategoria);
        }

        for (String nombreSubCategoria : categoriaRequest.subcategorias()) {
            SubCategoria subCategoria = SubCategoria.builder()
                    .nombre(nombreSubCategoria)
                    .build();
            categoria.addSubCategoria(subCategoria);
        }

        categoriaRepository.save(categoria);

        return new BaseResponse("Categoria actualizada exitosamente", true);
    }

    /**
     * Cambia el estado de la Categoria.
     *
     * @param id ID de la categoria a cambiar su estado
     * @return DTO con mensaje de confirmación según el estado de la categoria
     * @throws EntityNotFoundException si la categoria no existe
     * @apiNote Si la categoria tiene productos asociados, entonces no se podrá deshabilitar
     */
    @Override
    public BaseResponse changeEstadoCategoria(Long id) {
        Categoria categoria = this.getCategoriaById(id);
/*
        if (!categoria.getProductos().isEmpty()) {
            return new BaseResponse("No es posible deshabilitar una categoria con producto asociados", false);
        }
*/
        boolean nuevoEstado = !categoria.isActivo();
        categoria.setActivo(nuevoEstado);
        categoriaRepository.save(categoria);

        String mensaje = nuevoEstado
                ? "Categoría habilitada exitosamente"
                : "Categoría deshabilitada exitosamente";

        return new BaseResponse(mensaje, true);
    }

    /**
     * Elimina una categoría y sus subcategorias de la base de datos.
     * Solo es posible si no tiene productos asociados.
     *
     * @param id ID de la categoría a eliminar
     * @return DTO con mensaje de éxito o error según el estado del success
     */
    @Override
    @Transactional
    public BaseResponse deleteCategoria(Long id) {
        Categoria categoria = this.getCategoriaById(id);

        if (!categoria.getProductos().isEmpty()) {
            return new BaseResponse("No es posible eliminar una categoria con productos", false);
        }

        categoriaRepository.delete(categoria);

        return new BaseResponse("categoria eliminada exitosamente", true);
    }

    /**
     * Convierte una entidad Categoria a su DTO de respuesta,
     * no incluye la cantidad de productos que tiene
     * Para uso interno del Servicio en los métodos de búsqueda
     *
     * @param categoria Entidad categoria a convertir
     * @return DTO con la información de la categoria
     */
    @Override
    public CategoriaResponse toResponse(Categoria categoria) {
        return new CategoriaResponse(
                categoria.getCategoriaId(),
                cloudinaryService.getImagenUrl(categoria.getImagen()),
                categoria.getNombre(),
                categoria.getDescripcion(),
                categoria.isActivo(),
                categoriaRepository.countProductosByCategoriaId(categoria.getCategoriaId()),
                categoria.getSubCategorias().stream()
                        .map(subCategoria -> new SubCategoriaResponse(
                                subCategoria.getSubCategoriaId(), subCategoria.getNombre()
                        ))
                        .toList()
        );
    }
}
