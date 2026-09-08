package br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.mapper;

import br.com.murilohenzo.batch.etl.reverso.domain.model.ServicoGovernanca;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

@Component
public class ServicoGovernancaRowMapper
        implements RowMapper<ServicoGovernanca> {

    @Override
    public ServicoGovernanca mapRow(
            ResultSet resultSet,
            int rowNum
    ) throws SQLException {

        return new ServicoGovernanca(
                resultSet.getLong("cdServicoGovernanca"),
                resultSet.getString("cdServicoOrigem"),
                resultSet.getString("dsNomeProduto"),
                resultSet.getString("dsStatus"),
                resultSet.getObject(
                        "dtModificacao",
                        LocalDateTime.class
                )
        );
    }
}