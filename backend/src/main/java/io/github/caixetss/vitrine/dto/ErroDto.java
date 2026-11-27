package io.github.caixetss.vitrine.dto;

import org.springframework.http.HttpStatusCode;

import java.util.List;

public record ErroDto(int status, String erro, String mensagem, List<CampoErroDto> campos) {

	public ErroDto(HttpStatusCode status, String mensagem, List<CampoErroDto> campos) {
		this(status.value(), descreveStatus(status.value()), mensagem, campos);
	}

	public ErroDto(HttpStatusCode status, String mensagem) {
		this(status, mensagem, List.of());
	}

	private static String descreveStatus(int status) {
		return switch (status) {
			case 400 -> "Requisição inválida";
			case 404 -> "Não encontrado";
			case 405 -> "Método não permitido";
			case 422 -> "Regra de negócio violada";
			case 500 -> "Erro interno";
			default -> "Erro";
		};
	}

}
