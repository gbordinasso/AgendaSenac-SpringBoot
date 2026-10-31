package senac.agendasenac.security;

import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;
import senac.agendasenac.entity.Usuario;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;

// Cria o token que o usuário recebe ao fazer login
@Service
public class TokenService {

    // O token vale 24 horas
    private static final Duration VALIDADE = Duration.ofHours(24);

    private final JwtEncoder jwtEncoder;
    private final Clock clock;

    public TokenService(JwtEncoder jwtEncoder, Clock clock) {
        this.jwtEncoder = jwtEncoder;
        this.clock = clock;
    }

    public String gerar(Usuario usuario) {

        Instant agora = clock.instant();

        JwtClaimsSet dados = JwtClaimsSet.builder()
                .issuer("agenda-senac")
                .issuedAt(agora)
                .expiresAt(agora.plus(VALIDADE))
                .subject(String.valueOf(usuario.getId()))
                .claim("role", usuario.getTipoUsuario().name())
                .build();

        JwsHeader cabecalho = JwsHeader.with(MacAlgorithm.HS256).build();

        return jwtEncoder.encode(JwtEncoderParameters.from(cabecalho, dados)).getTokenValue();
    }
}
