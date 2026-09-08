package br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound;

import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernancaProcessado;

public interface ProcessamentoSaasPort {

    void registrarSucesso(ServicoGovernancaProcessado item);

    void registrarErro(ServicoGovernancaProcessado item);
}