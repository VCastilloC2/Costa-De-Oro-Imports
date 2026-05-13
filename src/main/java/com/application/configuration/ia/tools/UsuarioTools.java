package com.application.configuration.ia.tools;

import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.usuario.response.ClienteResponse;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UsuarioTools {

    private final UsuarioService usuarioService;

    @Tool(
            name = "obtener_usuario_por_correo",
            description = "Obtiene un usuario usando el correo electrónico"
    )
    public String getUsuarioByCorreo(
            @ToolParam(description = "Correo del usuario a buscar") String correo) {
        try {
            Usuario u = usuarioService.getUsuarioByCorreo(correo);
            return """
                    Usuario encontrado:
                    ID: %d
                    Nombre: %s
                    Correo: %s
                    Rol: %s
                    Estado: %s
                    """.formatted(
                    u.getUsuarioId(),
                    (u.getNombres() != null && u.getApellidos()!= null) ? u.getNombres()+" "+u.getApellidos() : "No especificado",
                    u.getCorreo(),
                    u.getRol() != null ? u.getRol().getName() : "No especificado",
                    u.isEnabled() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "Usuario no encontrado con correo: " + correo;
        }
    }

    @Tool(
            name = "obtener_total_clientes",
            description = "Obtiene el total de clientes registrados"
    )
    public String getTotalClientes() {
        Long total = usuarioService.getTotalClientes();
        return "Total de clientes registrados: " + total;
    }

    @Tool(
            name = "obtener_cliente_por_id",
            description = "Obtiene un cliente usando su ID"
    )
    public String getClienteById(Long clienteId) {
        try {
            ClienteResponse cliente = usuarioService.getClienteById(clienteId);
            return """
                    Cliente encontrado:
                    ID: %d
                    Nombre: %s
                    Correo: %s
                    Teléfono: %s
                    Dirección: %s
                    """.formatted(
                    cliente.clienteId(),
                    cliente.nombres() != null ? cliente.nombres() : "No especificado",
                    cliente.correo() != null ? cliente.correo() : "No especificado",
                    cliente.telefono() != null ? cliente.telefono() : "No especificado",
                    cliente.direccion() != null ? cliente.direccion() : "No especificado"
            );
        } catch (Exception e) {
            return "Cliente no encontrado con ID: " + clienteId;
        }
    }

    /*

    @Tool(
            name = "obtener_proveedor_por_id",
            description = "Obtiene un proveedor usando su ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String getProveedorById(Long proveedorId) {
        try {
            ProveedorResponse proveedor = usuarioService.getProveedorById(proveedorId);
            return """
                    🏢 Proveedor encontrado:
                    ID: %d
                    Nombre: %s
                    Correo: %s
                    Teléfono: %s
                    Dirección: %s
                    Estado: %s
                    """.formatted(
                    proveedor.getId(),
                    proveedor.getNombre() != null ? proveedor.getNombre() : "No especificado",
                    proveedor.getCorreo() != null ? proveedor.getCorreo() : "No especificado",
                    proveedor.getTelefono() != null ? proveedor.getTelefono() : "No especificado",
                    proveedor.getDireccion() != null ? proveedor.getDireccion() : "No especificado",
                    proveedor.isActivo() ? "Activo" : "Inactivo"
            );
        } catch (Exception e) {
            return "❌ Proveedor no encontrado con ID: " + proveedorId;
        }
    }

    @Tool(
            name = "obtener_usuarios_con_mayor_gasto",
            description = "Obtiene los usuarios con gastos del último año"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String getUsuarioGastoUltimoAnio() {
        List<UsuarioGastoResponse> usuarios = usuarioService.getUsuarioGastoUltimoAnio();

        if (usuarios == null || usuarios.isEmpty()) {
            return "📊 No hay usuarios con gastos registrados en el último año.";
        }

        StringBuilder result = new StringBuilder("💰 TOP USUARIOS CON MAYOR GASTO (Último año):\n\n");
        for (int i = 0; i < usuarios.size(); i++) {
            UsuarioGastoResponse u = usuarios.get(i);
            result.append(String.format("%d. %s - Total gastado: $%.2f\n",
                    i + 1,
                    u.getNombre() != null ? u.getNombre() : "Usuario " + u.getId(),
                    u.getTotalGasto()
            ));
        }
        return result.toString();
    }

    @Tool(
            name = "obtener_estadisticas_proveedores",
            description = "Obtiene estadísticas de los proveedores"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String getProveedorConEstadisticas() {
        List<ProveedorEstadisticasResponse> proveedores = usuarioService.getProveedorConEstadisticas();

        if (proveedores == null || proveedores.isEmpty()) {
            return "📊 No hay estadísticas de proveedores disponibles.";
        }

        StringBuilder result = new StringBuilder("📈 ESTADÍSTICAS DE PROVEEDORES:\n\n");
        for (ProveedorEstadisticasResponse p : proveedores) {
            result.append(String.format("🏢 %s\n", p.getNombreProveedor()));
            result.append(String.format("   📦 Productos suministrados: %d\n", p.getTotalProductos()));
            result.append(String.format("   💰 Ventas totales: $%.2f\n", p.getVentasTotales()));
            result.append(String.format("   ⭐ Calificación promedio: %.1f/5\n\n", p.getCalificacionPromedio()));
        }
        return result.toString();
    }

    @Tool(
            name = "obtener_proveedores_activos",
            description = "Obtiene la lista de proveedores activos"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String getProveedoresActivos() {
        List<ProveedorProductoResponse> proveedores = usuarioService.getProveedoresActivos();

        if (proveedores == null || proveedores.isEmpty()) {
            return "📋 No hay proveedores activos registrados.";
        }

        StringBuilder result = new StringBuilder("✅ PROVEEDORES ACTIVOS:\n\n");
        for (ProveedorProductoResponse p : proveedores) {
            result.append(String.format("🏢 %s - %s\n",
                    p.getNombreProveedor(),
                    p.getCategoria() != null ? p.getCategoria() : "Sin categoría"
            ));
            result.append(String.format("   📞 %s\n", p.getTelefono() != null ? p.getTelefono() : "Sin teléfono"));
            result.append(String.format("   📦 %d productos disponibles\n\n", p.getCantidadProductos()));
        }
        return result.toString();
    }

    @Tool(
            name = "crear_usuario",
            description = "Crea un nuevo usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String createUser(CreateUsuarioRequest request) {
        try {
            BaseResponse response = usuarioService.createUser(request);
            if (response.isSuccess()) {
                return "✅ Usuario creado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear usuario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear usuario: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_cliente",
            description = "Crea un nuevo cliente"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String createCliente(CreateClienteRequest request) {
        try {
            BaseResponse response = usuarioService.createCliente(request);
            if (response.isSuccess()) {
                return "✅ Cliente creado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear cliente: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear cliente: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_proveedor",
            description = "Crea un nuevo proveedor"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String createProveedor(CreateProveedorRequest request) {
        try {
            BaseResponse response = usuarioService.createProveedor(request);
            if (response.isSuccess()) {
                return "✅ Proveedor creado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al crear proveedor: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al crear proveedor: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_cliente",
            description = "Actualiza la información de un cliente"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String updateCliente(Long clienteId, CreateClienteRequest request) {
        try {
            BaseResponse response = usuarioService.updateCliente(clienteId, request);
            if (response.isSuccess()) {
                return "✅ Cliente con ID " + clienteId + " actualizado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar cliente: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar cliente con ID " + clienteId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_proveedor",
            description = "Actualiza la información de un proveedor"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String updateProveedor(Long proveedorId, CreateProveedorRequest request) {
        try {
            BaseResponse response = usuarioService.updateProveedor(proveedorId, request);
            if (response.isSuccess()) {
                return "✅ Proveedor con ID " + proveedorId + " actualizado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar proveedor: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar proveedor con ID " + proveedorId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_usuario",
            description = "Actualiza la información del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public String updateUser(CustomUserPrincipal principal, UpdateUsuarioRequest request) {
        try {
            GeneralResponse response = usuarioService.updateUser(principal, request);
            if (response.isSuccess()) {
                return "✅ Usuario actualizado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar usuario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar usuario: " + e.getMessage();
        }
    }

    @Tool(
            name = "completar_perfil_usuario",
            description = "Completa el perfil del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public String completeUserProfile(CustomUserPrincipal principal, CompleteUsuarioProfileRequest request) {
        try {
            GeneralResponse response = usuarioService.completeUserProfile(principal, request);
            if (response.isSuccess()) {
                return "✅ Perfil completado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al completar perfil: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al completar perfil: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_foto_usuario",
            description = "Actualiza la foto del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public String setUserPhoto(CustomUserPrincipal principal, SetUsuarioPhotoRequest request) {
        try {
            GeneralResponse response = usuarioService.setUserPhoto(principal, request);
            if (response.isSuccess()) {
                return "✅ Foto de perfil actualizada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar foto: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar foto: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_password_usuario",
            description = "Actualiza la contraseña del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public String updatePassword(CustomUserPrincipal principal, UpdatePasswordRequest request) {
        try {
            BaseResponse response = usuarioService.updatePassword(principal, request);
            if (response.isSuccess()) {
                return "✅ Contraseña actualizada exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al actualizar contraseña: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al actualizar contraseña: " + e.getMessage();
        }
    }

    @Tool(
            name = "deshabilitar_usuario",
            description = "Deshabilita o habilita un usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String changeEstadoUsuario(Long usuarioId) {
        try {
            BaseResponse response = usuarioService.changeEstadoUsuario(usuarioId);
            if (response.isSuccess()) {
                return "✅ Cambio de estado aplicado exitosamente al usuario ID " + usuarioId + ".\n" + response.getMessage();
            } else {
                return "❌ Error al cambiar estado: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al cambiar estado del usuario ID " + usuarioId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_usuario",
            description = "Elimina un usuario usando su ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public String deleteUsuario(Long usuarioId) {
        try {
            BaseResponse response = usuarioService.deleteUsuario(usuarioId);
            if (response.isSuccess()) {
                return "✅ Usuario con ID " + usuarioId + " eliminado exitosamente.\n" + response.getMessage();
            } else {
                return "❌ Error al eliminar usuario: " + response.getMessage();
            }
        } catch (Exception e) {
            return "❌ Error al eliminar usuario con ID " + usuarioId + ": " + e.getMessage();
        }
    }
     */

}