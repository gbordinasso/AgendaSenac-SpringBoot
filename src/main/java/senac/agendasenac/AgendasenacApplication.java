package senac.agendasenac;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AgendasenacApplication {

	public static void main(String[] args) {
		SpringApplication.run(AgendasenacApplication.class, args);
	}
}
