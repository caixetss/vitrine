package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.dto.AvaliacoesDoProdutoDto;
import io.github.caixetss.vitrine.service.AvaliacaoService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/produtos/{slug}/avaliacoes")
@RequiredArgsConstructor
public class AvaliacaoController {

	private final AvaliacaoService avaliacaoService;

	@GetMapping
	public AvaliacoesDoProdutoDto buscaListaAvaliacaoDoProduto(
			@PathVariable String slug,
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "não pode ser negativa") int pagina,
			@RequestParam(defaultValue = "5") @Min(value = 1, message = "deve ser entre 1 e 60") @Max(value = 60, message = "deve ser entre 1 e 60") int tamanho) {
		return avaliacaoService.buscaListaAvaliacaoDoProduto(slug, pagina, tamanho);
	}

}
