package br.com.murilohenzo.batch.etl.reverso.batch.job;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class EtlReversoJobParameters {
    private String carga;
}