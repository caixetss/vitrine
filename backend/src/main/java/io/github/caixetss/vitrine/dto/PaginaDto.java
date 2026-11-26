package io.github.caixetss.vitrine.dto;

import org.springframework.data.domain.Page;

import java.util.List;

public record PaginaDto<T>(List<T> itens, int pagina, int tamanho, long totalItens, int totalPaginas) {

	public PaginaDto(Page<T> pagina) {
		this(pagina.getContent(), pagina.getNumber(), pagina.getSize(), pagina.getTotalElements(), pagina.getTotalPages());
	}

}
