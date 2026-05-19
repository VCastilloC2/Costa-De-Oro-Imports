package com.application.configuration.ia.tools;

import com.application.persistence.entity.rol.Rol;
import com.application.persistence.entity.rol.enums.ERol;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.persistence.repository.RolRepository;
import com.application.persistence.repository.UsuarioRepository;
import com.application.presentation.dto.usuario.response.ProveedorProductoResponse;
import com.application.service.interfaces.usuario.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

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
                    Crea un nuevo usuario en el sistema.
                    Usa esta herramienta cuando el usuario quiera:
                    registrar, crear, añadir, ingresar, guardar o dar de alta un usuario, cliente, proveedor o persona.
                    También cuando diga cosas como:
                    - crear cuenta
                    - registrar cliente
                    - añadir proveedor
                    - nuevo usuario
                    - guardar persona
                    - registrar empleado
                    Datos requeridos:
                    nombres, apellidos, correo, password, telefono, tipoIdentificacion y numeroIdentificacion.
                    Tipos de identificación válidos:
                    CC, TI, NIT, PASAPORTE.
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
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
                    Busca y muestra información de un usuario usando su correo electrónico.
                    Usa esta herramienta cuando el usuario quiera:
                    buscar, consultar, encontrar, verificar, revisar o ver datos de un usuario por correo.
                    Ejemplos:
                    - buscar usuario por correo
                    - consultar cliente
                    - ver información de un correo
                    - existe este usuario
                    - mostrar datos del usuario
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
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
                    Lista usuarios según su rol dentro del sistema.
                    Usa esta herramienta cuando el usuario quiera:
                    ver usuarios, listar clientes, mostrar proveedores, consultar administradores o filtrar usuarios por tipo.
                    Roles válidos:
                    ADMIN, PROVEEDOR, INVITADO, PERSONA_CONTACTO, PERSONA_JURIDICA, PERSONA_NATURAL.
                    Puede filtrar solo usuarios activos.
                    Máximo 5 resultados.
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
                    Muestra proveedores activos registrados en el sistema.
                    Usa esta herramienta cuando el usuario quiera:
                    ver proveedores, listar empresas proveedoras, consultar proveedores activos o buscar proveedores disponibles.
                    Retorna:
                    ID, empresa y nombre del proveedor.
                    Máximo 5 resultados.
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
                    Obtiene la cantidad total de clientes registrados.
                    Usa esta herramienta cuando el usuario quiera:
                    contar clientes, saber cuántos clientes existen, ver total de usuarios o consultar estadísticas de clientes.
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
                    """
    )
    public String obtenerTotalClientes() {
        Long total = usuarioService.getTotalClientes();
        return "Total de clientes registrados: " + total;
    }

    @Tool(
            name = "actualizar_usuario",
            description = """
                    Actualiza información de un usuario existente mediante su ID.
                    Usa esta herramienta cuando el usuario quiera:
                    editar usuario, modificar cliente, actualizar datos, cambiar teléfono, cambiar dirección o corregir información.
                    Puede actualizar:
                    nombres, apellidos, telefono y direccion.
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
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
                    Habilita o deshabilita un usuario del sistema usando su ID.
                    Usa esta herramienta cuando el usuario quiera:
                    activar usuario, desactivar cuenta, bloquear usuario, suspender usuario, habilitar acceso o cambiar estado.
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
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
        usuario.setAccountNonExpired(nuevoEstado);

        usuarioRepository.save(usuario);

        return "Usuario " + (nuevoEstado ? "deshabilitado" : "habilitado") + " correctamente.";
    }

    @Tool(
            name = "eliminar_usuario_por_id",
            description = """
                    Elimina un usuario usando su ID.
                    Usa esta herramienta cuando el usuario quiera:
                    eliminar usuario, borrar cuenta, quitar cliente, remover usuario o desactivar permanentemente un registro.
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
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
                    Elimina un usuario usando su correo electrónico.
                    Usa esta herramienta cuando el usuario quiera:
                    eliminar usuario por correo, borrar cuenta, remover cliente o quitar un usuario usando email.
                    Siempre usa esta herramienta si la solicitud del usuario coincide aunque sea parcialmente.
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