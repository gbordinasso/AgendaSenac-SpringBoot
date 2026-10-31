package senac.agendasenac.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import senac.agendasenac.exception.AcessoNegadoException;

// Diz quem está fazendo o pedido a partir do token, é daqui que vem o "dono" das coisas
@Component
public class UsuarioLogado {

    // O id do usuário fica guardado dentro do token
    public Long getId() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        return Long.valueOf(autenticacao.getName());
    }

    public boolean isAdmin() {
        Authentication autenticacao = SecurityContextHolder.getContext().getAuthentication();
        return autenticacao.getAuthorities().stream()
                .anyMatch(permissao -> permissao.getAuthority().equals("ROLE_ADMIN"));
    }

    // Passa o admin ou o próprio dono, qualquer outro leva 403
    public void exigirDonoOuAdmin(Long idDoDono) {
        if (!isAdmin() && !getId().equals(idDoDono)) {
            throw new AcessoNegadoException("Você não tem permissão para acessar este recurso");
        }
    }
}
