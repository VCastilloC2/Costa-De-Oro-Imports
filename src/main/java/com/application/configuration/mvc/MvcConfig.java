package com.application.configuration.mvc;

import jakarta.annotation.PostConstruct;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Map;

@Configuration
public class MvcConfig implements WebMvcConfigurer {

    // Ruta base donde se almacenarán las carpetas de imágenes.
    private final String baseDir = "C:/gestion-ventas/";

    /**
     * Mapa con el nombre lógico del recurso (clave) y
     * con su directorio físico correspondiente (valor).
     */
    private final Map<String, String> directorios = Map.of(
            "perfil-usuario", baseDir + "perfil-usuario/",
            "perfil-empresa", baseDir + "perfil-empresa/",
            "imagen-producto", baseDir + "imagen-producto/",
            "imagen-categoria", baseDir + "imagen-categoria/",
            "imagen-blog", baseDir + "imagen-blog/");

    @Autowired
    private ResourceLoader resourceLoader;

    @Override
    public void addResourceHandlers(@NotNull ResourceHandlerRegistry registry) {
        WebMvcConfigurer.super.addResourceHandlers(registry);
        // recorremos los directorios y le enseñamos la ruta y ubicación a spring para
        // que sepa donde buscar
        directorios.forEach((tipo, ruta) -> {
            registry.addResourceHandler(tipo + "/**")
                    .addResourceLocations("file:/" + ruta);
        });
    }

    /**
     * Método para crear los directorios y cargar las imágenes en las rutas
     * especificadas
     *
     * Este método se inicializa automáticamente
     */
    @PostConstruct
    public void init() {
        directorios.forEach((tipo, ruta) -> {
            try {
                // Si el directorio físico no existe, lo creamos automáticamente
                Path path = Paths.get(ruta);
                if (!Files.exists(path)) {
                    Files.createDirectories(path);
                }
            } catch (IOException e) {
                throw new RuntimeException(
                        "error: no se ha podido crear el directorio de imágenes " + ruta + " " + e.getMessage() + e);
            }
        });

        // Creamos las imágenes por defecto dentro de del directorio
        // 'C:/gestion-ventas/**'
        // imagen de usuario
        copiarImagen("static/Assets/Img/ExperenciaUsuarios/imagen__user.jpg", "perfil-user.jpg", "perfil-usuario");
        // imagen de empresa
        copiarImagen("static/Assets/Img/ExperenciaUsuarios/logo__hotel2.png", "perfil-empresa.png", "perfil-empresa");
    }

    /**
     * Método para copiar una imagen desde dentro del proyecto (carpeta resources)
     * hacia uno de los directorios externos ubicados den "C:/gestion-ventas/**".
     *
     * @param rutaOrigen   aquí indicamos la ruta dentro de resources (ejemplo:
     *                     static/img/default.png)
     * @param nombreImagen nombre que tendrá la imagen en el directorio externo
     * @param tipo         tipo de directorio donde queremos guardar la imagen
     *                     (perfil-usuario, perfil-empresa, imagen-producto,
     *                     imagen-blog)
     */
    public void copiarImagen(String rutaOrigen, String nombreImagen, String tipo) {

        // Validamos que el tipo exista en el mapa
        String directorio = directorios.get(tipo);
        if (directorio == null) {
            throw new IllegalArgumentException("error: tipo de directorio no válido " + tipo);
        }

        try {
            // usamos el prefijo 'classpath:' porque estamos buscando dentro del proyecto,
            // específicamente en 'resources/*+'
            Resource resource = resourceLoader.getResource("classpath:" + rutaOrigen);

            if (!resource.exists()) {
                throw new IllegalArgumentException("La ruta específicada no existe: " + rutaOrigen);
            }

            Path rutaDestino = Paths.get(directorio + nombreImagen);

            if (!Files.exists(rutaDestino)) {
                try (InputStream inputStream = resource.getInputStream(); // InputStream: Se encarga de leer datos
                        OutputStream outputStream = Files.newOutputStream(rutaDestino); // OutputStream: Se encarga de
                                                                                        // escribir datos (en bytes)
                ) {
                    FileCopyUtils.copy(inputStream, outputStream);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(
                    "error: ha ocurrido un error al copiar la imagen: " + nombreImagen + " " + e.getMessage(), e);
        }

    }
}