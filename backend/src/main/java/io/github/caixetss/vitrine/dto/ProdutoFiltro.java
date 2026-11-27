package io.github.caixetss.vitrine.dto;

import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.List;

public record ProdutoFiltro(
		@Schema(description = "Slug da categoria; inclui os produtos das subcategorias", example = "audio")
		String categoria,

		@Schema(description = "Parte do nome do produto; ignora maiúsculas e acentos", example = "fone")
		@Size(min = 2, message = "deve ter pelo menos 2 caracteres") String busca,

		@Schema(description = "Preço mínimo", example = "100")
		@DecimalMin(value = "0", message = "não pode ser negativo") BigDecimal precoMin,

		@Schema(description = "Preço máximo", example = "500")
		@DecimalMin(value = "0", message = "não pode ser negativo") BigDecimal precoMax,

		@Schema(description = "Nota média mínima, de 1 a 5", example = "4")
		@Min(value = 1, message = "deve ser entre 1 e 5") @Max(value = 5, message = "deve ser entre 1 e 5") Integer avaliacaoMin,

		@ArraySchema(
				arraySchema = @Schema(description = "Slugs das marcas; produtos de qualquer uma delas", example = "[\"sonora\", \"vibe\"]"),
				schema = @Schema(example = "sonora"))
		List<String> marcas,

		@Schema(description = "true para trazer só produtos com frete grátis", example = "true")
		Boolean freteGratis) {

	@Schema(hidden = true)
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
