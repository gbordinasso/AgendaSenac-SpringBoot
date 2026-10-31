package senac.agendasenac.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

// Responde 401 sem token ou token inválido e 403 sem permissão no mesmo formato dos erros
@Component
public class ErroSegurancaHandler implements AuthenticationEntryPoint, AccessDeniedHandler {

    private final Clock clock;

    public ErroSegurancaHandler(Clock clock) {
        this.clock = clock;
    }

    // Quem não está logado sem token, token inválido ou vencido
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException e) throws IOException {

        escrever(request, response, HttpStatus.UNAUTHORIZED, "Não autorizado",
                "Token ausente, inválido ou expirado");
    }

    // Não pode acessar esta rota
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException e) throws IOException {

        escrever(request, response, HttpStatus.FORBIDDEN, "Acesso negado",
                "Você não tem permissão para acessar este recurso");
    }

    private void escrever(HttpServletRequest request, HttpServletResponse response,
                          HttpStatus status, String erro, String mensagem) throws IOException {

        String agora = OffsetDateTime.now(clock)
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String json = "{"
                + "\"timestamp\":\"" + agora + "\","
                + "\"status\":" + status.value() + ","
                + "\"erro\":\"" + escapar(erro) + "\","
                + "\"mensagem\":\"" + escapar(mensagem) + "\","
                + "\"caminho\":\"" + escapar(request.getRequestURI()) + "\""
                + "}";

        response.setStatus(status.value());
        response.setContentType("application/json");
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.getWriter().write(json);
    }

    private String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
