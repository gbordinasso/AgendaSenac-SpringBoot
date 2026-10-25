package senac.agendasenac.exception;

public record ErroResposta(
        String timestamp,
        int status,
        String erro,
        String mensagem,
        String caminho
) {
}
