package br.com.murilohenzo.batch.etl.reverso.batch.config;

import br.com.murilohenzo.batch.etl.reverso.batch.job.EtlReversoJobParameters;
import br.com.murilohenzo.batch.etl.reverso.batch.processor.ServicoGovernancaItemProcessor;
import br.com.murilohenzo.batch.etl.reverso.batch.writer.ServicoGovernancaItemWriter;
import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernanca;
import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernancaProcessado;
import br.com.murilohenzo.batch.etl.reverso.batch.reader.ServicoGovernancaItemReader;
import br.com.murilohenzo.batch.etl.reverso.batch.validator.EtlReversoJobParametersValidator;
import br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound.ProcessamentoSaasPort;
import br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound.ServicoGovernancaConsultaPort;
import br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.mapper.ServicoGovernancaRowMapper;
import lombok.extern.slf4j.Slf4j;
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
import org.springframework.transaction.PlatformTransactionManager;

import javax.sql.DataSource;

@Slf4j
@Configuration(proxyBeanMethods = false)
public class EtlReversoConfiguration {

    @Bean
    @StepScope
    public EtlReversoJobParameters etlReversoJobParameters(
            @Value("#{jobParameters['" + JobParameterNames.CARGA + "']}")
            String cargaId
    ) {
        return new EtlReversoJobParameters(cargaId);
    }

    @Bean
    @StepScope
    public ServicoGovernancaItemReader govItemReader(
            DataSource dataSource,
            ServicoGovernancaConsultaPort consultaPort,
            ServicoGovernancaRowMapper rowMapper,
            EtlReversoBatchProperties properties
    ) {
        return new ServicoGovernancaItemReader(
                dataSource,
                consultaPort,
                rowMapper,
                properties.pageSize(),
                properties.maxAttempt()
        );
    }

    @Bean
    public ServicoGovernancaItemProcessor servicoGovernancaItemProcessor() {
        return new ServicoGovernancaItemProcessor();
    }

    @Bean
    public ServicoGovernancaItemWriter govItemWriter(
            ProcessamentoSaasPort processamentoSaasPort
    ) {
        return new ServicoGovernancaItemWriter(processamentoSaasPort);
    }

    @Bean
    public Step governancaStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            ServicoGovernancaItemReader reader,
            ServicoGovernancaItemProcessor processor,
            ServicoGovernancaItemWriter writer,
            EtlReversoBatchProperties properties
    ) {

        log.info(
                "ETL_REVERSO_CONFIG step=governancaStep chunkSize={} pageSize={}",
                properties.chunkSize(),
                properties.pageSize()
        );

        return new StepBuilder(
                "governancaStep",
                jobRepository
        )
                .<ServicoGovernanca, ServicoGovernancaProcessado>
                        chunk(properties.chunkSize())
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
            EtlReversoJobParametersValidator validator
    ) {
        return new JobBuilder(
                "etlReversoJob",
                jobRepository
        )
                .validator(validator)
                .start(governancaStep)
                .build();
    }
}