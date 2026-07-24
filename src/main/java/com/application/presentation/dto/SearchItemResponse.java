package com.application.presentation.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchItemResponse {

    private Long id;

    private String type;

    private String titulo;

    private String subtitulo;

    private String descripcion;

    private String imagen;

    private String icono;

    private String url;

    private String badge;

    private String badgeClass;

}