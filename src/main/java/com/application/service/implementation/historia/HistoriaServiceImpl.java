package com.application.service.implementation.historia;

import com.application.persistence.entity.historia.Historia;
import com.application.persistence.repository.HistoriaRepository;
import com.application.presentation.dto.general.response.BaseResponse;
import com.application.presentation.dto.historia.request.HistoriaCreateRequest;
import com.application.presentation.dto.historia.response.HistoriaResponse;
import com.application.service.interfaces.CloudinaryService;
import com.application.service.interfaces.historia.HistoriaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HistoriaServiceImpl implements HistoriaService {

    private final HistoriaRepository historiaRepository;
    private final CloudinaryService cloudinaryService;

    /**
     * Obtener una Historia por id
     * Este método es de uso interno para otros métodos del servicio
     *
     * @param id ID de la historia a buscar
     * @return La entidad historia encontrada
     * @throws EntityNotFoundException si la historia no existe
     */
    @Override
    public Historia getHistoriaById(Long id) {
        return historiaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("error: La historia con id: " + id + " no existe."));
    }

    /**
     * Obtiene una historia como DTO de respuesta por su ID
     * Este método es para la presentación de la historia en la pagina de
     * Historia-Descripción
     *
     * @param id ID de la historia a buscar
     * @return DTO con la información de la historia, no incluye sus comentarios.
     */
    @Override
    public HistoriaResponse getHistoriaResponseById(Long id) {
        Historia historia = this.getHistoriaById(id);
        return this.toResponse(historia);
    }

    /**
     * Obtiene todas las historias registradas (activas e inactivas)
     * Uso común para el panel de administrador en la vista de blog
     *
     * @return Lista de DTO con los datos de la historia
     * Nota: Solo se mostrarán los atributos imagen, título, fecha y activo.
     */
    @Override
    public List<HistoriaResponse> getHistorias() {
        List<Historia> historias = historiaRepository.findAll();
        return historias.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Obtiene todas las historias registradas con estado activo
     * Uso común para el ecommerce en la sección de blog
     *
     * @return Lista de DTO con historias activas
     * Nota: Solo se mostrarán los atributos imagen, título, descripción y fecha
     */
    @Override
    public List<HistoriaResponse> getHistoriasActivas() {
        List<Historia> historias = historiaRepository.findByActivoTrue();
        return historias.stream()
                .map(this::toResponse)
                .toList();
    }

    /**
     * Método para crear una historia a partir de un DTO de creación
     *
     * @param historiaRequest DTO con los datos de la nueva historia
     * @return Respuesta con mensaje de confirmación
     */
    @Override
    public BaseResponse createHistoria(HistoriaCreateRequest historiaRequest) {
        String imagen = cloudinaryService.subirImagen(historiaRequest.imagen(), "imagen-blog");

        Historia historia = Historia.builder()
                .imagen(imagen)
                .titulo(historiaRequest.titulo())
                .descripcion(historiaRequest.descripcion())
                .historiaCompleta(historiaRequest.historiaCompleta())
                .fecha(LocalDate.now())
                .activo(historiaRequest.activo())
                .build();

        historiaRepository.save(historia);
        return new BaseResponse("Historia creada exitosamente", true);
    }

    /**
     * Actualiza los datos de una historia existente.
     *
     * @param historiaRequest DTO con los datos para actualizar la historia
     * @param id              ID de la historia a actualizar
     * @return Respuesta con mensaje de confirmación
     * @throws EntityNotFoundException si la historia no existe
     */
    @Override
    public BaseResponse updateHistoria(HistoriaCreateRequest historiaRequest, Long id) {

        Historia historiaActualizada = this.getHistoriaById(id);

        String imagen = cloudinaryService.subirImagen(historiaRequest.imagen(), "imagen-blog");

        historiaActualizada.setTitulo(historiaRequest.titulo());
        historiaActualizada.setImagen(imagen);
        historiaActualizada.setDescripcion(historiaRequest.descripcion());
        historiaActualizada.setHistoriaCompleta(historiaRequest.historiaCompleta());
        historiaActualizada.setFecha(LocalDate.now());
        historiaActualizada.setActivo(historiaRequest.activo());

        historiaRepository.save(historiaActualizada);

        return new BaseResponse("Historia actualizada exitosamente", true);
    }

    /**
     * Cambia el estado de la historia.
     *
     * @param id ID de la historia a cambiar su estado
     * @return DTO con mensaje de confirmación según el estado de la historia
     * @throws EntityNotFoundException si la historia no existe
     */
    @Override
    public BaseResponse changeEstadoHistoria(Long id) {
        Historia historia = this.getHistoriaById(id);

        boolean nuevoEstado = !historia.isActivo();
        historia.setActivo(nuevoEstado);
        historiaRepository.save(historia);

        String mensaje = nuevoEstado
                ? "Historia habilitada exitosamente"
                : "Historia deshabilitada exitosamente";

        return new BaseResponse(mensaje, true);
    }

    /**
     * Elimina una historia y sus comentarios asociados
     *
     * @param id ID de la historia a eliminar
     * @return DTO con mensaje de confirmación
     * @throws EntityNotFoundException si la historia no existe
     */
    @Override
    @Transactional
    public BaseResponse deleteHistoria(Long id) {
        Historia historia = this.getHistoriaById(id);
        historiaRepository.delete(historia);
        return new BaseResponse("Historia eliminada exitosamente", true);
    }

    /**
     * Convierte una entidad Historia a su DTO de respuesta,
     * no incluye la lista de comentarios
     * Para uso interno del Servicio en los métodos de búsqueda
     *
     * @param historia Entidad Historia a convertir
     * @return DTO con la información de la Historia
     */
    @Override
    public HistoriaResponse toResponse(Historia historia) {
        return new HistoriaResponse(
                historia.getHistoriaId(),
                cloudinaryService.getImagenUrl(historia.getImagen()),
                historia.getTitulo(),
                historia.getDescripcion(),
                historia.getHistoriaCompleta(),
                historia.getFecha(),
                historia.isActivo());
    }
}