package br.com.murilohenzo.batch.etl.reverso.batch.validator;

public class JobParametersInvalidException extends RuntimeException {

    public JobParametersInvalidException() {
    }

    public JobParametersInvalidException(String message) {
        super(message);
    }
}
