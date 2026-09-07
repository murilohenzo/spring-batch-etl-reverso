package br.com.murilohenzo.batch.etl.reverso.config;

import br.com.murilohenzo.batch.etl.reverso.job.EtlReversoJobParameters;
import br.com.murilohenzo.batch.etl.reverso.model.ServicoGovernanca;
import br.com.murilohenzo.batch.etl.reverso.model.ServicoGovernancaProcessado;
import br.com.murilohenzo.batch.etl.reverso.processor.GovItemProcessor;
import br.com.murilohenzo.batch.etl.reverso.reader.GovItemReader;
import br.com.murilohenzo.batch.etl.reverso.validator.EtlReversoJobParametersValidator;
import br.com.murilohenzo.batch.etl.reverso.writer.GovItemWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.job.Job;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.Step;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Configuration(proxyBeanMethods = false)
public class EtlReversoConfiguration {

    private static final Logger LOGGER =
            LoggerFactory.getLogger(EtlReversoConfiguration.class);

    @Bean
    @StepScope
    public EtlReversoJobParameters etlReversoJobParameters(
            @Value("#{jobParameters['" + JobParameterNames.CARGA + "']}")
            String cargaId) {

        return new EtlReversoJobParameters(cargaId);
    }

    @Bean
    @StepScope
    public GovItemReader govItemReader(
            DataSource dataSource,
            EtlReversoBatchProperties properties) {

        return new GovItemReader(
                dataSource,
                properties.pageSize()
        );
    }

    @Bean
    public GovItemProcessor govItemProcessor() {
        return new GovItemProcessor();
    }

    @Bean
    public GovItemWriter govItemWriter(
            JdbcTemplate jdbcTemplate) {

        return new GovItemWriter(jdbcTemplate);
    }

    @Bean
    public Step governancaStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            GovItemReader reader,
            GovItemProcessor processor,
            GovItemWriter writer,
            EtlReversoBatchProperties properties) {

        LOGGER.info(
                "ETL_REVERSO_CONFIG step=governancaStep chunkSize={} pageSize={}",
                properties.chunkSize(),
                properties.pageSize()
        );

        return new StepBuilder("governancaStep", jobRepository)
                .<ServicoGovernanca, ServicoGovernancaProcessado>chunk(properties.chunkSize())
                .transactionManager(transactionManager)
                .reader(reader)
                .processor(processor)
                .writer(writer)
                .listener(reader)
                .build();
    }

    @Bean
    public Job etlReversoJob(
            JobRepository jobRepository,
            Step governancaStep,
            EtlReversoJobParametersValidator validator) {

        return new JobBuilder("etlReversoJob", jobRepository)
                .validator(validator)
                .start(governancaStep)
                .build();
    }
}