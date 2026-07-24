package com.application.service.implementation;

import com.application.configuration.custom.CustomUserPrincipal;
import com.application.persistence.entity.pqrs.Peticion;
import com.application.persistence.entity.pqrs.enums.EEstadoPeticion;
import com.application.persistence.entity.usuario.Usuario;
import com.application.persistence.repository.PeticionRepository;
import com.application.persistence.repository.UsuarioRepository;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.peticion.request.PeticionCreateRequest;
import com.application.presentation.dto.peticion.response.PeticionResponse;
import com.application.presentation.dto.peticion.response.PeticionUsurarioResponse;
import com.application.service.interfaces.PeticionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PeticionServiceImpl implements PeticionService {

    private final PeticionRepository peticionRepository;
    private final UsuarioRepository usuarioRepository;

    private Peticion getPeticionById(Long peticionId) {
        return peticionRepository.findById(peticionId)
                .orElseThrow(() -> new EntityNotFoundException("La petición con id: " + peticionId + " no exite."));
    }

    @Override
    public List<PeticionResponse> getPeticionesActivas() {
        List<Peticion> peticiones = peticionRepository.findByActivoTrueOrderByFechaDesc();
        return peticiones.stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public BaseResponse createPeticion(CustomUserPrincipal principal, PeticionCreateRequest peticionRequest) {

        Usuario usuario = usuarioRepository.findByCorreo(principal.getCorreo())
                .orElseThrow(() -> new EntityNotFoundException("No existe un usuario en sesión"));

        Peticion peticion = Peticion.builder()
                .tipoPeticion(peticionRequest.tipoPeticion())
                .asunto(peticionRequest.asunto())
                .mensaje(peticionRequest.mensaje())
                .estadoPeticion(peticionRequest.estadoPeticion())
                .fecha(LocalDate.now())
                .activo(true)
                .build();

        usuario.addPeticion(peticion);
        usuarioRepository.save(usuario);

        return new BaseResponse("Petición creada exitosamente", true);
    }

    @Override
    public BaseResponse updatePeticion(Long peticionId, CustomUserPrincipal principal, PeticionCreateRequest peticionRequest) {

        Peticion peticion = this.getPeticionById(peticionId);

        peticion.setTipoPeticion(peticionRequest.tipoPeticion());
        peticion.setAsunto(peticionRequest.asunto());
        peticion.setMensaje(peticionRequest.mensaje());
        peticion.setEstadoPeticion(peticionRequest.estadoPeticion());
        peticion.setFecha(LocalDate.now());

        peticionRepository.save(peticion);
        return new BaseResponse("Petición actualizada exitosamente", true);
    }

    @Override
    public BaseResponse changeEstadoPeticion(Long peticionId, String estadoPeticion) {
        Peticion peticion = this.getPeticionById(peticionId);

        EEstadoPeticion nuevoEstado = this.mapearStringAEstado(estadoPeticion);
        peticion.setEstadoPeticion(nuevoEstado);
        peticionRepository.save(peticion);

        return new BaseResponse("Estado cambiado exitosamente", true);
    }

    private EEstadoPeticion mapearStringAEstado(String estado) {
        return switch (estado.toLowerCase()) {
            case "resuelto" -> EEstadoPeticion.RESUELTO;
            case "en proceso" -> EEstadoPeticion.EN_PROCESO;
            case "pendiente" -> EEstadoPeticion.PENDIENTE;
            default -> throw new IllegalArgumentException("Estado Invalido " + estado);
        };
    }

    @Override
    public BaseResponse deletePeticion(Long peticionId) {
        Peticion peticion = this.getPeticionById(peticionId);

        peticion.setActivo(false);
        peticionRepository.save(peticion);

        String mensaje = "Petición eliminada exitosamente";

        return new BaseResponse(mensaje, true);
    }

    private PeticionResponse toResponse(Peticion peticion) {
        return new PeticionResponse(
                peticion.getPeticionId(),
                peticion.getTipoPeticion().getDescripcion(),
                peticion.getAsunto(),
                peticion.getMensaje(),
                peticion.getFecha(),
                peticion.getEstadoPeticion().getDescripcion(),
                peticion.isActivo(),
                new PeticionUsurarioResponse(
                        peticion.getUsuario().getNombres(),
                        peticion.getUsuario().getApellidos(),
                        peticion.getUsuario().getCorreo(),
                        peticion.getUsuario().getTelefono()
                )
        );
    }
}