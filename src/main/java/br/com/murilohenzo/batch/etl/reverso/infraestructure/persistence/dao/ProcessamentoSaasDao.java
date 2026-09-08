package br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.dao;

import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernancaProcessado;
import br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound.ProcessamentoSaasPort;
import br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.sql.SqlLoader;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ProcessamentoSaasDao implements ProcessamentoSaasPort {

    private final NamedParameterJdbcTemplate jdbcTemplate;
    private final SqlLoader sqlLoader;

    @Override
    public void registrarSucesso(ServicoGovernancaProcessado item) {
        jdbcTemplate.update(
                sqlLoader.load("sql/processamento-saas/registrar-sucesso.sql"),
                toParams(item)
        );
    }

    @Override
    public void registrarErro(ServicoGovernancaProcessado item) {
        jdbcTemplate.update(
                sqlLoader.load("sql/processamento-saas/registrar-erro.sql"),
                toParams(item)
        );
    }

    private MapSqlParameterSource toParams(
            ServicoGovernancaProcessado item
    ) {
        return new MapSqlParameterSource()
                .addValue(
                        "cdServicoOrigem", item.getCdServicoOrigem()
                )
                .addValue(
                        "dtModificacaoProcessada", item.getDtModificacao()
                );
    }
}