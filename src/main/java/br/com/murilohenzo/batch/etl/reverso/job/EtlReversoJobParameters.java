package br.com.murilohenzo.batch.etl.reverso.job;

public class EtlReversoJobParameters {

    private final String carga;

    public EtlReversoJobParameters(String carga) {
        this.carga = carga;
    }

    public String cargaId() {
        return carga;
    }
}