package br.com.murilohenzo.batch.etl.reverso.batch.config;

import jakarta.validation.constraints.Max;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import jakarta.validation.constraints.Min;

@Validated
@ConfigurationProperties(prefix = "spring.batch")
public record EtlReversoBatchProperties(
        @Min(1)
        int pageSize,
        @Min(1)
        int chunkSize,
        @Min(1)
        @Max(3)
        int maxAttempt
) { }