package com.application.configuration.ia.tools;

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

@Component
@RequiredArgsConstructor
public class UsuarioTools {

    private final UsuarioRepository usuarioRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder encoder;

    @Tool(
            name = "buscar_usuario_por_correo",
            description = """
                    Busca un usuario por correo electrónico.
                    Retorna información básica del usuario.
                    """
    )
    public String buscarUsuarioPorCorreo(String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElse(null);

        if (usuario == null) {
            return "No existe un usuario con el correo: " + correo;
        }

        return """
                Usuario encontrado:
                                
                ID: %d
                Nombre: %s %s
                Correo: %s
                Teléfono: %s
                Rol: %s
                Estado: %s
                """
                .formatted(
                        usuario.getUsuarioId(),
                        usuario.getNombres(),
                        usuario.getApellidos(),
                        usuario.getCorreo(),
                        usuario.getTelefono(),
                        usuario.getRol() != null
                                ? usuario.getRol().getName().name()
                                : "SIN ROL",
                        usuario.isEnabled()
                                ? "ACTIVO"
                                : "INACTIVO"
                );
    }

    @Tool(
            name = "listar_usuarios",
            description = """
                    Lista todos los usuarios registrados.
                    """
    )
    public String listarUsuarios() {

        var usuarios = usuarioRepository.findAll();

        if (usuarios.isEmpty()) {
            return "No hay usuarios registrados.";
        }

        StringBuilder builder = new StringBuilder();

        for (Usuario usuario : usuarios) {

            builder.append("""
                                        
                    ID: %d
                    Nombre: %s %s
                    Correo: %s
                    Rol: %s
                    Estado: %s
                    -------------------------
                    """.formatted(
                    usuario.getUsuarioId(),
                    usuario.getNombres(),
                    usuario.getApellidos(),
                    usuario.getCorreo(),
                    usuario.getRol() != null
                            ? usuario.getRol().getName().name()
                            : "SIN ROL",
                    usuario.isEnabled()
                            ? "ACTIVO"
                            : "INACTIVO"
            ));
        }

        return builder.toString();
    }

    @Tool(
            name = "crear_usuario",
            description = """
                    Crea un nuevo usuario.
                    Necesita:
                    - nombres
                    - apellidos
                    - correo
                    - password
                    - telefono
                    - tipoIdentificacion
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

        if (usuarioRepository.existsByCorreo(correo)) {
            return "El correo ya está registrado.";
        }

        EIdentificacion identificacion;

        try {
            identificacion = EIdentificacion.valueOf(
                    tipoIdentificacion.toUpperCase()
            );
        } catch (Exception e) {
            return "Tipo de identificación inválido.";
        }

        ERol tipoRol = identificacion == EIdentificacion.NIT
                ? ERol.PERSONA_JURIDICA
                : ERol.PERSONA_NATURAL;

        Rol rol = rolRepository.findByName(tipoRol)
                .orElseThrow(() ->
                        new RuntimeException("Rol no encontrado"));

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

        return """
                Usuario creado exitosamente.
                                
                ID: %d
                Nombre: %s %s
                Correo: %s
                Rol: %s
                """
                .formatted(
                        usuario.getUsuarioId(),
                        usuario.getNombres(),
                        usuario.getApellidos(),
                        usuario.getCorreo(),
                        rol.getName().name()
                );
    }

    @Tool(
            name = "actualizar_usuario",
            description = """
                    Actualiza un usuario existente por ID.
                    """
    )
    public String actualizarUsuario(
            Long usuarioId,
            String nombres,
            String apellidos,
            String telefono,
            String direccion
    ) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (usuario == null) {
            return "Usuario no encontrado.";
        }

        usuario.setNombres(nombres);
        usuario.setApellidos(apellidos);
        usuario.setTelefono(telefono);
        usuario.setDireccion(direccion);

        usuarioRepository.save(usuario);

        return """
                Usuario actualizado exitosamente.
                                
                ID: %d
                Nombre: %s %s
                """
                .formatted(
                        usuario.getUsuarioId(),
                        usuario.getNombres(),
                        usuario.getApellidos()
                );
    }

    @Tool(
            name = "cambiar_estado_usuario",
            description = """
                    Habilita o deshabilita un usuario.
                    """
    )
    public String cambiarEstadoUsuario(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (usuario == null) {
            return "Usuario no encontrado.";
        }

        usuario.setEnabled(!usuario.isEnabled());
        usuario.setAccountNonLocked(!usuario.isAccountNonExpired());
        usuario.setCredentialsNonExpired(!usuario.isCredentialsNonExpired());

        usuarioRepository.save(usuario);

        return usuario.isEnabled()
                ? "Usuario habilitado correctamente."
                : "Usuario deshabilitado correctamente.";
    }

    @Tool(
            name = "eliminar_usuario",
            description = """
                    Elimina lógicamente un usuario por id.
                    """
    )
    public String eliminarUsuario(Long usuarioId) {

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElse(null);

        if (usuario == null) {
            return "Usuario no encontrado.";
        }

        usuario.setAccountNonLocked(false);

        usuarioRepository.delete(usuario);

        return "Usuario eliminado correctamente.";
    }

    @Tool(
            name = "eliminar_usuario",
            description = """
                    Elimina lógicamente un usuario por correo.
                    """
    )
    public String eliminarUsuarioPorCorreo(String correo) {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElse(null);

        if (usuario == null) {
            return "Usuario no encontrado.";
        }

        usuario.setAccountNonLocked(false);

        usuarioRepository.delete(usuario);

        return "Usuario eliminado correctamente.";
    }

}