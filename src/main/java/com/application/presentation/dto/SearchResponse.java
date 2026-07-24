package com.application.presentation.dto;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {

    @Builder.Default
    private List<SearchItemResponse> productos = new ArrayList<>();

    @Builder.Default
    private List<SearchItemResponse> categorias = new ArrayList<>();

    @Builder.Default
    private List<SearchItemResponse> clientes = new ArrayList<>();

    @Builder.Default
    private List<SearchItemResponse> proveedores = new ArrayList<>();

    @Builder.Default
    private List<SearchItemResponse> blog = new ArrayList<>();

    private Integer totalResults;

    private String query;

    private boolean success;

    private String message;

}