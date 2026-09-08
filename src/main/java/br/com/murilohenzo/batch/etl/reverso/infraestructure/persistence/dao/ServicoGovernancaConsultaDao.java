package br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.dao;

import br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound.ServicoGovernancaConsultaPort;
import br.com.murilohenzo.batch.etl.reverso.infraestructure.persistence.sql.SqlLoader;
import org.springframework.stereotype.Component;

@Component
public class ServicoGovernancaConsultaDao
        implements ServicoGovernancaConsultaPort {

    private static final String SELECT_SQL =
            "sql/servico-governanca/consulta/select.sql";

    private static final String FROM_SQL =
            "sql/servico-governanca/consulta/from.sql";

    private static final String WHERE_SQL =
            "sql/servico-governanca/consulta/where.sql";

    private final SqlLoader sqlLoader;

    public ServicoGovernancaConsultaDao(SqlLoader sqlLoader) {
        this.sqlLoader = sqlLoader;
    }

    @Override
    public String selectClause() {
        return sqlLoader.load(SELECT_SQL);
    }

    @Override
    public String fromClause() {
        return sqlLoader.load(FROM_SQL);
    }

    @Override
    public String whereClause() {
        return sqlLoader.load(WHERE_SQL);
    }
}