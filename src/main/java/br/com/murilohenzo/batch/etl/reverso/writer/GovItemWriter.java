package br.com.murilohenzo.batch.etl.reverso.writer;

import br.com.murilohenzo.batch.etl.reverso.model.ServicoGovernancaProcessado;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.infrastructure.item.Chunk;
import org.springframework.batch.infrastructure.item.ItemWriter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.sql.Types;
import java.util.List;

public class GovItemWriter implements ItemWriter<ServicoGovernancaProcessado> {

    private static final Logger log = LoggerFactory.getLogger(GovItemWriter.class);

    private static final String UPSERT = """
            INSERT INTO processamento_saas
                (cdServicoOrigem, dtUltimoProcessamento, dtUltimoSucesso,
                 dsStatusProcessamento, qtTentativas)
            VALUES (?, CURRENT_TIMESTAMP(6), ?, 'SUCESSO', 1) AS novo
            ON DUPLICATE KEY UPDATE
                dtUltimoProcessamento = novo.dtUltimoProcessamento,
                dtUltimoSucesso = novo.dtUltimoSucesso,
                dsStatusProcessamento = novo.dsStatusProcessamento,
                qtTentativas = processamento_saas.qtTentativas + 1
            """;

    private final JdbcTemplate jdbcTemplate;

    public GovItemWriter(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public void write(Chunk<? extends ServicoGovernancaProcessado> chunk) {
        if (chunk.isEmpty()) {
            return;
        }
        if (!TransactionSynchronizationManager.isActualTransactionActive()) {
            throw new IllegalStateException("Writer requer a transacao JDBC do Step");
        }

        var items = List.copyOf(chunk.getItems());

        var resultados = jdbcTemplate.batchUpdate(UPSERT, items, items.size(), (ps, item) -> {
            ps.setString(1, item.cdServicoOrigem());
            // Watermark da versao lida, nao o relogio local da tentativa.
            ps.setObject(2, item.dtModificacao(), Types.TIMESTAMP);
        });

        if (log.isDebugEnabled()) {
            log.debug("SIMULACAO_SAAS_BATCH tamanho={} resultados={}", items.size(), resultados.length);
        }

        TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
            @Override
            public void afterCommit() {
                for (var item : items) {
                    log.debug("SIMULACAO_SAAS cdServicoOrigem={} cdServicoGovernanca={} versao={}",
                            item.cdServicoOrigem(), item.cdServicoGovernanca(), item.dtModificacao());
                }
                log.info("SIMULACAO_SAAS_CHUNK itens={} primeiraOrigem={} ultimaOrigem={}",
                        items.size(),
                        items.getFirst().cdServicoOrigem(),
                        items.getLast().cdServicoOrigem());
            }
        });
    }
}