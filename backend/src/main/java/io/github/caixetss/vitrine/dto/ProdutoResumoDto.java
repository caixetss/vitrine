package io.github.caixetss.vitrine.dto;

import io.github.caixetss.vitrine.entidade.Produto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProdutoResumoDto(
		UUID id,
		String slug,
		String nome,
		String imagemCapa,
		BigDecimal preco,
		BigDecimal precoOriginal,
		BigDecimal avaliacaoMedia,
		Integer totalAvaliacoes,
		Boolean ehFreteGratis) {

	public ProdutoResumoDto(Produto produto, String imagemCapa) {
		this(produto.getId(), produto.getSlug(), produto.getNome(), imagemCapa, produto.getPreco(),
				produto.getPrecoOriginal(), produto.getAvaliacaoMedia(), produto.getTotalAvaliacoes(),
				produto.getEhFreteGratis());
	}

}
