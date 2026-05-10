package com.application.persistence.entity;

import lombok.*;


@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@Builder
public class DocumentChunk {
    private String id;
    private String source;
    private String content;
    private double score;

    public DocumentChunk(String id, String source, String content) {
        this.id = id;
        this.source = source;
        this.content = content;
    }

}