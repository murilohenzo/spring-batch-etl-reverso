package br.com.murilohenzo.batch.etl.reverso.validator;

public class JobParametersInvalidException extends RuntimeException {

    public JobParametersInvalidException() {
    }

    public JobParametersInvalidException(String message) {
        super(message);
    }
}
