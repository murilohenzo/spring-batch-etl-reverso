package br.com.murilohenzo.batch.etl.reverso.validator;

import br.com.murilohenzo.batch.etl.reverso.config.JobParameterNames;
import org.springframework.batch.core.job.parameters.JobParameters;
import org.springframework.batch.core.job.parameters.JobParametersValidator;
import org.springframework.stereotype.Component;

@Component
public class EtlReversoJobParametersValidator
        implements JobParametersValidator {

    @Override
    public void validate(JobParameters parameters)
            throws JobParametersInvalidException {

        String cargaId =
                parameters.getString(JobParameterNames.CARGA);

        if (cargaId == null || cargaId.isBlank()) {
            throw new JobParametersInvalidException(
                    "Parâmetro obrigatório '%s' não informado"
                            .formatted(JobParameterNames.CARGA)
            );
        }
    }
}