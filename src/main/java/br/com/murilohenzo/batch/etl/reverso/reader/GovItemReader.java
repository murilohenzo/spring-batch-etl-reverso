package br.com.murilohenzo.batch.etl.reverso.reader;

import br.com.murilohenzo.batch.etl.reverso.model.ServicoGovernanca;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.support.MySqlPagingQueryProvider;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * Nao thread-safe: assume step single-threaded (sem taskExecutor configurado).
 * Se o step vier a ser paralelizado, trocar pageQueries/queryNanos por AtomicLong.
 */
public class GovItemReader extends JdbcPagingItemReader<ServicoGovernanca> implements StepExecutionListener {

    private static final Logger log = LoggerFactory.getLogger(GovItemReader.class);

    public static final String SELECT = """
            SELECT s.cdServicoGovernanca, s.cdServicoOrigem,
                   s.dsNomeProduto, s.dsStatus, s.dtModificacao
            """;

    public static final String FROM = """
            FROM servico_governanca s
            LEFT JOIN processamento_saas p ON p.cdServicoOrigem = s.cdServicoOrigem
            """;

    public static final String WHERE = """
            WHERE (p.cdServicoOrigem IS NULL OR p.dtUltimoSucesso IS NULL
                   OR s.dtModificacao > p.dtUltimoSucesso)
            """;

    private long pageQueries;
    private long queryNanos;

    public GovItemReader(DataSource dataSource, int pageSize) {
        super(dataSource, queryProvider());
        if (pageSize < 1) {
            throw new IllegalArgumentException("simulacao.page-size deve ser positivo");
        }
        setName("govItemReader");
        setPageSize(pageSize);
        setFetchSize(pageSize);
        // O controle transacional e o checkpoint. Retomar reconsulta pendencias,
        // sem restaurar um offset sobre um conjunto que diminuiu apos os commits.
        setSaveState(false);
        setRowMapper((rs, rowNum) -> new ServicoGovernanca(
                rs.getLong("cdServicoGovernanca"), rs.getString("cdServicoOrigem"),
                rs.getString("dsNomeProduto"), rs.getString("dsStatus"),
                rs.getObject("dtModificacao", LocalDateTime.class)));
    }

    private static MySqlPagingQueryProvider queryProvider() {
        var provider = new MySqlPagingQueryProvider();
        provider.setSelectClause(SELECT);
        provider.setFromClause(FROM);
        provider.setWhereClause(WHERE);
        provider.setSortKeys(Map.of("cdServicoGovernanca", Order.ASCENDING));
        return provider;
    }

    @Override
    protected void doReadPage() {
        long start = System.nanoTime();

        try {
            super.doReadPage();
        } finally {
            pageQueries++;
            queryNanos += System.nanoTime() - start;
        }
    }

    @Override
    public void beforeStep(StepExecution step) {
        // Reset defensivo: garante metricas limpas mesmo se o escopo do bean mudar no futuro.
        pageQueries = 0;
        queryNanos = 0;
    }

    @Override
    public ExitStatus afterStep(StepExecution step) {
        step.getExecutionContext().putLong("reader.pageQueries", pageQueries);
        step.getExecutionContext().putLong("reader.queryNanos", queryNanos);

        LocalDateTime startTime = step.getStartTime();
        LocalDateTime endTime = step.getEndTime() != null ? step.getEndTime() : LocalDateTime.now();
        long durationMs = startTime != null ? Duration.between(startTime, endTime).toMillis() : -1L;

        log.info("SIMULACAO_RESUMO stepExecutionId={} status={} readCount={} writeCount={} "
                        + "filterCount={} pageQueries={} pageSize={} queryMs={} durationMs={}",
                step.getId(), step.getStatus(), step.getReadCount(), step.getWriteCount(),
                step.getFilterCount(), pageQueries, getPageSize(), queryNanos / 1_000_000, durationMs);

        return step.getExitStatus();
    }
}