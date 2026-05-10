package com.application.service.http;

import com.application.persistence.entity.DocumentChunk;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Getter
@Service
@RequiredArgsConstructor
public class DocumentLoaderService {

    private final List<DocumentChunk> chunks = new ArrayList<>();

    @PostConstruct
    public void loadDocuments() {
        try {
            PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
            Resource[] resources = resolver.getResources("classpath:/documents/*.txt");

            int counter = 1;

            for (Resource resource : resources) {
                String fileName = resource.getFilename();
                String content = readResource(resource);
                List<String> parts = splitIntoChunks(content, 450);

                for (String part : parts) {
                    chunks.add(new DocumentChunk(
                            "chunk-" + counter,
                            fileName,
                            part
                    ));
                    counter++;
                }
            }

            System.out.println("Documentos RAG cargados. Fragmentos: " + chunks.size());

        } catch (Exception e) {
            throw new RuntimeException("Error cargando documentos RAG", e);
        }
    }

    private String readResource(Resource resource) throws Exception {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8))) {

            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line).append("\n");
            }
        }

        return sb.toString();
    }

    private List<String> splitIntoChunks(String text, int maxLength) {
        List<String> result = new ArrayList<>();
        String[] paragraphs = text.split("\\n\\s*\\n");

        StringBuilder current = new StringBuilder();

        for (String paragraph : paragraphs) {
            if (current.length() + paragraph.length() > maxLength) {
                if (!current.isEmpty()) {
                    result.add(current.toString().trim());
                    current = new StringBuilder();
                }
            }

            current.append(paragraph.trim()).append("\n\n");
        }

        if (!current.isEmpty()) {
            result.add(current.toString().trim());
        }

        return result;
    }
}