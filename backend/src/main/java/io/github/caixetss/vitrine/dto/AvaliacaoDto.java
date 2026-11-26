package io.github.caixetss.vitrine.dto;

import io.github.caixetss.vitrine.entidade.Avaliacao;

import java.time.Instant;

public record AvaliacaoDto(
		String nomeAutor,
		Integer nota,
		String titulo,
		String comentario,
		Boolean ehCompraVerificada,
		Instant criadoEm) {

	public AvaliacaoDto(Avaliacao avaliacao) {
		this(avaliacao.getNomeAutor(), avaliacao.getNota(), avaliacao.getTitulo(), avaliacao.getComentario(),
				avaliacao.getEhCompraVerificada(), avaliacao.getCriadoEm());
	}

}
