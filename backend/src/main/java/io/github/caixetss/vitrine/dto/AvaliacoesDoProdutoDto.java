package io.github.caixetss.vitrine.dto;

import java.math.BigDecimal;
import java.util.Map;

public record AvaliacoesDoProdutoDto(ResumoAvaliacoesDto resumo, PaginaDto<AvaliacaoDto> avaliacoes) {

	public record ResumoAvaliacoesDto(BigDecimal media, Integer total, Map<Integer, Long> distribuicao) {
	}

}
