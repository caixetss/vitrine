package io.github.caixetss.vitrine.excecao;

import io.github.caixetss.vitrine.dto.CampoErroDto;
import io.github.caixetss.vitrine.dto.ErroDto;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.TypeMismatchException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.method.ParameterErrors;
import org.springframework.validation.method.ParameterValidationResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Stream;

@Slf4j
@RestControllerAdvice
public class TratadorGlobalExcecoes extends ResponseEntityExceptionHandler {

	private static final String MENSAGEM_PARAMETROS_INVALIDOS = "Parâmetros inválidos";
	private static final String MENSAGEM_VALOR_INVALIDO = "valor inválido";

	@ExceptionHandler(RecursoNaoEncontradoException.class)
	public ResponseEntity<ErroDto> trataRecursoNaoEncontrado(RecursoNaoEncontradoException excecao) {
		return montaResposta(new ErroDto(HttpStatus.NOT_FOUND, excecao.getMessage()));
	}

	@ExceptionHandler(RegraNegocioException.class)
	public ResponseEntity<ErroDto> trataRegraNegocio(RegraNegocioException excecao) {
		return montaResposta(new ErroDto(HttpStatus.UNPROCESSABLE_CONTENT, excecao.getMessage()));
	}

	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErroDto> trataErroInesperado(Exception excecao) {
		log.error("Erro inesperado", excecao);
		return montaResposta(new ErroDto(HttpStatus.INTERNAL_SERVER_ERROR, "Ocorreu um erro inesperado. Tente novamente mais tarde."));
	}

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(
			MethodArgumentNotValidException excecao, HttpHeaders cabecalhos, HttpStatusCode status, WebRequest requisicao) {
		List<CampoErroDto> campos = excecao.getBindingResult().getFieldErrors().stream()
				.map(erro -> new CampoErroDto(erro.getField(), escolheMensagemDoCampo(erro)))
				.toList();
		return new ResponseEntity<>(new ErroDto(status, MENSAGEM_PARAMETROS_INVALIDOS, campos), cabecalhos, status);
	}

	@Override
	protected ResponseEntity<Object> handleHandlerMethodValidationException(
			HandlerMethodValidationException excecao, HttpHeaders cabecalhos, HttpStatusCode status, WebRequest requisicao) {
		List<CampoErroDto> campos = excecao.getParameterValidationResults().stream()
				.flatMap(this::listaCamposComErro)
				.toList();
		return new ResponseEntity<>(new ErroDto(status, MENSAGEM_PARAMETROS_INVALIDOS, campos), cabecalhos, status);
	}

	@Override
	protected ResponseEntity<Object> handleTypeMismatch(
			TypeMismatchException excecao, HttpHeaders cabecalhos, HttpStatusCode status, WebRequest requisicao) {
		List<CampoErroDto> campos = List.of(new CampoErroDto(excecao.getPropertyName(), MENSAGEM_VALOR_INVALIDO));
		return new ResponseEntity<>(new ErroDto(status, MENSAGEM_PARAMETROS_INVALIDOS, campos), cabecalhos, status);
	}

	@Override
	protected ResponseEntity<Object> handleExceptionInternal(
			Exception excecao, Object corpo, HttpHeaders cabecalhos, HttpStatusCode status, WebRequest requisicao) {
		return new ResponseEntity<>(new ErroDto(status, "Não foi possível atender a requisição"), cabecalhos, status);
	}

	private ResponseEntity<ErroDto> montaResposta(ErroDto erro) {
		return ResponseEntity.status(erro.status()).body(erro);
	}

	private Stream<CampoErroDto> listaCamposComErro(ParameterValidationResult resultadoDaValidacao) {
		if (resultadoDaValidacao instanceof ParameterErrors errosDoObjeto) {
			return errosDoObjeto.getFieldErrors().stream()
					.map(erro -> new CampoErroDto(erro.getField(), escolheMensagemDoCampo(erro)));
		}
		String nomeDoParametro = resultadoDaValidacao.getMethodParameter().getParameterName();
		return resultadoDaValidacao.getResolvableErrors().stream()
				.map(erro -> new CampoErroDto(nomeDoParametro, erro.getDefaultMessage()));
	}

	private String escolheMensagemDoCampo(FieldError erro) {
		return erro.isBindingFailure() ? MENSAGEM_VALOR_INVALIDO : erro.getDefaultMessage();
	}

}
