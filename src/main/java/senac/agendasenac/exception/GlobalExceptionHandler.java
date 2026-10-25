package senac.agendasenac.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Clock;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.TreeMap;
import java.util.stream.Collectors;

// Recebe os erros que saem de qualquer controller e cada tipo de erro na resposta HTTP certa, sempre no mesmo formato
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private final Clock clock;

    public GlobalExceptionHandler(Clock clock) {
        this.clock = clock;
    }

    @ExceptionHandler(RecursoNaoEncontradoException.class)
    public ResponseEntity<ErroResposta> naoEncontrado(
            RecursoNaoEncontradoException e, HttpServletRequest request) {

        return montar(HttpStatus.NOT_FOUND, "Não encontrado", e.getMessage(), request);
    }

    @ExceptionHandler(ConflitoException.class)
    public ResponseEntity<ErroResposta> conflito(
            ConflitoException e, HttpServletRequest request) {

        return montar(HttpStatus.CONFLICT, "Conflito", e.getMessage(), request);
    }

    @ExceptionHandler(RequisicaoInvalidaException.class)
    public ResponseEntity<ErroResposta> requisicaoInvalida(
            RequisicaoInvalidaException e, HttpServletRequest request) {

        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida", e.getMessage(), request);
    }


    // JSON quebrado, corpo vazio ou valor de tipo errado
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErroResposta> corpoInvalido(
            HttpMessageNotReadableException e, HttpServletRequest request) {

        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O corpo da requisição está vazio, mal formado ou com valores inválidos", request);
    }

    // Campo obrigatório vazio ou inválido no corpo (Bean Validation, via @Valid), junta todos os campos com problema em uma mensagem só
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErroResposta> validacao(
            MethodArgumentNotValidException e, HttpServletRequest request) {

        // Um campo por vez, em ordem alfabética, Se o mesmo campo quebrou duas regras (ex.: CPF vazio), aparece só a primeira mensagem.
        String mensagem = e.getBindingResult().getFieldErrors().stream()
                .collect(Collectors.toMap(
                        FieldError::getField,
                        erro -> String.valueOf(erro.getDefaultMessage()),
                        (primeira, segunda) -> primeira,
                        TreeMap::new))
                .entrySet().stream()
                .map(campo -> campo.getKey() + ": " + campo.getValue())
                .collect(Collectors.joining("; "));

        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida", mensagem, request);
    }

    // Parâmetro da URL com tipo errado
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErroResposta> parametroInvalido(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {

        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O valor informado para '" + e.getName() + "' é inválido", request);
    }

    // Parâmetro obrigatório da URL que não foi enviado
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ErroResposta> parametroAusente(
            MissingServletRequestParameterException e, HttpServletRequest request) {

        return montar(HttpStatus.BAD_REQUEST, "Requisição inválida",
                "O parâmetro '" + e.getParameterName() + "' é obrigatório", request);
    }


    // A rota não existe
    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErroResposta> rotaNaoEncontrada(
            NoResourceFoundException e, HttpServletRequest request) {

        return montar(HttpStatus.NOT_FOUND, "Não encontrado", "Rota não encontrada", request);
    }

    // A rota existe, mas não aceita esse metodo (POST em uma rota só de consulta)
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ErroResposta> metodoNaoPermitido(
            HttpRequestMethodNotSupportedException e, HttpServletRequest request) {

        return montar(HttpStatus.METHOD_NOT_ALLOWED, "Método não permitido",
                "O método " + e.getMethod() + " não é permitido para esta rota", request);
    }

    // O corpo foi enviado sem ser JSON
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ErroResposta> tipoNaoSuportado(
            HttpMediaTypeNotSupportedException e, HttpServletRequest request) {

        return montar(HttpStatus.UNSUPPORTED_MEDIA_TYPE, "Tipo de conteúdo não suportado",
                "Envie o corpo da requisição como application/json", request);
    }


    // Última defesa do banco (chave única, chave estrangeira, campo obrigatório)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErroResposta> integridade(
            DataIntegrityViolationException e, HttpServletRequest request) {

        log.warn("Violação de integridade em {}: {}", request.getRequestURI(), e.getMostSpecificCause().getMessage());

        return montar(HttpStatus.CONFLICT, "Conflito",
                "A operação não pôde ser concluída porque viola uma regra de integridade dos dados", request);
    }


    // O cliente recebe uma mensagem curta, o erro completo fica só no console
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErroResposta> erroInesperado(
            Exception e, HttpServletRequest request) {

        log.error("Erro inesperado em {} {}", request.getMethod(), request.getRequestURI(), e);

        return montar(HttpStatus.INTERNAL_SERVER_ERROR, "Erro interno",
                "Ocorreu um erro inesperado. Tente novamente mais tarde", request);
    }

    private ResponseEntity<ErroResposta> montar(
            HttpStatus status, String erro, String mensagem, HttpServletRequest request) {

        String agora = OffsetDateTime.now(clock)
                .truncatedTo(ChronoUnit.SECONDS)
                .format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        ErroResposta corpo = new ErroResposta(
                agora,
                status.value(),
                erro,
                mensagem,
                request.getRequestURI()
        );

        return ResponseEntity.status(status).body(corpo);
    }
}
