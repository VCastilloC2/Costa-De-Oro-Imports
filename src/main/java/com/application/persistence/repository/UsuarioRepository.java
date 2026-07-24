package com.application.persistence.repository;

import com.application.persistence.entity.rol.enums.ERol;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.usuario.response.ProveedorEstadisticasResponse;
import com.application.presentation.dto.usuario.response.UsuarioGastoResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long> {
    Optional<Usuario> findByCorreo(String correo);

    boolean existsByCorreo(String correo);

    List<Usuario> findByRol_NameAndEmpresa_ActivoTrue(ERol name);

    List<Usuario> findByRol_Name(ERol rol);

    List<Usuario> findByRol_NameAndIsEnabledTrue(ERol rol);

    List<Usuario> findTop5ByOrderByUsuarioIdAsc();

    List<Usuario> findTop10ByRol_Name(ERol rol);

    @Query("""
                SELECT new com.application.presentation.dto.usuario.response.UsuarioGastoResponse(
                    u.usuarioId,
                    u.imagen,
                    CONCAT(u.nombres, ' ', u.apellidos),
                    u.correo,
                    u.telefono,
                    COALESCE(SUM(c.total), 0),
                    u.isEnabled
                )
                FROM Usuario u
                LEFT JOIN u.compras c 
                    ON c.fecha >= :fechaInicio 
                    AND c.estado = com.application.persistence.entity.compra.enums.EEstado.PAGADO
                WHERE u.accountNonLocked = true
                AND u.rol.name IN ('INVITADO', 'PERSONA_CONTACTO', 'PERSONA_JURIDICA', 'PERSONA_NATURAL')
                GROUP BY u.usuarioId, u.imagen, u.nombres, u.apellidos, u.correo, u.telefono, u.isEnabled
                ORDER BY COALESCE(SUM(c.total), 0) DESC
            """)
    List<UsuarioGastoResponse> obtenerUsuariosConGastoUltimoAnio(@Param("fechaInicio") LocalDateTime fechaInicio);

    @Query(value = """
                SELECT COUNT(*) 
                FROM usuario u
                JOIN rol r ON u.rol_id = r.rol_id
                WHERE r.rol IN ('PERSONA_CONTACTO', 'PERSONA_JURIDICA', 'PERSONA_NATURAL')
            """, nativeQuery = true)
    Long totalClientes();

    @Query("""
            SELECT new com.application.presentation.dto.usuario.response.ProveedorEstadisticasResponse(
                u.usuarioId,
                e.imagen,
                e.razonSocial,
                CONCAT(u.nombres, ' ', u.apellidos),
                u.correo,
                CAST(COALESCE(SUM(fp.total), 0) AS double),
                CAST(COALESCE(
                    (
                        SELECT SUM(dv.subtotal)
                        FROM DetalleVenta dv
                        JOIN dv.compra c
                        JOIN dv.producto p
                        WHERE p.proveedor.usuarioId = u.usuarioId
                          AND c.estado = com.application.persistence.entity.compra.enums.EEstado.PAGADO
                          AND YEAR(c.fecha) = YEAR(CURRENT_DATE)
                    ), 0
                ) AS double),
                u.isEnabled
            )
            FROM Usuario u
            JOIN u.empresa e
            JOIN u.rol r
            LEFT JOIN FacturaProveedor fp ON u.usuarioId = fp.usuario.usuarioId
                AND fp.activo = true
                AND YEAR(fp.fechaEmision) = YEAR(CURRENT_DATE)
            WHERE r.name = com.application.persistence.entity.rol.enums.ERol.PROVEEDOR
              AND u.accountNonLocked = true
            GROUP BY u.usuarioId, e.imagen, e.razonSocial, u.nombres, u.apellidos, u.correo, u.isEnabled
            ORDER BY COALESCE(SUM(fp.total), 0) DESC
            """)
    List<ProveedorEstadisticasResponse> findProveedoresConEstadisticas();


    @Query("""
            SELECT u
            FROM Usuario u
            WHERE u.accountNonLocked = true
            AND (
            LOWER(u.nombres) LIKE LOWER(CONCAT('%',:query,'%'))
            OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%',:query,'%'))
            OR LOWER(u.correo) LIKE LOWER(CONCAT('%',:query,'%'))
            )
            AND u.rol.name IN (
            com.application.persistence.entity.rol.enums.ERol.PERSONA_CONTACTO,
            com.application.persistence.entity.rol.enums.ERol.PERSONA_NATURAL,
            com.application.persistence.entity.rol.enums.ERol.PERSONA_JURIDICA,
            com.application.persistence.entity.rol.enums.ERol.INVITADO
            )
            """)
    List<Usuario> buscarClientes(@Param("query") String query);

    @Query("""
            SELECT u
            FROM Usuario u
            WHERE u.accountNonLocked = true
            AND u.rol.name =
            com.application.persistence.entity.rol.enums.ERol.PROVEEDOR
            AND (
            LOWER(u.nombres) LIKE LOWER(CONCAT('%',:query,'%'))
            OR LOWER(u.apellidos) LIKE LOWER(CONCAT('%',:query,'%'))
            OR LOWER(u.correo) LIKE LOWER(CONCAT('%',:query,'%'))
            OR LOWER(u.empresa.razonSocial) LIKE LOWER(CONCAT('%',:query,'%'))
            )
            """)
    List<Usuario> buscarProveedores(@Param("query") String query);

    List<Usuario> findByNombresContainingIgnoreCaseOrApellidosContainingIgnoreCaseOrCorreoContainingIgnoreCase(
            String nombres,
            String apellidos,
            String correo
    );
}