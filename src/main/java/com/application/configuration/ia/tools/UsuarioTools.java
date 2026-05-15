package com.application.configuration.ia.tools;

import com.application.presentation.dto.usuario.response.ProveedorProductoResponse;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import com.application.persistence.entity.rol.Rol;
import com.application.persistence.entity.rol.enums.ERol;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.persistence.repository.RolRepository;
import com.application.persistence.repository.UsuarioRepository;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.crypto.password.PasswordEncoder;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class UsuarioTools {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;
    private final RolRepository rolRepository;
    private final PasswordEncoder encoder;

    // Límites para evitar sobrecarga
    private static final int MAX_RESULTADOS = 5;
    private static final int MAX_PROVEEDORES = 5;

    @Tool(
            name = "crear_usuario",
            description = """
                    Crea un nuevo usuario.
                    Usa siempre este tool cuando el usuario pida registrar, crear o añadir usuarios.
                    Parámetros requeridos:
                    - nombres, apellidos, correo, password
                    - telefono, tipoIdentificacion (CC, TI, NIT, Pasaporte)
                    - numeroIdentificacion
                    """
    )
    public String crearUsuario(
            String nombres,
            String apellidos,
            String correo,
            String password,
            String telefono,
            String tipoIdentificacion,
            String numeroIdentificacion
    ) {
        // Validaciones básicas
        if (nombres == null || nombres.isBlank() || apellidos == null || apellidos.isBlank()) {
            return "Nombre y apellido son requeridos.";
        }

        if (correo == null || correo.isBlank() || !correo.contains("@")) {
            return "Correo inválido.";
        }

        if (usuarioRepository.existsByCorreo(correo)) {
            return "El correo ya está registrado.";
        }

        EIdentificacion identificacion;
        try {
            identificacion = EIdentificacion.valueOf(tipoIdentificacion.toUpperCase());
        } catch (IllegalArgumentException e) {
            return "Tipo de identificación inválido. Opciones: CC, TI, NIT, PASAPORTE";
        }

        ERol tipoRol = identificacion == EIdentificacion.NIT
                ? ERol.PERSONA_JURIDICA
                : ERol.PERSONA_NATURAL;

        Rol rol = rolRepository.findByName(tipoRol)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Usuario usuario = Usuario.builder()
                .nombres(nombres)
                .apellidos(apellidos)
                .correo(correo)
                .telefono(telefono)
                .tipoIdentificacion(identificacion)
                .numeroIdentificacion(numeroIdentificacion)
                .password(encoder.encode(password))
                .imagen("perfil-user.jpg")
                .rol(rol)
                .isEnabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .build();

        usuarioRepository.save(usuario);

        return "Usuario creado exitosamente. ID: " + usuario.getUsuarioId() + ", Correo: " + correo;
    }

    @Tool(
            name = "buscar_usuario_por_correo",
            description = """
                    Busca un usuario por correo electrónico.
                    Retorna información básica del usuario.
                    """
    )
    public String buscarUsuarioPorCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return "El correo no puede estar vacío.";
        }

        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return "No existe un usuario con el correo: " + correo;
        }

        return formatearUsuario(usuario);
    }

    @Tool(
            name = "listar_usuarios_por_rol",
            description = """
                Herramienta para listar usuarios del sistema por rol.
                Parámetros:
                - rol (requerido): ADMIN, PROVEEDOR, INVITADO, PERSONA_CONTACTO, PERSONA_JURIDICA, PERSONA_NATURAL
                - soloActivos (optional): true/false para filtrar por estado
                
                Máximo de resultados: 5 usuarios.
                """
    )
    public String listarUsuariosPorRol(
            String rol,
            Boolean soloActivos
    ) {
        if (rol == null || rol.isBlank()) {
            return "El rol es requerido. Roles válidos: ADMIN, PROVEEDOR, INVITADO, PERSONA_CONTACTO, PERSONA_JURIDICA, PERSONA_NATURAL";
        }

        try {
            ERol eRol = ERol.valueOf(rol.toUpperCase());

            List<Usuario> usuarios;
            if (Boolean.TRUE.equals(soloActivos)) {
                usuarios = usuarioRepository.findByRol_NameAndIsEnabledTrue(eRol)
                        .stream()
                        .limit(MAX_RESULTADOS)
                        .toList();
            } else {
                usuarios = usuarioRepository.findByRol_Name(eRol)
                        .stream()
                        .limit(MAX_RESULTADOS)
                        .toList();
            }

            if (usuarios.isEmpty()) {
                return "No se encontraron usuarios con rol: " + eRol.name();
            }

            return usuarios.stream()
                    .map(this::formatearUsuario)
                    .collect(Collectors.joining("\n"));

        } catch (IllegalArgumentException e) {
            return """
                Rol inválido.
                Roles válidos:
                ADMIN, PROVEEDOR, INVITADO, PERSONA_CONTACTO, PERSONA_JURIDICA, PERSONA_NATURAL
                """;
        }
    }

    @Tool(
            name = "listar_proveedores_activos",
            description = """
                Lista proveedores activos del sistema (máximo 30).
                Retorna: id, empresa, nombre del proveedor
                """
    )
    public String listarProveedoresActivos() {
        List<ProveedorProductoResponse> proveedores = usuarioService.getProveedoresActivos()
                .stream()
                .limit(MAX_PROVEEDORES)
                .toList();

        if (proveedores.isEmpty()) {
            return "No hay proveedores activos.";
        }

        return proveedores.stream()
                .map(p -> """
                        ID: %d
                        Empresa: %s
                        Proveedor: %s
                        """.formatted(p.id(), p.nombreEmpresa(), p.nombreUsuario()))
                .collect(Collectors.joining("-------------------------\n"));
    }

    @Tool(
            name = "obtener_total_clientes",
            description = """
                Obtiene el total de clientes registrados.
                """
    )
    public String obtenerTotalClientes() {
        Long total = usuarioService.getTotalClientes();
        return "Total de clientes registrados: " + total;
    }

    @Tool(
            name = "actualizar_usuario",
            description = """
                    Actualiza un usuario existente por ID.
                    Parámetros: usuarioId, nombres, apellidos, telefono, direccion
                    """
    )
    public String actualizarUsuario(
            Long usuarioId,
            String nombres,
            String apellidos,
            String telefono,
            String direccion
    ) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return "Usuario no encontrado con ID: " + usuarioId;
        }

        // Solo actualiza campos no nulos
        if (nombres != null && !nombres.isBlank()) usuario.setNombres(nombres);
        if (apellidos != null && !apellidos.isBlank()) usuario.setApellidos(apellidos);
        if (telefono != null && !telefono.isBlank()) usuario.setTelefono(telefono);
        if (direccion != null && !direccion.isBlank()) usuario.setDireccion(direccion);

        usuarioRepository.save(usuario);

        return "Usuario actualizado: " + usuario.getNombres() + " " + usuario.getApellidos();
    }

    @Tool(
            name = "cambiar_estado_usuario",
            description = """
                    Habilita o deshabilita un usuario por ID.
                    """
    )
    public String cambiarEstadoUsuario(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return "Usuario no encontrado con ID: " + usuarioId;
        }

        boolean nuevoEstado = !usuario.isEnabled();
        usuario.setEnabled(nuevoEstado);
        usuario.setAccountNonLocked(nuevoEstado);
        usuario.setCredentialsNonExpired(nuevoEstado);

        usuarioRepository.save(usuario);

        return "Usuario " + (nuevoEstado ? "habilitado" : "deshabilitado") + " correctamente.";
    }

    @Tool(
            name = "eliminar_usuario_por_id",
            description = """
                    Elimina lógicamente un usuario por ID.
                    """
    )
    public String eliminarUsuarioPorId(Long usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId).orElse(null);
        if (usuario == null) {
            return "Usuario no encontrado con ID: " + usuarioId;
        }

        usuario.setAccountNonLocked(false);
        usuarioRepository.delete(usuario);

        return "Usuario eliminado correctamente.";
    }

    @Tool(
            name = "eliminar_usuario_por_correo",
            description = """
                    Elimina lógicamente un usuario por correo.
                    """
    )
    public String eliminarUsuarioPorCorreo(String correo) {
        Usuario usuario = usuarioRepository.findByCorreo(correo).orElse(null);
        if (usuario == null) {
            return "Usuario no encontrado con correo: " + correo;
        }

        usuario.setAccountNonLocked(false);
        usuarioRepository.delete(usuario);

        return "Usuario eliminado correctamente.";
    }

    private String formatearUsuario(Usuario usuario) {
        return """
                ID: %d
                Nombre: %s %s
                Correo: %s
                Teléfono: %s
                Rol: %s
                Estado: %s
                """.formatted(
                usuario.getUsuarioId(),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getCorreo(),
                usuario.getTelefono() != null ? usuario.getTelefono() : "N/A",
                usuario.getRol() != null ? usuario.getRol().getName().name() : "SIN ROL",
                usuario.isEnabled() ? "ACTIVO" : "INACTIVO"
        );
    }

}