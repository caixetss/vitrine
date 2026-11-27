package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.dto.AvaliacoesDoProdutoDto;
import io.github.caixetss.vitrine.service.AvaliacaoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "Avaliações", description = "Avaliações de clientes na página do produto")
@RestController
@RequestMapping("/api/produtos/{slug}/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

	private final AvaliacaoService avaliacaoService;

	@Operation(
			summary = "Lista as avaliações do produto",
			description = "Devolve o resumo (média, total e quantidade por nota de 1 a 5) e as avaliações paginadas, das mais recentes para as mais antigas.")
	@GetMapping
	public AvaliacoesDoProdutoDto buscaListaAvaliacaoDoProduto(
			@Parameter(description = "Identificador do produto na URL", example = "kit-halteres-emborrachados-2-x-5-kg")
			@PathVariable String slug,
			@Parameter(description = "Número da página, começando em 0", example = "0")
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "não pode ser negativa") int pagina,
			@Parameter(description = "Avaliações por página, de 1 a 60", example = "5")
			@RequestParam(defaultValue = "5") @Min(value = 1, message = "deve ser entre 1 e 60") @Max(value = 60, message = "deve ser entre 1 e 60") int tamanho) {
		return avaliacaoService.buscaListaAvaliacaoDoProduto(slug, pagina, tamanho);
	}

}
