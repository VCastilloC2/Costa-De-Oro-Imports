package com.application.service.implementation;

import com.application.service.interfaces.ImagenService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Service
public class ImagenServiceImpl implements ImagenService {

    private final String baseDir = "C://gestion-ventas//";

    private final Map<String, String> directorios = Map.of(
            "perfil-usuario", baseDir + "perfil-usuario//",
            "perfil-empresa", baseDir + "perfil-empresa//",
            "imagen-producto", baseDir + "imagen-producto//",
            "imagen-categoria", baseDir + "imagen-categoria//",
            "imagen-blog", baseDir + "imagen-blog//"
    );

    /**
     * Método para asignar una foto
     *
     * @param imagen interfaz que recibe la imagen que se cargue en el formulario
     * @param tipo tipo de directorio donde se guardara la imagen
     * @return el nombre de la foto con un formato en milisegundos más su extensión (.jpg, .png, étc)
     * o un null si el usuario no cuenta con una foto de perfil
     * @apiNote: La interfaz MultipartFile, es usada para representar un archivo
     * cargado mediante un formulario multipart/form-data,
     * (típico en formularios HTML con <input type="file">).
     */
    @Override
    public String asignarImagen(MultipartFile imagen, String tipo) {

        String directorio = directorios.get(tipo);
        if (directorio == null) {
            throw new IllegalArgumentException("error: tipo de imagen invalido: " + tipo);
        }

        try {
            if (!imagen.isEmpty()) {

                String original = imagen.getOriginalFilename();

                if (original == null || !original.contains(".")) {
                    throw new IllegalArgumentException("El archivo no tiene una extensión válida.");
                }

                String extension = original.substring(original.lastIndexOf('.') + 1)
                        .toLowerCase();

                if (!extension.matches("jpg|jpeg|png|gif|webp")) {
                    throw new IllegalArgumentException("Formato de imagen no permitido.");
                }

                String nombreFoto = System.currentTimeMillis() + "." + extension;

                Path carpeta = Paths.get(directorio).toAbsolutePath().normalize();
                Path rutaCompleta = carpeta.resolve(nombreFoto).normalize();

                if (!rutaCompleta.startsWith(carpeta)) {
                    throw new SecurityException("Ruta de archivo inválida.");
                }

                byte[] bytesImagen = imagen.getBytes();
                Files.write(rutaCompleta, bytesImagen);

                return nombreFoto;
            } else {
                return null;
            }
        } catch (IOException e) {
            throw new RuntimeException("error: ha ocurrido un error al guardar la imagen " + e.getMessage(), e);
        }
    }

}