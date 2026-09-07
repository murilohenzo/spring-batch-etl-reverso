package br.com.murilohenzo.batch.etl.reverso.model;

import java.time.LocalDateTime;

public record ServicoGovernancaProcessado(long cdServicoGovernanca, String cdServicoOrigem,
        String dsNomeProduto, String dsStatus, LocalDateTime dtModificacao) {
}
