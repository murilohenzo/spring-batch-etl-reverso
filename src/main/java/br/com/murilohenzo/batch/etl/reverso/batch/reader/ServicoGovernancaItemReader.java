package br.com.murilohenzo.batch.etl.reverso.batch.reader;

import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernanca;
import br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound.ServicoGovernancaConsultaPort;
import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.listener.StepExecutionListener;
import org.springframework.batch.core.step.StepExecution;
import org.springframework.batch.infrastructure.item.database.JdbcPagingItemReader;
import org.springframework.batch.infrastructure.item.database.Order;
import org.springframework.batch.infrastructure.item.database.support.MySqlPagingQueryProvider;
import org.springframework.jdbc.core.RowMapper;

import javax.sql.DataSource;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Map;

@Slf4j
public class ServicoGovernancaItemReader
        extends JdbcPagingItemReader<ServicoGovernanca>
        implements StepExecutionListener {

    private long pageQueries;
    private long queryNanos;

    public ServicoGovernancaItemReader(
            DataSource dataSource,
            ServicoGovernancaConsultaPort consultaPort,
            RowMapper<ServicoGovernanca> rowMapper,
            int pageSize,
            int maxAttempt
    ) {
        super(
                dataSource,
                queryProvider(consultaPort)
        );

        validatePageSize(pageSize);

        setName("ServicoGovernancaItemReader");
        setPageSize(pageSize);
        setFetchSize(pageSize);
        setSaveState(false);
        setRowMapper(rowMapper);

        setParameterValues(Map.of("qtMaxTentativas", maxAttempt));
    }

    private static MySqlPagingQueryProvider queryProvider(
            ServicoGovernancaConsultaPort consultaPort
    ) {
        var provider = new MySqlPagingQueryProvider();

        provider.setSelectClause(
                consultaPort.selectClause()
        );

        provider.setFromClause(
                consultaPort.fromClause()
        );

        provider.setWhereClause(
                consultaPort.whereClause()
        );

        provider.setSortKeys(
                Map.of(
                        "s.cdServicoGovernanca",
                        Order.ASCENDING
                )
        );

        return provider;
    }

    private static void validatePageSize(int pageSize) {
        if (pageSize < 1) {
            throw new IllegalArgumentException(
                    "simulacao.page-size deve ser positivo"
            );
        }
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
    public void beforeStep(@NonNull StepExecution stepExecution) {
        pageQueries = 0;
        queryNanos = 0;
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {

        stepExecution
                .getExecutionContext()
                .putLong(
                        "reader.pageQueries",
                        pageQueries
                );

        stepExecution
                .getExecutionContext()
                .putLong(
                        "reader.queryNanos",
                        queryNanos
                );

        LocalDateTime startTime =
                stepExecution.getStartTime();

        LocalDateTime endTime =
                stepExecution.getEndTime() != null
                        ? stepExecution.getEndTime()
                        : LocalDateTime.now();

        long durationMs =
                startTime != null
                        ? Duration
                        .between(startTime, endTime)
                        .toMillis()
                        : -1L;

        log.info(
                """
                SIMULACAO_RESUMO \
                stepExecutionId={} \
                status={} \
                readCount={} \
                writeCount={} \
                filterCount={} \
                pageQueries={} \
                pageSize={} \
                queryMs={} \
                durationMs={}
                """,
                stepExecution.getId(),
                stepExecution.getStatus(),
                stepExecution.getReadCount(),
                stepExecution.getWriteCount(),
                stepExecution.getFilterCount(),
                pageQueries,
                getPageSize(),
                queryNanos / 1_000_000,
                durationMs
        );

        return stepExecution.getExitStatus();
    }
}