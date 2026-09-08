package br.com.murilohenzo.batch.etl.reverso.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
@AllArgsConstructor
public class ServicoGovernancaProcessado {
    private long cdServicoGovernanca;
    private String cdServicoOrigem;
    private String dsNomeProduto;
    private String dsStatus;
    private LocalDateTime dtModificacao;
}
