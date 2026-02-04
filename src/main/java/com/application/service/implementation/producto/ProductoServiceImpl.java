package com.application.service.implementation.producto;

import com.application.persistence.entity.categoria.Categoria;
import com.application.persistence.entity.categoria.SubCategoria;
import com.application.persistence.entity.producto.Producto;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.repository.CategoriaRepository;
import com.application.persistence.repository.ProductoRepository;
import com.application.persistence.repository.SubCategoriaRepository;
import com.application.persistence.repository.UsuarioRepository;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.producto.request.FiltroRequest;
import com.application.presentation.dto.producto.request.ProductoCreateRequest;
import com.application.presentation.dto.producto.response.ProductoResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.producto.ProductoService;
import com.application.service.interfaces.usuario.UsuarioService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final UsuarioRepository usuarioRepository;
    private final CategoriaRepository categoriaRepository;
    private final SubCategoriaRepository subCategoriaRepository;

    private final CloudinaryService cloudinaryService;

    /**
     * Obtiene un producto por su ID.
     * Este método es de uso interno para otros métodos del servicio.
     *
     * @param id ID del producto a buscar
     * @return La entidad producto encontrada
     * @throws EntityNotFoundException si la producto no existe
     */
    @Override
    public Producto getProductoById(Long id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("error: El producto con id: " + id + " no existe"));
    }

    /**
     * Obtiene un producto como DTO de respuesta por su ID.
     * Este método es para la presentación del producto en la pagina de
     * Producto-Descripción
     *
     * @param id ID del producto a buscar
     * @return DTO con la información del producto, incluyendo categorías.
     */
    @Override
    public ProductoResponse getProductoResponseById(Long id) {
        Producto producto = this.getProductoById(id);
        return this.toResponse(producto);
    }

    /**
     * Obtiene todos los productos registrados (activos e inactivos).
     * Uso común para el panel administrativo.
     *
     * @return Lista de DTOs con datos básicos de cada producto
     */
    @Override
    public List<ProductoResponse> getProductos() {
        List<Producto> productos = productoRepository.findAll();
        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene todos los productos que están activos (disponibles para la venta).
     * Este método es para la vista del E-commerce
     *
     * @return Lista de DTOs con productos activos
     */
    @Override
    public List<ProductoResponse> getProductosActivos() {
        List<Producto> productos = productoRepository.findProductosActivos();
        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene todos los productos que están activos y sean distintos de
     * Etipo.UNIDAD,
     * Este método es para la vista de Pack para listar cajas, pack y combos
     * que estén disponibles para la venta
     *
     * @return Lista de DTOs con productos activos que no sean individuales
     */
    @Override
    public List<ProductoResponse> getPacksActivos() {
        List<Producto> productoList = productoRepository.findProductosActivosMasVendidosNoUnidad();
        return productoList.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene los productos más vendidos en orden descendente (más ventas a menor ventas) y con estado activo.
     * Este método sera usado en la pagina de Index.html para mostrar los productos destacados
     *
     * @return Lista de DTO con los 16 productos con mayor cantidad de ventas y con estado activo
     */
    @Override
    public List<ProductoResponse> getProductosMasVendidosActivos() {
        List<Producto> productos = productoRepository.findProductosMasVendidosActivos();
        return productos.stream()
                .map(this::toResponse)
                .limit(16)
                .toList();
    }

    /**
     * Obtiene los productos más vendidos por su categoria en orden descendente y con estado activo.
     * Este método sera usado en la pagina de Index.html para mostrar los productos según su categoriaId
     *
     * @param categoriaId ID de la categoria
     * @return Lista de DTO con 12 productos pertenecientes a la categoria indicada, con mayor ventas y estado activo
     */
    @Override
    public List<ProductoResponse> getProductosMasVendidosByCategoriaIdActivos(Long categoriaId) {
        List<Producto> productos = productoRepository.findProductosMasVendidosByCategoriaIdActivos(categoriaId);
        return productos.stream()
                .map(this::toResponse)
                .limit(12)
                .toList();
    }

    /**
     * Obtiene los productos más vendidos de varias categorías en orden descendente y con estado activo.
     * Este método sera usado en la pagina de Index.html para mostrar los productos de varias categorías
     *
     * @param categoriaIds IDs de las categorías
     * @return Lista de DTO con 12 productos pertenecientes a las categorías indicadas, con mayor ventas y estado activo
     */
    @Override
    public List<ProductoResponse> getProductosMasVendidosByCategoriaIdsActivos(List<Long> categoriaIds) {
        List<Producto> productos = productoRepository.findProductosMasVendidosByCategoriaIdsActivos(categoriaIds);
        return productos.stream()
                .map(this::toResponse)
                .limit(12)
                .toList();
    }

    /**
     * Obtiene los países de los productos en orden alfabético y sin repetir.
     *
     * @return lista de países.
     */
    public List<String> getPaisesProducto() {
        return productoRepository.findDistinctPaises();
    }

    /**
     * Obtiene las marcas de los productos sin repetir.
     *
     * @return lista de marcas.
     */
    public List<String> getMarcasProductos() {
        return productoRepository.findDistinctMarcas();
    }

    /**
     * Obtiene los productos ordenados por precio de menor a mayor.
     *
     * @return lista de productos ordenados.
     */
    public List<ProductoResponse> getProductosPorPrecioAsc() {
        List<Producto> productos = productoRepository.findAllOrderByPrecioAsc();
        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene los productos ordenados por precio de mayor a menor.
     *
     * @return lista de productos ordenados.
     */
    public List<ProductoResponse> getProductosPorPrecioDesc() {
        List<Producto> productos = productoRepository.findAllOrderByPrecioDesc();
        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene los productos ordenados alfabéticamente de A a Z.
     *
     * @return lista de productos ordenados.
     */
    public List<ProductoResponse> getProductosPorNombreAsc() {
        List<Producto> productos = productoRepository.findAllOrderByNombreAsc();
        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene los productos ordenados alfabéticamente de Z a A.
     *
     * @return lista de productos ordenados.
     */
    public List<ProductoResponse> getProductosPorNombreDesc() {
        List<Producto> productos = productoRepository.findAllOrderByNombreDesc();
        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene las categorías activas con sus subcategorías.
     *
     * @return lista de pares categoria-subcategoria.
     */
    public Map<String, List<String>> getCategoriasActivasConSubcategorias() {
        List<Object[]> data = productoRepository.findCategoriasActivasConSubcategorias();

        Map<String, List<String>> resultado = new LinkedHashMap<>();

        for (Object[] row : data) {
            String categoria = (String) row[0];
            String subcategoria = (String) row[1];

            resultado
                    .computeIfAbsent(categoria, k -> new ArrayList<>())
                    .add(subcategoria);
        }

        return resultado;
    }

    /**
     * Obtiene los productos más vendidos.
     *
     * @return lista de productos ordenados por ventas.
     */
    public List<ProductoResponse> getProductosMasVendidos() {
        List<Object[]> data = productoRepository.findProductosMasVendidos();

        return data.stream()
                .map(row -> {
                    Producto p = (Producto) row[0];
                    return toResponse(p);
                })
                .toList();
    }

    public List<ProductoResponse> filtrarProductos(FiltroRequest filtros) {
        List<Producto> productos = productoRepository.findByFiltros(
                filtros.paises(),
                filtros.marcas(),
                filtros.categorias(),
                filtros.subcategorias(),
                filtros.precioMin(),
                filtros.precioMax()
        );

        return productos.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Crea un nuevo producto a partir de un DTO de creación.
     *
     * @param productoRequest DTO con los datos del nuevo producto
     * @return Respuesta con mensaje de confirmación y con un boolean que define si la operación fue exitosa o no.
     */
    @Override
    @Transactional
    public BaseResponse createProducto(@NotNull ProductoCreateRequest productoRequest) {

        String imagen = this.cloudinaryService.subirImagen(productoRequest.imagen(), "imagen-producto");

        Producto producto = Producto.builder()
                .codigoProducto(productoRequest.codigoProducto())
                .imagen(imagen)
                .nombre(productoRequest.nombre())
                .marca(productoRequest.marca())
                .pais(productoRequest.pais())
                .eTipo(productoRequest.tipo())
                .precioRegular(productoRequest.precioRegular())
                .precio(productoRequest.precio())
                .stock(productoRequest.stock())
                .descripcion(productoRequest.descripcion())
                .activo(productoRequest.activo())
                .build();

        Long categoriaId = productoRequest.categoriaId();
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntityNotFoundException("La categoria con id: " + categoriaId + " no exite"));
        categoria.addProducto(producto);

        Long subCategoriaId = productoRequest.subCategoriaId();
        SubCategoria subCategoria = subCategoriaRepository.findById(subCategoriaId)
                        .orElseThrow(() -> new EntityNotFoundException("La subcategoria con id: " + subCategoriaId + " no existe"));

        if (!subCategoria.getCategoria().getCategoriaId().equals(categoriaId)) {
            throw new IllegalArgumentException("La subcategoria seleccionada no pertenece a la categoría " + categoria.getNombre());
        }
        subCategoria.addProducto(producto);

        Long proveedorId = productoRequest.proveedorId();
        Usuario proveedor = usuarioRepository.findById(proveedorId)
                        .orElseThrow(() -> new EntityNotFoundException("El proveedor con id: " + proveedorId + " no existe"));
        proveedor.addProducto(producto);

        productoRepository.save(producto);

        return new BaseResponse("Producto creado exitosamente", true);
    }

    /**
     * Actualiza los datos de un producto existente.
     *
     * @param productoRequest DTO con los datos actualizados
     * @param id              ID del producto a actualizar
     * @return Respuesta con mensaje de confirmación y con un boolean que define si la operación fue exitosa o no.
     * @throws EntityNotFoundException si el producto no existe
     */
    @Override
    @Transactional
    public BaseResponse updateProducto(@NotNull ProductoCreateRequest productoRequest, Long id) {

        Producto producto = this.getProductoById(id);

        String imagen = this.cloudinaryService.subirImagen(productoRequest.imagen(), "imagen-producto");

        producto.setCodigoProducto(productoRequest.codigoProducto());
        producto.setImagen(imagen);
        producto.setNombre(productoRequest.nombre());
        producto.setMarca(productoRequest.marca());
        producto.setPais(productoRequest.pais());
        producto.setETipo(productoRequest.tipo());
        producto.setPrecioRegular(productoRequest.precioRegular());
        producto.setPrecio(productoRequest.precio());
        producto.setStock(productoRequest.stock());
        producto.setDescripcion(productoRequest.descripcion());
        producto.setActivo(productoRequest.activo());

        producto.getCategoria().deleteProducto(producto);
        producto.getSubCategoria().deleteProducto(producto);
        producto.getProveedor().deleteProducto(producto);

        Long categoriaId = productoRequest.categoriaId();
        Categoria categoria = categoriaRepository.findById(categoriaId)
                .orElseThrow(() -> new EntityNotFoundException("La categoria con id: " + categoriaId + " no exite"));
        categoria.addProducto(producto);

        Long subCategoriaId = productoRequest.subCategoriaId();
        SubCategoria subCategoria = subCategoriaRepository.findById(subCategoriaId)
                .orElseThrow(() -> new EntityNotFoundException("La subcategoria con id: " + subCategoriaId + " no existe"));

        if (!subCategoria.getCategoria().getCategoriaId().equals(categoriaId)) {
            throw new IllegalArgumentException("La subcategoria seleccionada no pertenece a la categoría " + categoria.getNombre());
        }
        subCategoria.addProducto(producto);

        Long proveedorId = productoRequest.proveedorId();
        Usuario proveedor = usuarioRepository.findById(proveedorId)
                        .orElseThrow(() -> new EntityNotFoundException("El proveedor con id: " + proveedorId + " no existe."));
        proveedor.addProducto(producto);

        productoRepository.save(producto);

        return new BaseResponse("Producto actualizado exitosamente", true);
    }

    /**
     * Cambia el estado del producto.
     *
     * @param id ID del producto a cambiar su estado
     * @return DTO con mensaje de confirmación según el estado del producto y con un boolean que define si la operación fue exitosa o no.
     * @throws EntityNotFoundException si el producto no existe
     */
    @Override
    public BaseResponse changeEstadoProducto(Long id) {
        Producto producto = this.getProductoById(id);

        boolean nuevoEstado = !producto.isActivo();
        producto.setActivo(nuevoEstado);
        productoRepository.save(producto);

        String mensaje = nuevoEstado
                ? "Producto Habilitado exitosamente"
                : "Producto deshabilitado exitosamente";

        return new BaseResponse(mensaje, true);
    }

    /**
     * Elimina un producto y rompe sus relaciones con categoría y subcategoria para evitar
     * errores de integridad referencial en la base de datos.
     *
     * @param id ID del producto a eliminar
     * @return Respuesta con mensaje de confirmación y con un boolean que define si la operación fue exitosa o no.
     * @throws EntityNotFoundException si el producto no existe
     */
    @Override
    @Transactional
    public BaseResponse deleteProducto(Long id) {

        Producto producto = this.getProductoById(id);

        producto.getCategoria().deleteProducto(producto);
        producto.getSubCategoria().deleteProducto(producto);
        producto.getProveedor().deleteProducto(producto);

        productoRepository.delete(producto);
        return new BaseResponse("Producto eliminado exitosamente", true);
    }

    /**
     * Convierte una entidad Producto a su DTO de respuesta, incluyendo
     * categoria y subcategoria.
     * Para uso interno del servicio en los métodos de búsqueda
     *
     * @param producto Entidad producto a convertir
     * @return DTO con la información completa del producto
     */
    @Override
    public ProductoResponse toResponse(Producto producto) {
        return new ProductoResponse(
                producto.getProductoId(),
                producto.getCodigoProducto(),
                cloudinaryService.getImagenUrl(producto.getImagen()),
                producto.getNombre(),
                producto.getMarca(),
                producto.getPais(),
                producto.getETipo(),
                producto.getPrecio(),
                producto.getPrecioRegular(),
                producto.getStock(),
                producto.getDescripcion(),
                producto.isActivo(),
                producto.getCategoria().getCategoriaId(),
                producto.getCategoria().getNombre(),
                producto.getSubCategoria().getSubCategoriaId() != null ? producto.getSubCategoria().getSubCategoriaId() : null,
                producto.getSubCategoria().getNombre(),
                producto.getProveedor().getUsuarioId(),
                producto.getProveedor().getNombres() + producto.getProveedor().getApellidos(),
                producto.getProveedor().getEmpresa().getRazonSocial()
        );
    }
}