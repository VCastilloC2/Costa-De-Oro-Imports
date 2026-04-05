package com.application.service.implementation.usuario;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.empresa.Empresa;
import com.application.persistence.entity.rol.Rol;
import com.application.persistence.entity.rol.enums.ERol;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.entity.usuario.enums.EIdentificacion;
import com.application.persistence.repository.EmpresaRepository;
import com.application.persistence.repository.RolRepository;
import com.application.persistence.repository.UsuarioRepository;
import com.application.presentation.dto.general.response.GeneralResponse;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.usuario.request.*;
import com.application.presentation.dto.usuario.response.*;
import com.application.service.implementation.ImagenServiceImpl;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.EmailService;
import com.application.service.interfaces.ImagenService;
import com.application.service.interfaces.empresa.EmpresaService;
import com.application.service.interfaces.usuario.UsuarioService;
import com.application.utils.JwtUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService, UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final EmpresaRepository empresaRepository;
    private final RolRepository rolRepository;

    private final CloudinaryService cloudinaryService;
    private final EmailService emailService;
    private final PasswordEncoder encoder;

    @Override
    public UserDetails loadUserByUsername(String correo) throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new UsernameNotFoundException("ERROR: el correo '" + correo + "' no existe"));

        return new CustomUserPrincipal(usuario);
    }

    private Usuario getUsuarioById(Long usuarioId) {
        return usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new EntityNotFoundException("El usuario con id: " + usuarioId + " no existe"));
    }

    @Override
    public Usuario getUsuarioByCorreo(String correo) {
        return usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new EntityNotFoundException("error: El correo '" + correo + "' no exite"));
    }

    @Override
    public ClienteResponse getClienteById(Long clienteId) {
        Usuario usuario = this.getUsuarioById(clienteId);
        return this.toClienteResponse(usuario);
    }

    @Override
    public ProveedorResponse getProveedorById(Long proveedorId) {
        Usuario usuario = this.getUsuarioById(proveedorId);
        return this.toProveedorResponse(usuario);
    }

    @Override
    public List<UsuarioGastoResponse> getUsuarioGastoUltimoAnio() {
        LocalDateTime ultimoAnio = LocalDateTime.now().minusYears(1);
        return usuarioRepository.obtenerUsuariosConGastoUltimoAnio(ultimoAnio).stream()
                .map(usuario -> new UsuarioGastoResponse(
                        usuario.usuarioId(),
                        cloudinaryService.getImagenUrl(usuario.imagen()),
                        usuario.nombreCompleto(),
                        usuario.correo(),
                        usuario.telefono(),
                        usuario.totalGastado(),
                        usuario.isEnabled()
                ))
                .toList();
    }

    @Override
    public List<ProveedorEstadisticasResponse> getProveedorConEstadisticas() {
        List<ProveedorEstadisticasResponse> proveedores = usuarioRepository.findProveedoresConEstadisticas();
        return proveedores.stream()
                .map(proveedor -> new ProveedorEstadisticasResponse(
                        proveedor.usuarioId(),
                        cloudinaryService.getImagenUrl(proveedor.imagenEmpresa()),
                        proveedor.nombreEmpresa(),
                        proveedor.nombreCompleto(),
                        proveedor.correo(),
                        proveedor.totalGastado(),
                        proveedor.totalGanado(),
                        proveedor.isEnabled()
                ))
                .toList();
    }

    /**
     * Obtiene los usuarios con rol 'PROVEEDOR' y estado activo.
     * Para uso del select de proveedor en los formularios de crear y editar producto
     *
     * @return Lista de DTOs con los proveedores activos (Solo se incluye id, nombre y empresa del usuario proveedor)
     */
    @Override
    public List<ProveedorProductoResponse> getProveedoresActivos() {
        return usuarioRepository.findByRol_NameAndEmpresa_ActivoTrue(ERol.PROVEEDOR).stream()
                .map(proveedor -> new ProveedorProductoResponse(
                        proveedor.getUsuarioId(),
                        proveedor.getEmpresa() != null ? proveedor.getEmpresa().getRazonSocial() : "Sin empresa",
                        proveedor.getNombres() + " " + proveedor.getApellidos()
                ))
                .toList();
    }

    @Override
    public Long getTotalClientes() {
        Long totalCliente = usuarioRepository.totalClientes();
        return totalCliente != null ? totalCliente : 0;
    }

    @Override
    public GeneralResponse completeUserProfile(CustomUserPrincipal principal, CompleteUsuarioProfileRequest completeProfileRequest) {
        Usuario usuario = this.getUsuarioByCorreo(principal.getCorreo());
        String encryptedPassword = encoder.encode(completeProfileRequest.password());

        usuario.setTipoIdentificacion(completeProfileRequest.tipoIdentificacion());
        usuario.setNumeroIdentificacion(completeProfileRequest.numeroIdentificacion());
        usuario.setImagen("perfil-user.jpg");
        usuario.setNombres(completeProfileRequest.nombres());
        usuario.setApellidos(completeProfileRequest.apellidos());
        usuario.setTelefono(completeProfileRequest.telefono());
        usuario.setPassword(encryptedPassword);

        if (completeProfileRequest.tipoIdentificacion().equals(EIdentificacion.NIT)) {
            Rol rolPersonaJuridica = rolRepository.findByName(ERol.PERSONA_JURIDICA)
                    .orElseThrow(
                            () -> new EntityNotFoundException("error: el rol PERSONA_JURIDICA no existe en la BD"));
            usuario.setRol(rolPersonaJuridica);
        } else {
            Rol rolPersonaNatural = rolRepository.findByName(ERol.PERSONA_NATURAL)
                    .orElseThrow(() -> new EntityNotFoundException("error: el rol PERSONA_NATURAL no exite en la BD"));
            usuario.setRol(rolPersonaNatural);
        }

        usuarioRepository.save(usuario);
        this.emailService.sendWelcomeEmail(usuario);
        return new GeneralResponse("Registro completado exitosamente.");
    }

    @Override
    public BaseResponse createUser(@Valid CreateUsuarioRequest usuarioRequest) {

        String correo = usuarioRequest.correo();
        if (usuarioRepository.existsByCorreo(correo)) {
            return new BaseResponse("El correo " + correo + " ya está registrado", false);
        }

        Usuario usuario = Usuario.builder()
                .tipoIdentificacion(usuarioRequest.tipoIdentificacion())
                .numeroIdentificacion(usuarioRequest.numeroIdentificacion())
                .imagen("perfil-user.jpg")
                .nombres(usuarioRequest.nombres())
                .apellidos(usuarioRequest.apellidos())
                .telefono(usuarioRequest.telefono())
                .correo(usuarioRequest.correo())
                .password(encoder.encode(usuarioRequest.password()))
                .build();

        if (usuarioRequest.tipoIdentificacion().equals(EIdentificacion.NIT)) {
            Rol rolPersonaJuridica = rolRepository.findByName(ERol.PERSONA_JURIDICA)
                    .orElseThrow(
                            () -> new EntityNotFoundException("error: el rol PERSONA_JURIDICA no existe en la BD"));
            usuario.setRol(rolPersonaJuridica);
        } else {
            Rol rolPersonaNatural = rolRepository.findByName(ERol.PERSONA_NATURAL)
                    .orElseThrow(() -> new EntityNotFoundException("error: el rol PERSONA_NATURAL no exite en la BD"));
            usuario.setRol(rolPersonaNatural);
        }

        usuarioRepository.save(usuario);

        return new BaseResponse("Usuario creado exitosamente", true);
    }

    /**
     * Método para crear un cliente
     * Usado por el Admin en la pagina AgregarCliente.html
     *
     * @param clienteRequest DTO con los datos del cliente
     * @return DTO de respuesta con un mensaje y con un boolean que define si la operación fue exitosa o no.
     */
    @Override
    public BaseResponse createCliente(CreateClienteRequest clienteRequest) {
        String correo = clienteRequest.correo();
        if (usuarioRepository.existsByCorreo(correo)) {
            return new BaseResponse("El correo " + correo + " ya está registrado", false);
        }

        String imagen = cloudinaryService.subirImagen(clienteRequest.imagen(), "perfil-usuario");

        Usuario usuario = Usuario.builder()
                .tipoIdentificacion(clienteRequest.tipoIdentificacion())
                .numeroIdentificacion(clienteRequest.numeroIdentificacion())
                .imagen(imagen)
                .nombres(clienteRequest.nombres())
                .apellidos(clienteRequest.apellidos())
                .telefono(clienteRequest.telefono())
                .correo(clienteRequest.correo())
                .password(encoder.encode(clienteRequest.password()))
                .direccion(clienteRequest.direccion())
                .build();

        if (clienteRequest.tipoIdentificacion().equals(EIdentificacion.NIT)) {
            Rol rolPersonaJuridica = rolRepository.findByName(ERol.PERSONA_JURIDICA)
                    .orElseThrow(
                            () -> new EntityNotFoundException("error: el rol PERSONA_JURIDICA no existe en la BD"));
            usuario.setRol(rolPersonaJuridica);
        } else {
            Rol rolPersonaNatural = rolRepository.findByName(ERol.PERSONA_NATURAL)
                    .orElseThrow(() -> new EntityNotFoundException("error: el rol PERSONA_NATURAL no exite en la BD"));
            usuario.setRol(rolPersonaNatural);
        }

        usuarioRepository.save(usuario);

        return new BaseResponse("Usuario creado exitosamente", true);
    }

    /**
     * Método para crear un proveedor
     * Usado por el Admin en la pagina AgregarCliente.html
     *
     * @param proveedorRequest DTO con los datos del proveedor y la empresa la que pertenece
     * @return DTO de respuesta con un mensaje y con un boolean que define si la operación fue exitosa o no.
     */
    @Override
    @Transactional
    public BaseResponse createProveedor(CreateProveedorRequest proveedorRequest) {
        String correo = proveedorRequest.correo();
        if (usuarioRepository.existsByCorreo(correo)) {
            return new BaseResponse("El correo " + correo + " ya está registrado", false);
        }

        Rol rolProveedor = rolRepository.findByName(ERol.PROVEEDOR)
                .orElseThrow( () -> new EntityNotFoundException("Error: el rol PROVEEDOR no existe en la BD"));

        String imagen = cloudinaryService.subirImagen(proveedorRequest.imagen(), "perfil-usuario");
        Usuario usuario = Usuario.builder()
                .tipoIdentificacion(proveedorRequest.tipoIdentificacion())
                .numeroIdentificacion(proveedorRequest.numeroIdentificacion())
                .imagen(imagen)
                .nombres(proveedorRequest.nombres())
                .apellidos(proveedorRequest.apellidos())
                .telefono(proveedorRequest.telefono())
                .correo(proveedorRequest.correo())
                .password(encoder.encode(proveedorRequest.password()))
                .direccion(proveedorRequest.direccion())
                .rol(rolProveedor)
                .build();

        return createEmpresaProveedor(usuario, proveedorRequest);
    }

    private BaseResponse createEmpresaProveedor(Usuario usuario, CreateProveedorRequest proveedorRequest) {
        String nit = proveedorRequest.nit();
        boolean existeEmpresa = empresaRepository.existsByNit(nit);
        if (existeEmpresa) {
            return new BaseResponse("error: La empresa con el nit '" + nit + "' ya tiene un usuario asignado.\n" +
                    "Si usted es el nuevo representante, escriba a admin@mail.com\n" +
                    " o use el formulario de contacto para solicitar la actualización.", false);
        }

        String imagenEmpresa = cloudinaryService.subirImagen(proveedorRequest.imagenEmpresa(), "perfil-empresa");
        Empresa empresa = Empresa.builder()
                .imagen(imagenEmpresa)
                .nit(proveedorRequest.nit())
                .razonSocial(proveedorRequest.razonSocial())
                .ciudad(proveedorRequest.ciudad())
                .direccion(proveedorRequest.direccionEmpresa())
                .telefono(proveedorRequest.telefonoEmpresa())
                .correo(proveedorRequest.correoEmpresa())
                .activo(true)
                .build();

        usuario.setEmpresa(empresa);
        usuarioRepository.save(usuario);

        return new BaseResponse("Proveedor creado exitosamente", true);
    }

    @Override
    public GeneralResponse updateUser(CustomUserPrincipal principal, UpdateUsuarioRequest usuarioRequest) {

        Usuario usuarioActualizado = this.getUsuarioByCorreo(principal.getCorreo());

        usuarioActualizado.setTipoIdentificacion(usuarioRequest.tipoIdentificacion());
        usuarioActualizado.setNumeroIdentificacion(usuarioRequest.numeroIdentificacion());
        usuarioActualizado.setNombres(usuarioRequest.nombres());
        usuarioActualizado.setApellidos(usuarioRequest.apellidos());
        usuarioActualizado.setTelefono(usuarioRequest.telefono());
        usuarioActualizado.setCorreo(usuarioRequest.correo());

        usuarioRepository.save(usuarioActualizado);
        return new GeneralResponse("Sus datos se han actualizado exitosamente");
    }

    /**
     * Método para actualizar un cliente
     *
     * @param clienteId      ID del cliente a actualizar
     * @param clienteRequest DTO con los nuevos datos del cliente
     * @return DTO de respuesta con un mensaje y con un boolean que define si la operación fue exitosa o no.
     */
    @Override
    public BaseResponse updateCliente(Long clienteId, CreateClienteRequest clienteRequest) {
        Usuario cliente = this.getUsuarioById(clienteId);

        String imagen = cloudinaryService.subirImagen(clienteRequest.imagen(), "perfil-usuario");

        cliente.setImagen(imagen);
        cliente.setTipoIdentificacion(clienteRequest.tipoIdentificacion());
        cliente.setNumeroIdentificacion(clienteRequest.numeroIdentificacion());
        cliente.setNombres(clienteRequest.nombres());
        cliente.setApellidos(clienteRequest.apellidos());
        cliente.setTelefono(clienteRequest.telefono());
        cliente.setCorreo(clienteRequest.correo());
        cliente.setPassword(encoder.encode(clienteRequest.password()));
        cliente.setDireccion(clienteRequest.direccion());

        usuarioRepository.save(cliente);
        return new BaseResponse("Cliente actualizado exitosamente", true);
    }

    /**
     * Método para actualizar un proveedor
     *
     * @param proveedorId      ID del proveedor a actualizar
     * @param proveedorRequest DTO con los nuevos datos del proveedor y la empresa a la que pertenece
     * @return DTO de respuesta con un mensaje y con un boolean que define si la operación fue exitosa o no.
     */
    @Override
    @Transactional
    public BaseResponse updateProveedor(Long proveedorId, CreateProveedorRequest proveedorRequest) {
        Usuario proveedor = this.getUsuarioById(proveedorId);

        String imagen = cloudinaryService.subirImagen(proveedorRequest.imagen(), "perfil-usuario");

        proveedor.setImagen(imagen);
        proveedor.setTipoIdentificacion(proveedorRequest.tipoIdentificacion());
        proveedor.setNumeroIdentificacion(proveedorRequest.numeroIdentificacion());
        proveedor.setNombres(proveedorRequest.nombres());
        proveedor.setApellidos(proveedorRequest.apellidos());
        proveedor.setTelefono(proveedorRequest.telefono());
        proveedor.setCorreo(proveedorRequest.correo());
        proveedor.setPassword(encoder.encode(proveedorRequest.password()));
        proveedor.setDireccion(proveedorRequest.direccion());

        return this.updateEmpresaProveedor(proveedor, proveedorRequest);
    }

    private BaseResponse updateEmpresaProveedor(Usuario usuario, CreateProveedorRequest proveedorRequest) {
        Empresa empresaActualizada = usuario.getEmpresa();
        if (empresaActualizada == null) {
            return new BaseResponse("El proveedor no tiene una empresa asociada.", false);
        }

        String imagenEmpresa = cloudinaryService.subirImagen(proveedorRequest.imagenEmpresa(), "perfil-empresa");

        empresaActualizada.setImagen(imagenEmpresa);
        empresaActualizada.setNit(proveedorRequest.nit());
        empresaActualizada.setRazonSocial(proveedorRequest.razonSocial());
        empresaActualizada.setCiudad(proveedorRequest.ciudad());
        empresaActualizada.setDireccion(proveedorRequest.direccionEmpresa());
        empresaActualizada.setTelefono(proveedorRequest.telefonoEmpresa());
        empresaActualizada.setCorreo(proveedorRequest.correoEmpresa());

        usuario.setEmpresa(empresaActualizada);
        usuarioRepository.save(usuario);

        return new BaseResponse("Proveedor actualizado exitosamente", true);
    }

    @Override
    public GeneralResponse setUserPhoto(CustomUserPrincipal principal, SetUsuarioPhotoRequest usuarioPhotoRequest) {

        Usuario usuarioPhoto = this.getUsuarioByCorreo(principal.getCorreo());

        String imagen = cloudinaryService.subirImagen(usuarioPhotoRequest.imagenUsuarioNueva(), "perfil-usuario");
        if (imagen != null) {
            usuarioPhoto.setImagen(imagen);
        } else {
            usuarioPhoto.setImagen(usuarioPhotoRequest.imagenUsuarioOriginal());
        }

        usuarioRepository.save(usuarioPhoto);

        return new GeneralResponse("Imagen asignada exitosamente");
    }

    @Override
    public BaseResponse updatePassword(CustomUserPrincipal principal, UpdatePasswordRequest passwordRequest) {

        Usuario usuario = this.getUsuarioByCorreo(principal.getCorreo());
        String currentPassword = passwordRequest.currentPassword();
        String newPassword = passwordRequest.newPassword();

        if (!encoder.matches(currentPassword, usuario.getPassword())) {
            return new BaseResponse("Contraseña actual incorrecta", false);
        }

        if (encoder.matches(newPassword, usuario.getPassword())) {
            return new BaseResponse("La nueva contraseña no puede ser igual a la anterior", false);
        }

        usuario.setPassword(encoder.encode(newPassword));
        usuarioRepository.save(usuario);

        return new BaseResponse("contraseña actualizada exitosamente", true);
    }

    @Override
    public BaseResponse changeEstadoUsuario(Long usuarioId) {
        Usuario usuario = getUsuarioById(usuarioId);

        boolean nuevoEstado = !usuario.isEnabled();
        usuario.setEnabled(nuevoEstado);
        usuarioRepository.save(usuario);

        String mensaje = nuevoEstado
                ? "Usuario habilitado exitosamente"
                : "Usuario deshabilitado exitosamente";


        return new BaseResponse(mensaje, true);
    }

    @Override
    public BaseResponse deleteUsuario(Long usuarioId) {
        Usuario usuario = getUsuarioById(usuarioId);

        usuario.setAccountNonLocked(false);
        usuarioRepository.save(usuario);

        String mensaje = "Usuario eliminado exitosamente";

        return new BaseResponse(mensaje, true);
    }

    private ClienteResponse toClienteResponse(Usuario usuario) {
        return new ClienteResponse(
                usuario.getUsuarioId(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                cloudinaryService.getImagenUrl(usuario.getImagen()),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getDireccion()
        );
    }

    private ProveedorResponse toProveedorResponse(Usuario usuario) {
        return new ProveedorResponse(
                // Datos del Usuario
                usuario.getUsuarioId(),
                usuario.getTipoIdentificacion(),
                usuario.getNumeroIdentificacion(),
                cloudinaryService.getImagenUrl(usuario.getImagen()),
                usuario.getNombres(),
                usuario.getApellidos(),
                usuario.getTelefono(),
                usuario.getCorreo(),
                usuario.getPassword(),
                usuario.getDireccion(),

                // Datos de la Empresa
                usuario.getEmpresa().getEmpresaId(),
                cloudinaryService.getImagenUrl(usuario.getEmpresa().getImagen()),
                usuario.getEmpresa().getNit(),
                usuario.getEmpresa().getRazonSocial(),
                usuario.getEmpresa().getCiudad(),
                usuario.getEmpresa().getDireccion(),
                usuario.getEmpresa().getTelefono(),
                usuario.getEmpresa().getCorreo()
        );
    }

}