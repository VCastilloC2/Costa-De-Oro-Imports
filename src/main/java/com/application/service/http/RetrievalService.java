package com.application.service.http;

import com.application.persistence.entity.DocumentChunk;
import com.application.utils.TextSimilarityUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Comparator;
import java.util.List;

@Service
@RequiredArgsConstructor
public class RetrievalService {

    private final DocumentLoaderService documentLoaderService;

    public List<DocumentChunk> retrieve(String question, int topK) {
        return documentLoaderService.getChunks().stream()
                .map(chunk -> new DocumentChunk(
                        chunk.getId(),
                        chunk.getSource(),
                        chunk.getContent(),
                        TextSimilarityUtil.cosineSimilarity(question, chunk.getContent())
                ))
                .sorted(Comparator.comparingDouble(DocumentChunk::getScore).reversed())
                .limit(topK)
                .toList();
    }

}