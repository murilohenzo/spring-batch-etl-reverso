package br.com.murilohenzo.batch.etl.reverso.batch.writer;

import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernancaProcessado;
import br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound.ProcessamentoSaasPort;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;


public class ServicoGovernancaItemWriter
        implements ItemWriter<ServicoGovernancaProcessado> {

    private final ProcessamentoSaasPort processamentoSaasPort;

    public ServicoGovernancaItemWriter(ProcessamentoSaasPort processamentoSaasPort) {
        this.processamentoSaasPort = processamentoSaasPort;
    }

    @Override
    public void write(
            Chunk<? extends ServicoGovernancaProcessado> chunk
    ) {
        chunk.forEach(processamentoSaasPort::registrarSucesso);
    }
}