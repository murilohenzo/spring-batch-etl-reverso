package br.com.murilohenzo.batch.etl.reverso.domain.ports.outbound;

public interface ServicoGovernancaConsultaPort {
    String selectClause();
    String fromClause();
    String whereClause();
}