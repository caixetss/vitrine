package io.github.caixetss.vitrine.dto;

import io.github.caixetss.vitrine.entidade.Categoria;

import java.util.List;
import java.util.UUID;

public record CategoriaDto(UUID id, String nome, String slug, String icone, List<CategoriaDto> subcategorias) {

	public CategoriaDto(Categoria categoria, List<CategoriaDto> subcategorias) {
		this(categoria.getId(), categoria.getNome(), categoria.getSlug(), categoria.getIcone(), subcategorias);
	}

}
