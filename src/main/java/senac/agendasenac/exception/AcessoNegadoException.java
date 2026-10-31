package senac.agendasenac.exception;

// Quando o usuário está logado mas tenta mexer em algo que não é dele (403)
public class AcessoNegadoException extends RuntimeException {

    public AcessoNegadoException(String mensagem) {
        super(mensagem);
    }
}
