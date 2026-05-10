package com.application.persistence.entity;

import lombok.*;

@Data
@Getter
@Setter
@AllArgsConstructor
@RequiredArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentChunk {
    private String id;
    private String source;
    private String content;
    private double score;
}