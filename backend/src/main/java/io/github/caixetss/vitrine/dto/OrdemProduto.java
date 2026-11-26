package io.github.caixetss.vitrine.dto;

import java.util.Arrays;

public enum OrdemProduto {

	RELEVANCIA("relevancia"),
	MENOR_PRECO("menor-preco"),
	MAIOR_PRECO("maior-preco"),
	AVALIACAO("avaliacao"),
	MAIS_VENDIDOS("mais-vendidos"),
	MAIOR_DESCONTO("maior-desconto");

	private final String valorNaUrl;

	OrdemProduto(String valorNaUrl) {
		this.valorNaUrl = valorNaUrl;
	}

	public static OrdemProduto buscaPorValorNaUrl(String valorNaUrl) {
		return Arrays.stream(values())
				.filter(ordem -> ordem.valorNaUrl.equals(valorNaUrl))
				.findFirst()
				.orElseThrow(() -> new IllegalArgumentException("Ordenação desconhecida: " + valorNaUrl));
	}

}
