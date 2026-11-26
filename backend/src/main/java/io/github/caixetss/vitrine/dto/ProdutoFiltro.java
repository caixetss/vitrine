package io.github.caixetss.vitrine.dto;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoFiltro(
		String categoria,
		@Size(min = 2, message = "deve ter pelo menos 2 caracteres") String busca,
		@DecimalMin(value = "0", message = "não pode ser negativo") BigDecimal precoMin,
		@DecimalMin(value = "0", message = "não pode ser negativo") BigDecimal precoMax,
		@Min(value = 1, message = "deve ser entre 1 e 5") @Max(value = 5, message = "deve ser entre 1 e 5") Integer avaliacaoMin,
		List<String> marcas,
		Boolean freteGratis) {

	@AssertTrue(message = "precoMin deve ser menor ou igual a precoMax")
	public boolean isFaixaDePrecoValida() {
		return precoMin == null || precoMax == null || precoMin.compareTo(precoMax) <= 0;
	}

	public ProdutoFiltro semMarcas() {
		return new ProdutoFiltro(categoria, busca, precoMin, precoMax, avaliacaoMin, null, freteGratis);
	}

	public ProdutoFiltro semFaixaDePreco() {
		return new ProdutoFiltro(categoria, busca, null, null, avaliacaoMin, marcas, freteGratis);
	}

	public ProdutoFiltro semFreteGratis() {
		return new ProdutoFiltro(categoria, busca, precoMin, precoMax, avaliacaoMin, marcas, null);
	}

}
