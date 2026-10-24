package senac.agendasenac.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Clock;
import java.time.ZoneId;

@Configuration
public class ClockConfig {

    public static final ZoneId FUSO_HORARIO = ZoneId.of("America/Sao_Paulo");

    // Relógio único do sistema. Todos os services usam este bean,
    // então o fuso fica igual em tudo e dá para fixar a hora nos testes.
    @Bean
    public Clock clock() {
        return Clock.system(FUSO_HORARIO);
    }
}
