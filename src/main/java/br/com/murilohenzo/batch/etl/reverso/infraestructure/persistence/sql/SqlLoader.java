package br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.sql;

import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class SqlLoader {

    private static final String CLASSPATH_PREFIX = "classpath:";
    private final ResourceLoader resourceLoader;
    private final Map<String, String> cache = new ConcurrentHashMap<>();

    public SqlLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
    }

    public String load(String path) {
        return cache.computeIfAbsent(path, this::read);
    }

    private String read(String path) {
        try {
            return resourceLoader
                    .getResource(CLASSPATH_PREFIX + path)
                    .getContentAsString(StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new IllegalStateException(
                    "Erro ao carregar SQL: " + path,
                    exception
            );
        }
    }
}