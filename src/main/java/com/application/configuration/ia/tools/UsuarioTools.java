package com.application.configuration.ia.tools;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.usuario.Usuario;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.usuario.request.*;
import com.application.presentation.dto.usuario.response.*;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;
import org.springframework.ai.tool.annotation.Tool;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UsuarioTools {

    private final UsuarioService usuarioService;

    @Tool(
            name = "obtener_usuario_por_correo",
            description = "Obtiene un usuario usando el correo electrónico"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Usuario getUsuarioByCorreo(String correo) {
        return usuarioService.getUsuarioByCorreo(correo);
    }

    @Tool(
            name = "obtener_total_clientes",
            description = "Obtiene el total de clientes registrados"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public Long getTotalClientes() {
        return usuarioService.getTotalClientes();
    }

    @Tool(
            name = "obtener_cliente_por_id",
            description = "Obtiene un cliente usando su ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ClienteResponse getClienteById(Long clienteId) {
        return usuarioService.getClienteById(clienteId);
    }

    @Tool(
            name = "obtener_proveedor_por_id",
            description = "Obtiene un proveedor usando su ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public ProveedorResponse getProveedorById(Long proveedorId) {
        return usuarioService.getProveedorById(proveedorId);
    }

    @Tool(
            name = "obtener_usuarios_con_mayor_gasto",
            description = "Obtiene los usuarios con gastos del último año"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioGastoResponse> getUsuarioGastoUltimoAnio() {
        return usuarioService.getUsuarioGastoUltimoAnio();
    }

    @Tool(
            name = "obtener_estadisticas_proveedores",
            description = "Obtiene estadísticas de los proveedores"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProveedorEstadisticasResponse> getProveedorConEstadisticas() {
        return usuarioService.getProveedorConEstadisticas();
    }

    @Tool(
            name = "obtener_proveedores_activos",
            description = "Obtiene la lista de proveedores activos"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public List<ProveedorProductoResponse> getProveedoresActivos() {
        return usuarioService.getProveedoresActivos();
    }

    @Tool(
            name = "crear_usuario",
            description = "Crea un nuevo usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse createUser(CreateUsuarioRequest request) {
        return usuarioService.createUser(request);
    }

    @Tool(
            name = "crear_cliente",
            description = "Crea un nuevo cliente"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse createCliente(CreateClienteRequest request) {
        return usuarioService.createCliente(request);
    }

    @Tool(
            name = "crear_proveedor",
            description = "Crea un nuevo proveedor"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse createProveedor(CreateProveedorRequest request) {
        return usuarioService.createProveedor(request);
    }

    @Tool(
            name = "actualizar_cliente",
            description = "Actualiza la información de un cliente"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse updateCliente(
            Long clienteId,
            CreateClienteRequest request
    ) {
        return usuarioService.updateCliente(clienteId, request);
    }

    @Tool(
            name = "actualizar_proveedor",
            description = "Actualiza la información de un proveedor"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse updateProveedor(
            Long proveedorId,
            CreateProveedorRequest request
    ) {
        return usuarioService.updateProveedor(proveedorId, request);
    }

    @Tool(
            name = "actualizar_usuario",
            description = "Actualiza la información del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public GeneralResponse updateUser(
            CustomUserPrincipal principal,
            UpdateUsuarioRequest request
    ) {
        return usuarioService.updateUser(principal, request);
    }

    @Tool(
            name = "completar_perfil_usuario",
            description = "Completa el perfil del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public GeneralResponse completeUserProfile(
            CustomUserPrincipal principal,
            CompleteUsuarioProfileRequest request
    ) {
        return usuarioService.completeUserProfile(principal, request);
    }

    @Tool(
            name = "actualizar_foto_usuario",
            description = "Actualiza la foto del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public GeneralResponse setUserPhoto(
            CustomUserPrincipal principal,
            SetUsuarioPhotoRequest request
    ) {
        return usuarioService.setUserPhoto(principal, request);
    }

    @Tool(
            name = "actualizar_password_usuario",
            description = "Actualiza la contraseña del usuario autenticado"
    )
    @PreAuthorize("isAuthenticated()")
    public BaseResponse updatePassword(
            CustomUserPrincipal principal,
            UpdatePasswordRequest request
    ) {
        return usuarioService.updatePassword(principal, request);
    }

    @Tool(
            name = "deshabilitar_usuario",
            description = "Deshabilita o habilita un usuario"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse changeEstadoUsuario(Long usuarioId) {
        return usuarioService.changeEstadoUsuario(usuarioId);
    }

    @Tool(
            name = "eliminar_usuario",
            description = "Elimina un usuario usando su ID"
    )
    @PreAuthorize("hasRole('ADMIN')")
    public BaseResponse deleteUsuario(Long usuarioId) {
        return usuarioService.deleteUsuario(usuarioId);
    }

}