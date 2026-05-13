package com.application.configuration.ia.tools;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.usuario.response.ClienteResponse;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;
import org.springframework.stereotype.Component;
import org.springframework.security.access.prepost.PreAuthorize;
import com.application.presentation.dto.usuario.response.ProveedorResponse;
import com.application.presentation.dto.usuario.response.UsuarioGastoResponse;
import com.application.presentation.dto.usuario.response.ProveedorEstadisticasResponse;
import com.application.presentation.dto.usuario.response.ProveedorProductoResponse;
import com.application.presentation.dto.usuario.request.CreateUsuarioRequest;
import com.application.presentation.dto.usuario.request.CreateClienteRequest;
import com.application.presentation.dto.usuario.request.CreateProveedorRequest;
import com.application.presentation.dto.usuario.request.UpdateUsuarioRequest;
import com.application.presentation.dto.usuario.request.CompleteUsuarioProfileRequest;
import com.application.presentation.dto.usuario.request.SetUsuarioPhotoRequest;
import com.application.presentation.dto.usuario.request.UpdatePasswordRequest;
import java.util.List;

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
                    Numero de Identificación: %s
                    """.formatted(
                    cliente.clienteId(),
                    cliente.nombres() != null ? cliente.nombres() : "No especificado",
                    cliente.correo() != null ? cliente.correo() : "No especificado",
                    cliente.telefono() != null ? cliente.telefono() : "No especificado",
                    cliente.direccion() != null ? cliente.direccion() : "No especificado",
                    cliente.numeroIdentificacion() != null ? cliente.numeroIdentificacion() : "No especificado"
            );
        } catch (Exception e) {
            return "Cliente no encontrado con ID: " + clienteId;
        }
    }


    @Tool(
            name = "obtener_proveedor_por_id",
            description = "Obtiene un proveedor usando su ID"
    )
    public String getProveedorById(Long proveedorId) {
        try {
            ProveedorResponse proveedor = usuarioService.getProveedorById(proveedorId);
            return """
                    Proveedor encontrado:
                    ID: %d
                    Nombre: %s
                    Correo: %s
                    Teléfono: %s
                    Dirección: %s
                    Teléfono de la empresa: %s
                    Dirección de la empresa: %s
                    Ciudad: %s
                    """.formatted(
                    proveedor.proveedorId(),
                    proveedor.nombres() != null ? proveedor.nombres() : "No especificado",
                    proveedor.correo() != null ? proveedor.correo() : "No especificado",
                    proveedor.telefono() != null ? proveedor.telefono() : "No especificado",
                    proveedor.direccion() != null ? proveedor.direccion() : "No especificado",
                    proveedor.telefonoEmpresa() != null ? proveedor.telefonoEmpresa() : "No especificado",
                    proveedor.direccionEmpresa() != null ? proveedor.direccionEmpresa() : "No especificado",
                    proveedor.ciudad() != null ? proveedor.ciudad() : "No especificado"
            );
        } catch (Exception e) {
            return "Proveedor no encontrado con ID: " + proveedorId;
        }
    }

    @Tool(
            name = "obtener_usuarios_con_mayor_gasto",
            description = "Obtiene los usuarios con gastos del último año"
    )
    public String getUsuarioGastoUltimoAnio() {
        List<UsuarioGastoResponse> usuarios = usuarioService.getUsuarioGastoUltimoAnio();

        if (usuarios == null || usuarios.isEmpty()) {
            return "No hay usuarios con gastos registrados en el último año.";
        }

        StringBuilder result = new StringBuilder("TOP USUARIOS CON MAYOR GASTO (Último año):\n\n");
        for (int i = 0; i < usuarios.size(); i++) {
            UsuarioGastoResponse u = usuarios.get(i);
            result.append(String.format("%d. %s - Total gastado: $%.2f\n",
                    i + 1,
                    u.nombreCompleto() != null ? u.nombreCompleto() : "Usuario " + u.usuarioId(),
                    u.totalGastado()
            ));
        }
        return result.toString();
    }

    @Tool(
            name = "obtener_estadisticas_proveedores",
            description = "Obtiene estadísticas de los proveedores"
    )
    public String getProveedorConEstadisticas() {
        List<ProveedorEstadisticasResponse> proveedores = usuarioService.getProveedorConEstadisticas();

        if (proveedores == null || proveedores.isEmpty()) {
            return "No hay estadísticas de proveedores disponibles.";
        }

        StringBuilder result = new StringBuilder("ESTADÍSTICAS DE PROVEEDORES:\n\n");
        for (ProveedorEstadisticasResponse p : proveedores) {
            result.append(String.format("%s\n", p.nombreCompleto()));
            result.append(String.format("   Ventas Totales Ganadas: $%.2f\n", p.totalGanado()));
            result.append(String.format("   Ventas Totales Gastada: %.1f/5\n\n", p.totalGastado()));
        }
        return result.toString();
    }

    @Tool(
            name = "obtener_proveedores_activos",
            description = "Obtiene la lista de proveedores activos"
    )
    public String getProveedoresActivos() {
        List<ProveedorProductoResponse> proveedores = usuarioService.getProveedoresActivos();

        if (proveedores == null || proveedores.isEmpty()) {
            return "No hay proveedores activos registrados.";
        }

        StringBuilder result = new StringBuilder("PROVEEDORES ACTIVOS:\n\n");
        for (ProveedorProductoResponse p : proveedores) {
            result.append(String.format("%s - %s\n",
                    p.nombreUsuario(),
                    p.nombreEmpresa()
            ));
        }
        return result.toString();
    }

    @Tool(
            name = "crear_usuario",
            description = "Crea un nuevo usuario"
    )
    public String createUser(CreateUsuarioRequest request) {
        try {
            BaseResponse response = usuarioService.createUser(request);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al crear usuario: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_cliente",
            description = "Crea un nuevo cliente"
    )
    public String createCliente(CreateClienteRequest request) {
        try {
            BaseResponse response = usuarioService.createCliente(request);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al crear cliente: " + e.getMessage();
        }
    }

    @Tool(
            name = "crear_proveedor",
            description = "Crea un nuevo proveedor"
    )
    public String createProveedor(CreateProveedorRequest request) {
        try {
            BaseResponse response = usuarioService.createProveedor(request);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al crear proveedor: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_cliente",
            description = "Actualiza la información de un cliente"
    )
    public String updateCliente(Long clienteId, CreateClienteRequest request) {
        try {
            BaseResponse response = usuarioService.updateCliente(clienteId, request);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al actualizar cliente con ID " + clienteId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_proveedor",
            description = "Actualiza la información de un proveedor"
    )
    public String updateProveedor(Long proveedorId, CreateProveedorRequest request) {
        try {
            BaseResponse response = usuarioService.updateProveedor(proveedorId, request);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al actualizar proveedor con ID " + proveedorId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_usuario",
            description = "Actualiza la información del usuario autenticado"
    )
    public String updateUser(CustomUserPrincipal principal, UpdateUsuarioRequest request) {
        try {
            GeneralResponse response = usuarioService.updateUser(principal, request);
            if (response.mensaje().equals("Sus datos se han actualizado exitosamente") ) {
                return response.mensaje();
            } else {
                return "Sus datos no se han actualizado: " + response.mensaje();
            }
        } catch (Exception e) {
            return "Error al actualizar usuario: " + e.getMessage();
        }
    }

    @Tool(
            name = "completar_perfil_usuario",
            description = "Completa el perfil del usuario autenticado"
    )
    public String completeUserProfile(CustomUserPrincipal principal, CompleteUsuarioProfileRequest request) {
        try {
            GeneralResponse response = usuarioService.completeUserProfile(principal, request);
            if (response.mensaje().equals("Registro completado exitosamente.")) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al completar perfil: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_foto_usuario",
            description = "Actualiza la foto del usuario autenticado"
    )
    public String setUserPhoto(CustomUserPrincipal principal, SetUsuarioPhotoRequest request) {
        try {
            GeneralResponse response = usuarioService.setUserPhoto(principal, request);
            if (response.mensaje().equals("Imagen asignada exitosamente")) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al actualizar foto: " + e.getMessage();
        }
    }

    @Tool(
            name = "actualizar_password_usuario",
            description = "Actualiza la contraseña del usuario autenticado"
    )
    public String updatePassword(CustomUserPrincipal principal, UpdatePasswordRequest request) {
        try {
            BaseResponse response = usuarioService.updatePassword(principal, request);
            if (response.success()) {
                return "Contraseña actualizada exitosamente.\n" + response.mensaje();
            } else {
                return "Error al actualizar contraseña: " + response.mensaje();
            }
        } catch (Exception e) {
            return "Error al actualizar contraseña: " + e.getMessage();
        }
    }

    @Tool(
            name = "deshabilitar_usuario",
            description = "Deshabilita o habilita un usuario"
    )
    public String changeEstadoUsuario(Long usuarioId) {
        try {
            BaseResponse response = usuarioService.changeEstadoUsuario(usuarioId);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al cambiar estado del usuario ID " + usuarioId + ": " + e.getMessage();
        }
    }

    @Tool(
            name = "eliminar_usuario",
            description = "Elimina un usuario usando su ID"
    )
    public String deleteUsuario(Long usuarioId) {
        try {
            BaseResponse response = usuarioService.deleteUsuario(usuarioId);
            if (response.success()) {
                return response.mensaje();
            } else {
                return response.mensaje();
            }
        } catch (Exception e) {
            return "Error al eliminar usuario con ID " + usuarioId + ": " + e.getMessage();
        }
    }

}