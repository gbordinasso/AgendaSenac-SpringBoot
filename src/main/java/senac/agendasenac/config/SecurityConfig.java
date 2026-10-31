package senac.agendasenac.config;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import senac.agendasenac.security.ErroSegurancaHandler;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;

@Configuration
public class SecurityConfig {

    // Chave secreta que assina os tokens
    // O valor padrão abaixo serve só para desenvolvimento (precisa ter pelo menos 32 caracteres).
    @Value("${jwt.secret:agenda-senac-chave-de-desenvolvimento-trocar-em-producao}")
    private String segredo;

    @Bean
    public SecurityFilterChain filtros(HttpSecurity http, ErroSegurancaHandler erroHandler) throws Exception {

        http.csrf(csrf -> csrf.disable())
                .sessionManagement(sessao -> sessao.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .cors(Customizer.withDefaults())
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
                .authorizeHttpRequests(regras -> regras

                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/h2-console/**").permitAll()

                        .requestMatchers(HttpMethod.POST, "/auth/login", "/usuarios/comum").permitAll()

                        .requestMatchers("/usuarios/admin/**").hasRole("ADMIN")
                        .requestMatchers("/participantes/**", "/reservas-participantes/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/usuarios/comum").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/reservas/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.GET, "/quadras/**", "/horarios/**").authenticated()
                        .requestMatchers("/quadras/**", "/horarios/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/checkins").authenticated()
                        .requestMatchers("/checkins/**").hasRole("ADMIN")

                        .anyRequest().authenticated())

                .oauth2ResourceServer(token -> token
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(conversor()))
                        .authenticationEntryPoint(erroHandler)
                        .accessDeniedHandler(erroHandler))

                .exceptionHandling(erros -> erros
                        .authenticationEntryPoint(erroHandler)
                        .accessDeniedHandler(erroHandler));

        return http.build();
    }

    private JwtAuthenticationConverter conversor() {
        JwtGrantedAuthoritiesConverter permissoes = new JwtGrantedAuthoritiesConverter();
        permissoes.setAuthoritiesClaimName("role");
        permissoes.setAuthorityPrefix("ROLE_");

        JwtAuthenticationConverter conversor = new JwtAuthenticationConverter();
        conversor.setJwtGrantedAuthoritiesConverter(permissoes);
        return conversor;
    }

    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(chave()));
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        return NimbusJwtDecoder.withSecretKey(chave()).macAlgorithm(MacAlgorithm.HS256).build();
    }

    private SecretKey chave() {
        return new SecretKeySpec(segredo.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
    }
}
