package com.application.service.implementation;

import com.application.service.interfaces.CloudinaryService;
import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class CloudinaryServiceImpl implements CloudinaryService {

    private final Cloudinary cloudinary;

    private final Map<String, String> folders = Map.of(
            "perfil-usuario", "gestion-ventas/perfil-usuario",
            "perfil-empresa", "gestion-ventas/perfil-empresa",
            "imagen-producto", "gestion-ventas/imagen-producto",
            "imagen-categoria", "gestion-ventas/imagen-categoria",
            "imagen-blog", "gestion-ventas/imagen-blog"
    );

    /**
     * Método para subir una foto a cloudinary
     *
     * @param imagen Interfaz que recibe la imagen que se cargue en el formulario
     * @param tipo   Tipo de directorio donde se guardara la imagen
     * @return El public_id asignado por Cloudinary de la imágen subida
     * o un null si el usuario no cuenta con una foto de perfil
     * @throws IllegalArgumentException Sí el tipo de imagen es inválido
     * @throws RuntimeException         Sí ocurre un error durante la carga del archivo a Cloudinary
     * @apiNote La interfaz MultipartFile, es usada para representar un archivo
     * cargado mediante un formulario multipart/form-data,
     * (típico en formularios HTML con <input type="file">).
     */
    @Override
    public String subirImagen(MultipartFile imagen, String tipo) {

        if (imagen.isEmpty()) {
            return null;
        }

        String folder = folders.get(tipo);
        if (folder == null) {
            throw new IllegalArgumentException("Error: tipo de imagen inválido: " + tipo);
        }

        try {

            var uploadOptions = ObjectUtils.asMap(
                    "folder", folder,
                    "public_id", "img_" + System.currentTimeMillis(),
                    "overwrite", false,
                    "resource_type", "auto"
            );

            Map<?, ?> uploadResult = cloudinary.uploader().upload(imagen.getBytes(), uploadOptions);
            return (String) uploadResult.get("public_id");
        } catch (Exception e) {
            throw new RuntimeException("Error: ha ocurrido un error al subir la imagen a Cloudinary " + e.getMessage(), e);
        }
    }

    /**
     * Método para obtener la url (con HTTPS) de una imagen en Cloudinary
     *
     * @param publicId Identificador único del recurso en Cloudinary
     * @return Url segura de acceso a la imagen
     * @throws IllegalArgumentException Sí el public_id es nulo o vacío
     * @throws RuntimeException         Sí ocurre un error al generar la Url a partir del public_id
     */
    @Override
    public String getImagenUrl(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            return null;
        }

        try {
            return cloudinary.url().secure(true).generate(publicId);
        } catch (Exception e) {
            throw new RuntimeException("Error al intentar obtener la url segura de la imagen " + e.getMessage(), e);
        }
    }

    /**
     * Método para eliminar una imagen de Cloudinary a partir de su public_id
     *
     * @param publicId Identificador único del recurso en Cloudinary
     * @throws IllegalArgumentException Sí el public_id es nulo o vacío
     * @throws RuntimeException         Sí ocurre un error al eliminar el recurso en Cloudinary
     */
    @Override
    public void deleteImagen(String publicId) {
        if (publicId == null || publicId.isEmpty()) {
            throw new IllegalArgumentException("Error: no existe el publicId " + publicId);
        }

        try {
            cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
        } catch (Exception e) {
            throw new RuntimeException("Error al intentar eliminar la imagen " + e.getMessage(), e);
        }
    }
}
