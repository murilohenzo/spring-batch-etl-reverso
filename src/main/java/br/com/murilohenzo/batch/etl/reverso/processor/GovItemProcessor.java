package br.com.murilohenzo.batch.etl.reverso.processor;

import br.com.murilohenzo.batch.etl.reverso.model.ServicoGovernanca;
import br.com.murilohenzo.batch.etl.reverso.model.ServicoGovernancaProcessado;
import org.jspecify.annotations.NonNull;
import org.springframework.batch.infrastructure.item.ItemProcessor;

import java.util.Locale;
import java.util.UUID;

public class GovItemProcessor implements ItemProcessor<ServicoGovernanca, ServicoGovernancaProcessado> {

    private static final int NOME_MAX_LENGTH = 255;
    private static final String STATUS_ATIVO = "ATIVO";
    private static final String STATUS_INATIVO = "INATIVO";

    @Override
    public ServicoGovernancaProcessado process(@NonNull ServicoGovernanca item) {
        if (item.dtModificacao() == null) {
            throw new IllegalArgumentException("dtModificacao e obrigatoria");
        }

        String origem = validarUuidCanonico(required(item.cdServicoOrigem(), "cdServicoOrigem"));
        String nome = validarNome(required(item.dsNomeProduto(), "dsNomeProduto"));
        String status = validarStatus(required(item.dsStatus(), "dsStatus"));

        return new ServicoGovernancaProcessado(
                item.cdServicoGovernanca(),
                origem,
                nome,
                status,
                item.dtModificacao()
        );
    }

    private String required(String value, String field) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(field + " e obrigatorio");
        }
        return value.trim();
    }

    private String validarUuidCanonico(String origem) {
        String canonico;

        try {
            canonico = UUID.fromString(origem).toString();
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("cdServicoOrigem nao e um UUID valido: " + origem, ex);
        }
        if (!canonico.equals(origem)) {
            throw new IllegalArgumentException(
                    "cdServicoOrigem deve estar em formato canonico minusculo: " + origem);
        }
        return origem;
    }

    private String validarNome(String nome) {
        if (nome.length() > NOME_MAX_LENGTH) {
            throw new IllegalArgumentException(
                    "dsNomeProduto excede " + NOME_MAX_LENGTH + " caracteres: " + nome);
        }
        return nome;
    }

    private String validarStatus(String status) {
        String statusUpper = status.toUpperCase(Locale.ROOT);
        if (!statusUpper.equals(STATUS_ATIVO) && !statusUpper.equals(STATUS_INATIVO)) {
            throw new IllegalArgumentException(
                    "dsStatus invalido (esperado ATIVO ou INATIVO): " + status);
        }
        return statusUpper;
    }
}