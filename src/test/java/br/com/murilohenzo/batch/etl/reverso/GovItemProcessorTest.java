//package br.com.murilohenzo.batch.etl.reverso;
//
//import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernanca;
//import br.com.murilohenzo.batch.etl.reverso.batch.processor.GovItemProcessor;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDateTime;
//import static org.assertj.core.api.Assertions.*;
//
//class GovItemProcessorTest {
//    private final GovItemProcessor processor = new GovItemProcessor();
//    private final LocalDateTime version = LocalDateTime.of(2026, 9, 16, 9, 20);
//
//    @Test
//    void normalizesPayloadWithoutChangingIdentityOrVersion() {
//        var source = new ServicoGovernanca(1, "00000000-0000-0000-0000-000000000001",
//                "  Produto  ", " ativo ", version);
//        var result = processor.process(source);
//        assertThat(result.dsNomeProduto()).isEqualTo("Produto");
//        assertThat(result.dsStatus()).isEqualTo("ATIVO");
//        assertThat(result.cdServicoOrigem()).isEqualTo(source.cdServicoOrigem());
//        assertThat(result.cdServicoGovernanca()).isEqualTo(1);
//        assertThat(result.dtModificacao()).isEqualTo(version);
//    }
//
//    @Test
//    void rejectsInvalidDataInsteadOfSilentlyMarkingSuccess() {
//        assertThatThrownBy(() -> processor.process(new ServicoGovernanca(1, "invalid", "Nome", "ATIVO", version)))
//                .isInstanceOf(IllegalArgumentException.class);
//        assertThatThrownBy(() -> processor.process(new ServicoGovernanca(1,
//                "00000000-0000-0000-0000-000000000001", " ", "ATIVO", version)))
//                .isInstanceOf(IllegalArgumentException.class);
//        assertThatThrownBy(() -> processor.process(new ServicoGovernanca(1,
//                "00000000-0000-0000-0000-000000000001", "Nome", "DESCONHECIDO", version)))
//                .isInstanceOf(IllegalArgumentException.class);
//        assertThatThrownBy(() -> processor.process(new ServicoGovernanca(1,
//                "00000000-0000-0000-0000-000000000001", "Nome", "ATIVO", null)))
//                .isInstanceOf(IllegalArgumentException.class);
//    }
//}
