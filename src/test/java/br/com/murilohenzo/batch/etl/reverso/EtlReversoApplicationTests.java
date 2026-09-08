//package br.com.murilohenzo.batch.etl.reverso;
//
//import br.com.murilohenzo.batch.etl.reverso.batch.config.JobParameterNames;
//import org.junit.jupiter.api.Test;
//import org.springframework.batch.core.BatchStatus;
//import org.springframework.batch.core.job.JobExecution;
//import org.springframework.batch.core.job.parameters.JobParameters;
//import org.springframework.batch.core.job.parameters.JobParametersBuilder;
//import org.springframework.batch.test.JobOperatorTestUtils;
//import org.springframework.batch.test.context.SpringBatchTest;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//
//import java.util.UUID;
//
//import static org.assertj.core.api.Assertions.assertThat;
//
//@SpringBootTest(properties = "spring.batch.job.enabled=false")
//@SpringBatchTest
//class EtlReversoApplicationTests {
//
//	@Autowired
//	private JobOperatorTestUtils jobOperatorTestUtils;
//
//	@Test
//	void contextLoads() {
//		// Smoke test puro: contexto sobe sem exceção de bean creation,
//		// sem disparar o job automaticamente no startup.
//	}
//
//	@Test
//	void jobExecutaComCargaIdInjetadoENaoRejeitaParametroObrigatorio() throws Exception {
//		String cargaId = "contextLoads-" + UUID.randomUUID();
//
//		JobParameters jobParameters = new JobParametersBuilder()
//				.addString(JobParameterNames.CARGA, cargaId)
//				.toJobParameters();
//
//		JobExecution jobExecution = jobOperatorTestUtils.startJob(jobParameters);
//
//		assertThat(jobExecution.getStatus()).isEqualTo(BatchStatus.COMPLETED);
//	}
//}