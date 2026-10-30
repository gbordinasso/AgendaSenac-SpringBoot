package senac.agendasenac.exception;

// Usada quando o login falha, vira 401
public class NaoAutorizadoException extends RuntimeException {

    public NaoAutorizadoException(String mensagem) {
        super(mensagem);
    }
}
