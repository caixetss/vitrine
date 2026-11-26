package io.github.caixetss.vitrine.dto;

import java.math.BigDecimal;
import java.util.List;

public record FiltrosDisponiveisDto(
		List<OpcaoFiltroDto> subcategorias,
		List<OpcaoFiltroDto> marcas,
		BigDecimal precoMinimo,
		BigDecimal precoMaximo,
		long totalFreteGratis) {
}
