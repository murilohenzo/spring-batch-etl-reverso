//package br.com.murilohenzo.batch.etl.reverso;
//
//import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernancaProcessado;
//import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernanca;
//import br.com.murilohenzo.batch.etl.reverso.batch.processor.GovItemProcessor;
//import br.com.murilohenzo.batch.etl.reverso.batch.reader.ServicoGovernancaItemReader;
//import br.com.murilohenzo.batch.etl.reverso.batch.validator.JobParametersInvalidException;
//import br.com.murilohenzo.batch.etl.reverso.batch.writer.GovItemWriter;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.job.Job;
//import org.springframework.batch.core.job.JobExecution;
//import org.springframework.batch.core.job.builder.JobBuilder;
//import org.springframework.batch.core.job.parameters.JobParameters;
//import org.springframework.batch.core.job.parameters.JobParametersBuilder;
//import org.springframework.batch.core.launch.JobOperator;
//import org.springframework.batch.core.repository.JobRepository;
//import org.springframework.batch.core.repository.support.ResourcelessJobRepository;
//import org.springframework.batch.core.step.StepExecution;
//import org.springframework.batch.core.step.builder.StepBuilder;
//import org.springframework.batch.infrastructure.item.Chunk;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.core.io.ClassPathResource;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.datasource.DataSourceTransactionManager;
//import org.springframework.jdbc.datasource.init.ResourceDatabasePopulator;
//import org.springframework.test.context.DynamicPropertyRegistry;
//import org.springframework.test.context.DynamicPropertySource;
//import org.springframework.transaction.PlatformTransactionManager;
//import org.springframework.transaction.support.TransactionTemplate;
//import org.testcontainers.junit.jupiter.Container;
//import org.testcontainers.junit.jupiter.Testcontainers;
//import org.testcontainers.mysql.MySQLContainer;
//
//import javax.sql.DataSource;
//import java.time.LocalDateTime;
//import java.util.List;
//import java.util.UUID;
//import java.util.stream.IntStream;
//
//import static org.assertj.core.api.Assertions.*;
//
//@Testcontainers
//@SpringBootTest(properties = {
//        "spring.batch.job.enabled=false",
//        "spring.batch.page-size=3",
//        "spring.batch.chunk-size=2",
//        "logging.level.br.com.murilohenzo.batch.etl.reverso.writer=WARN"
//})
//class EtlReversoBatchIT {
//    @Container
//    static final MySQLContainer MYSQL = new MySQLContainer("mysql:8.4")
//            .withDatabaseName("etl_reverso_test").withUsername("batch").withPassword("batch");
//
//    @DynamicPropertySource
//    static void database(DynamicPropertyRegistry registry) {
//        registry.add("spring.datasource.url", () -> MYSQL.getJdbcUrl()
//                + "?connectionTimeZone=UTC&forceConnectionTimeZoneToSession=true");
//        registry.add("spring.datasource.username", MYSQL::getUsername);
//        registry.add("spring.datasource.password", MYSQL::getPassword);
//    }
//
//    @Autowired JdbcTemplate jdbc;
//    @Autowired DataSource dataSource;
//    @Autowired PlatformTransactionManager transactionManager;
//    @Autowired JobRepository repository;
//    @Autowired JobOperator operator;
//    @Autowired Job etlReversoJob;
//    @Autowired GovItemWriter writer;
//
//    @BeforeEach
//    void cleanBusinessData() {
//        script("mysql/99-cleanup.sql");
//    }
//
//    @Test
//    void infrastructureUsesMysqlAndOneTransactionalDataSource() throws Exception {
//        assertThat(jdbc.getDataSource()).isSameAs(dataSource);
//        assertThat(((DataSourceTransactionManager) transactionManager).getDataSource()).isSameAs(dataSource);
//        assertThat(repository).isNotInstanceOf(ResourcelessJobRepository.class);
//        try (var connection = dataSource.getConnection()) {
//            assertThat(connection.getMetaData().getDatabaseProductName()).isEqualTo("MySQL");
//        }
//        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM BATCH_JOB_INSTANCE", Long.class)).isNotNull();
//    }
//
//    @Test
//    void d0IsIdempotentAndRetainsTechnicalKeys() throws Exception {
//        d0();
//        var keys = jdbc.queryForList("SELECT cdServicoGovernanca FROM servico_governanca ORDER BY cdServicoOrigem", Long.class);
//        d0();
//        assertThat(jdbc.queryForList("SELECT cdServicoGovernanca FROM servico_governanca ORDER BY cdServicoOrigem", Long.class))
//                .isEqualTo(keys);
//        assertCounts(run(), 5);
//        assertThat(count("processamento_saas")).isEqualTo(5);
//        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM processamento_saas WHERE dsStatusProcessamento='SUCESSO'", Long.class))
//                .isEqualTo(5);
//        assertThat(jdbc.queryForObject("SELECT COUNT(*) FROM processamento_saas p JOIN servico_governanca s "
//                + "ON p.cdServicoOrigem=s.cdServicoOrigem WHERE p.dtUltimoSucesso=s.dtModificacao", Long.class)).isEqualTo(5);
//        assertCounts(run(), 0);
//        assertThat(jdbc.queryForObject("SELECT SUM(qtTentativas) FROM processamento_saas", Long.class)).isEqualTo(5);
//    }
//
//    @Test
//    void mixedIncrementalReadsExactlyTwoChangedAndTwoNewRecords() throws Exception {
//        d0(); run();
//        script("mysql/dml/02-d1.sql");
//        assertThat(eligible()).containsExactly(id(2), id(4), id(6), id(7));
//        assertCounts(run(), 4);
//        assertThat(count("servico_governanca")).isEqualTo(7);
//        assertThat(count("processamento_saas")).isEqualTo(7);
//        script("mysql/dml/03-idempotencia.sql");
//        script("mysql/dml/02-d1.sql");
//        assertCounts(run(), 0);
//        assertThat(jdbc.queryForObject("SELECT COUNT(DISTINCT cdServicoOrigem) FROM servico_governanca", Long.class)).isEqualTo(7);
//        assertThat(jdbc.queryForObject("SELECT SUM(qtTentativas) FROM processamento_saas", Long.class)).isEqualTo(9);
//    }
//
//    @Test
//    void onlyNewRecordsAreSelected() throws Exception {
//        d0(); run(); script("mysql/dml/03-idempotencia.sql");
//        assertThat(eligible()).containsExactly(id(6), id(7));
//        assertCounts(run(), 2);
//    }
//
//    @Test
//    void onlyModifiedRecordsAreSelected() throws Exception {
//        d0(); run();
//        jdbc.update("UPDATE servico_governanca SET dtModificacao=dtModificacao + INTERVAL 1 MICROSECOND WHERE cdServicoOrigem IN (?, ?)", id(2), id(4));
//        assertThat(eligible()).containsExactly(id(2), id(4));
//        assertCounts(run(), 2);
//    }
//
//    @Test
//    void nullSuccessRetriesAndTechnicalOrOlderChangesDoNot() throws Exception {
//        d0(); run();
//        jdbc.update("UPDATE processamento_saas SET dtUltimoSucesso=NULL, dsStatusProcessamento='ERRO' WHERE cdServicoOrigem=?", id(3));
//        jdbc.update("UPDATE servico_governanca SET dtAtualizacao='2030-01-01' WHERE cdServicoOrigem=?", id(1));
//        jdbc.update("UPDATE servico_governanca SET dtModificacao='2020-01-01' WHERE cdServicoOrigem=?", id(2));
//        assertThat(eligible()).containsExactly(id(3));
//        assertCounts(run(), 1);
//        assertCounts(run(), 0);
//    }
//
//    @Test
//    void pagingDoesNotSkipItemsAsEligibilityShrinks() throws Exception {
//        insertRange(1, 37);
//        var step = assertCounts(run(), 37);
//        assertThat(step.getExecutionContext().getLong("reader.pageQueries")).isGreaterThan(1);
//        assertThat(step.getExecutionContext().getLong("reader.queryNanos")).isPositive();
//        assertThat(count("processamento_saas")).isEqualTo(37);
//        assertCounts(run(), 0);
//    }
//
//    @Test
//    void failedChunkRestartsWithOnlyUncommittedRecords() throws Exception {
//        d0();
//        jdbc.update("UPDATE servico_governanca SET dsNomeProduto=' ' WHERE cdServicoOrigem=?", id(3));
//        var parameters = parameters();
//        var failed = operator.start(etlReversoJob, parameters);
//        assertThat(failed.getStatus()).isEqualTo(BatchStatus.FAILED);
//        assertThat(count("processamento_saas")).isEqualTo(2);
//        jdbc.update("UPDATE servico_governanca SET dsNomeProduto='Produto corrigido' WHERE cdServicoOrigem=?", id(3));
//        var restarted = operator.start(etlReversoJob, parameters);
//        assertThat(restarted.getJobInstance().getId()).isEqualTo(failed.getJobInstance().getId());
//        assertCounts(restarted, 3);
//        assertThat(count("processamento_saas")).isEqualTo(5);
//        assertThat(jdbc.queryForObject("SELECT SUM(qtTentativas) FROM processamento_saas", Long.class)).isEqualTo(5);
//    }
//
//    @Test
//    void jdbcWriteFailureRollsBackEntireChunk() {
//        d0();
//        var valid = new ServicoGovernancaProcessado(1, id(1), "Produto", "ATIVO", LocalDateTime.of(2026, 9, 1, 8, 0));
//        var missing = new ServicoGovernancaProcessado(999, id(999), "Ausente", "ATIVO", valid.dtModificacao());
//        assertThatThrownBy(() -> new TransactionTemplate(transactionManager)
//                .executeWithoutResult(status -> writer.write(new Chunk<>(List.of(valid, missing)))))
//                .isInstanceOf(org.springframework.dao.DataAccessException.class);
//        assertThat(count("processamento_saas")).isZero();
//    }
//
//    @Test
//    void concurrentSourceChangeRemainsEligibleAfterOldVersionIsWritten() {
//        d0();
//        var old = new ServicoGovernancaProcessado(1, id(1), "Produto", "ATIVO", LocalDateTime.of(2026, 9, 1, 8, 0));
//        jdbc.update("UPDATE servico_governanca SET dtModificacao='2026-09-16 09:00:00' WHERE cdServicoOrigem=?", id(1));
//        new TransactionTemplate(transactionManager).executeWithoutResult(status -> writer.write(new Chunk<>(List.of(old))));
//        assertThat(eligible()).contains(id(1));
//    }
//
//    @Test
//    void schemaEnforcesGovernedTypesAndCleanupPreservesUsability() throws Exception {
//        assertThat(jdbc.queryForList("SELECT COLUMN_TYPE FROM information_schema.COLUMNS WHERE TABLE_SCHEMA=DATABASE() "
//                + "AND TABLE_NAME IN ('servico_governanca','processamento_saas') AND COLUMN_NAME='cdServicoOrigem'", String.class))
//                .containsExactlyInAnyOrder("char(36)", "char(36)");
//        d0(); run();
//        assertThatThrownBy(() -> jdbc.update("UPDATE processamento_saas SET qtTentativas=-1 WHERE cdServicoOrigem=?", id(1)))
//                .isInstanceOf(org.springframework.dao.DataAccessException.class);
//        script("mysql/99-cleanup.sql");
//        assertThat(count("servico_governanca")).isZero();
//        assertThat(count("processamento_saas")).isZero();
//        d0(); assertCounts(run(), 5);
//    }
//
//    @Test
//    void requiresAnExplicitExecutionIdentity() {
//        assertThatThrownBy(() -> operator.start(etlReversoJob, new JobParametersBuilder().toJobParameters()))
//                .isInstanceOf(JobParametersInvalidException.class);
//    }
//
//    @Test
//    void explainUsesGovernedColumns() {
//        d0();
//        assertThat(jdbc.queryForList("EXPLAIN ANALYZE " + ServicoGovernancaItemReader.SELECT + ServicoGovernancaItemReader.FROM
//                + ServicoGovernancaItemReader.WHERE + " ORDER BY s.cdServicoGovernanca LIMIT 3", String.class)).isNotEmpty();
//    }
//
//    @Test
//    void volumeReadsFiftyThousandThenOnlySixThousandTwoHundredChanges() throws Exception {
//        script("mysql/volume/10-volume-d0.sql");
//        var d0Step = assertCounts(runVolume(), 50_000);
//        script("mysql/volume/11-volume-d1.sql");
//        assertThat(count("servico_governanca")).isEqualTo(55_000);
//        var plan = jdbc.queryForList("EXPLAIN ANALYZE " + ServicoGovernancaItemReader.SELECT + ServicoGovernancaItemReader.FROM
//                + ServicoGovernancaItemReader.WHERE + " ORDER BY s.cdServicoGovernanca LIMIT 1000", String.class);
//        var d1Step = assertCounts(runVolume(), 6_200);
//        assertThat(count("processamento_saas")).isEqualTo(55_000);
//        script("mysql/volume/11-volume-d1.sql");
//        var repeat = assertCounts(runVolume(), 0);
//        java.nio.file.Files.writeString(java.nio.file.Path.of("target/volume-result.txt"),
//                "MySQL 8.4; pageSize=1000; chunkSize=1000\n"
//                + "D0: " + d0Step + "\nD1: " + d1Step + "\nRepeticao: " + repeat
//                + "\nEXPLAIN ANALYZE antes do D1:\n" + String.join("\n", plan));
//    }
//
//    private JobExecution runVolume() throws Exception {
//        // Reader novo por execucao, como ocorre com o bean StepScope do job principal.
//        var reader = new ServicoGovernancaItemReader(dataSource, 1000);
//        reader.afterPropertiesSet();
//        var step = new StepBuilder("volumeStep", repository)
//                .<ServicoGovernanca, ServicoGovernancaProcessado>chunk(1000)
//                .transactionManager(transactionManager).reader(reader)
//                .processor(new GovItemProcessor()).writer(writer).build();
//        var job = new JobBuilder("volumeJob", repository).start(step).build();
//        return operator.start(job, parameters());
//    }
//
//    void insertRange(int start, int end) {
//        var ids = IntStream.rangeClosed(start, end).boxed().toList();
//        jdbc.batchUpdate("INSERT INTO servico_governanca (cdServicoOrigem, dsNomeProduto, dsStatus, dtModificacao) "
//                + "VALUES (?, ?, 'ATIVO', '2026-09-01 08:00:00')", ids, 1000, (ps, number) -> {
//            ps.setString(1, id(number)); ps.setString(2, "Produto " + number);
//        });
//    }
//
//    private void d0() { script("mysql/dml/01-d0.sql"); }
//    private void script(String path) { new ResourceDatabasePopulator(new ClassPathResource(path)).execute(dataSource); }
//    private long count(String table) { return jdbc.queryForObject("SELECT COUNT(*) FROM " + table, Long.class); }
//    private List<String> eligible() {
//        return jdbc.query(ServicoGovernancaItemReader.SELECT + ServicoGovernancaItemReader.FROM + ServicoGovernancaItemReader.WHERE + " ORDER BY s.cdServicoGovernanca",
//                (rs, row) -> rs.getString("cdServicoOrigem"));
//    }
//    private JobParameters parameters() { return new JobParametersBuilder().addString("carga", UUID.randomUUID().toString()).toJobParameters(); }
//    private JobExecution run() throws Exception { return operator.start(etlReversoJob, parameters()); }
//    private static String id(int number) { return "00000000-0000-0000-0000-%012d".formatted(number); }
//    private StepExecution assertCounts(JobExecution execution, long expected) {
//        assertThat(execution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
//        assertThat(execution.getStepExecutions()).hasSize(1);
//        var step = execution.getStepExecutions().iterator().next();
//        assertThat(step.getReadCount()).isEqualTo(expected);
//        assertThat(step.getWriteCount()).isEqualTo(expected);
//        assertThat(step.getFilterCount()).isZero();
//        return step;
//    }
//}
