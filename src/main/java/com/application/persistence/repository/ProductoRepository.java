package com.application.persistence.repository;

import com.application.persistence.entity.producto.Producto;
import com.application.presentation.dto.factura.response.FacturaProductoProveedorResponse;
import com.application.presentation.dto.grafica.columnasApiladas.StockCategoriaDTO;
import com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductoRepository extends JpaRepository<Producto, Long> {

    List<Producto> findByCategoria_CategoriaId(Long categoriaId);

    @Query("""
            SELECT p
              FROM Producto p
              LEFT JOIN p.detalleVentas dv
             WHERE p.activo = true
             GROUP BY p
             ORDER BY COALESCE(SUM(dv.cantidad), 0) DESC
            """)
    List<Producto> findProductosActivos();

    @Query("""
            SELECT p
              FROM Producto p
              LEFT JOIN p.detalleVentas dv
             WHERE p.activo = true
               AND p.eTipo <> 'UNIDAD'
             GROUP BY p
             ORDER BY COALESCE(SUM(dv.cantidad),0) DESC
            """)
    List<Producto> findProductosActivosMasVendidosNoUnidad();

    @Query("""
            SELECT p
              FROM Producto p
              JOIN p.detalleVentas dv
             WHERE p.activo = true
             GROUP BY p
             ORDER BY SUM(dv.cantidad) DESC
            """)
    List<Producto> findProductosMasVendidosActivos();

    @Query("""
                SELECT p
                  FROM Producto p
                  JOIN p.detalleVentas dv
                 WHERE p.categoria.categoriaId = :categoriaId
                   AND p.activo = true
                 GROUP BY p
                 ORDER BY SUM(dv.cantidad) DESC
            """)
    List<Producto> findProductosMasVendidosByCategoriaIdActivos(@Param("categoriaId") Long categoriaId);

    @Query("""
                SELECT p
                  FROM Producto p
                  JOIN p.detalleVentas dv
                 WHERE p.categoria.categoriaId IN :categoriaIds
                   AND p.activo = true
                 GROUP BY p
                 ORDER BY SUM(dv.cantidad) DESC
            """)
    List<Producto> findProductosMasVendidosByCategoriaIdsActivos(@Param("categoriaIds") List<Long> categoriaIds);

    @Query("SELECT p.stock FROM Producto p WHERE p.productoId = :productoId")
    Integer findStockByProductoId(@Param("productoId") Long productoId);

    @Query("SELECT DISTINCT p.pais FROM Producto p ORDER BY p.pais ASC")
    List<String> findDistinctPaises();

    @Query("SELECT DISTINCT p.marca FROM Producto p ORDER BY p.marca ASC")
    List<String> findDistinctMarcas();

    @Query("SELECT p FROM Producto p ORDER BY p.precio ASC")
    List<Producto> findAllOrderByPrecioAsc();

    @Query("SELECT p FROM Producto p ORDER BY p.precio DESC")
    List<Producto> findAllOrderByPrecioDesc();

    @Query("SELECT p FROM Producto p ORDER BY p.nombre ASC")
    List<Producto> findAllOrderByNombreAsc();

    @Query("SELECT p FROM Producto p ORDER BY p.nombre DESC")
    List<Producto> findAllOrderByNombreDesc();

    @Query("""
                SELECT DISTINCT c.nombre, sc.nombre
                FROM Producto p
                JOIN p.categoria c
                LEFT JOIN p.subCategoria sc
                WHERE c.activo = true
                ORDER BY c.nombre ASC, sc.nombre ASC
            """)
    List<Object[]> findCategoriasActivasConSubcategorias();

    @Query("""
                SELECT p, SUM(d.cantidad) AS totalVendido
                FROM DetalleVenta d
                JOIN d.producto p
                GROUP BY p.productoId
                ORDER BY totalVendido DESC
            """)
    List<Object[]> findProductosMasVendidos();

    @Query("""
                SELECT p 
                FROM Producto p 
                LEFT JOIN p.categoria c
                LEFT JOIN p.subCategoria sc
                WHERE p.activo = true
                  AND (:paises IS NULL OR p.pais IN :paises)
                  AND (:marcas IS NULL OR p.marca IN :marcas)
                  AND (:categorias IS NULL OR c.nombre IN :categorias)
                  AND (:subcategorias IS NULL OR sc.nombre IN :subcategorias)
                  AND (:precioMin IS NULL OR p.precio >= :precioMin)
                  AND (:precioMax IS NULL OR p.precio <= :precioMax)
                ORDER BY p.nombre ASC
            """)
    List<Producto> findByFiltros(
            @Param("paises") List<String> paises,
            @Param("marcas") List<String> marcas,
            @Param("categorias") List<String> categorias,
            @Param("subcategorias") List<String> subcategorias,
            @Param("precioMin") Double precioMin,
            @Param("precioMax") Double precioMax
    );

    @Query("SELECT new com.application.presentation.dto.grafica.columnasApiladas.StockCategoriaDTO(" +
            "c.nombre, SUM(p.stock)) " +
            "FROM Producto p " +
            "JOIN p.categoria c " +
            "WHERE p.activo = true " +
            "GROUP BY c.categoriaId, c.nombre " +
            "ORDER BY SUM(p.stock) DESC")
    List<StockCategoriaDTO> findStockPorCategoria();

    @Query("SELECT COALESCE(SUM(p.stock), 0) FROM Producto p WHERE p.activo = true")
    Long findStockTotal();

    @Query("""
                SELECT new com.application.presentation.dto.grafica.productosMasVendidos.ProductoMasVendidoResponse(
                    p.productoId,
                    p.nombre,
                    p.imagen,
                    p.precio,
                    SUM(dv.cantidad) as totalVentas
                )
                FROM Producto p
                JOIN p.detalleVentas dv
                JOIN dv.compra c
                WHERE c.estado = 'PAGADO'
                AND p.activo = true
                GROUP BY p.productoId, p.nombre, p.imagen, p.precio
                ORDER BY totalVentas DESC
                LIMIT 4
            """)
    List<ProductoMasVendidoResponse> findTopProductosMasVendidos();

    // Obtener productos activos de un proveedor específico
    @Query("SELECT new com.application.presentation.dto.factura.response.FacturaProductoProveedorResponse(" +
            "p.productoId, p.nombre, p.codigoProducto, p.marca, p.precio, p.stock) " +
            "FROM Producto p " +
            "WHERE p.proveedor.usuarioId = :proveedorId AND p.activo = true " +
            "ORDER BY p.nombre")
    List<FacturaProductoProveedorResponse> findProductosActivosByProveedorId(@Param("proveedorId") Long proveedorId);

    // Encontrar producto por ID y verificar que pertenezca al proveedor
    @Query("SELECT p FROM Producto p WHERE p.productoId = :productoId AND p.proveedor.usuarioId = :proveedorId")
    Optional<Producto> findByIdAndProveedorId(@Param("productoId") Long productoId, @Param("proveedorId") Long proveedorId);

    List<Producto> findByNombreContainingIgnoreCaseOrMarcaContainingIgnoreCaseOrDescripcionContainingIgnoreCase(
            String nombre,
            String marca,
            String descripcion
    );



}