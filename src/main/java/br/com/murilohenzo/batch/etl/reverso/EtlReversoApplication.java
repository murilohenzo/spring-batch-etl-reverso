package br.com.murilohenzo.batch.etl.reverso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class EtlReversoApplication {

	public static void main(String[] args) {
		System.exit(SpringApplication.exit(SpringApplication.run(EtlReversoApplication.class, args)));
	}
}
