package com.application.service.interfaces;

import com.application.presentation.dto.SearchResponse;

public interface SearchService {

    SearchResponse buscarUniversal(
            String query,
            String tipo,
            int limit
    );

}