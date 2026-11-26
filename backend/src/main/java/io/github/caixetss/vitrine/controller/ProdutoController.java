package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.dto.FiltrosDisponiveisDto;
import io.github.caixetss.vitrine.dto.OrdemProduto;
import io.github.caixetss.vitrine.dto.PaginaDto;
import io.github.caixetss.vitrine.dto.ProdutoDetalheDto;
import io.github.caixetss.vitrine.dto.ProdutoFiltro;
import io.github.caixetss.vitrine.dto.ProdutoResumoDto;
import io.github.caixetss.vitrine.service.ProdutoService;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

	private final ProdutoService produtoService;

	@GetMapping
	public PaginaDto<ProdutoResumoDto> buscaListaProduto(
			@Valid ProdutoFiltro filtro,
			@RequestParam(defaultValue = "relevancia") OrdemProduto ordem,
			@RequestParam(defaultValue = "0") @Min(0) int pagina,
			@RequestParam(defaultValue = "20") @Min(1) @Max(60) int tamanho) {
		return produtoService.buscaListaProduto(filtro, ordem, pagina, tamanho);
	}

	@GetMapping("/filtros")
	public FiltrosDisponiveisDto buscaFiltrosDisponiveis(@Valid ProdutoFiltro filtro) {
		return produtoService.buscaFiltrosDisponiveis(filtro);
	}

	@GetMapping("/{slug}")
	public ProdutoDetalheDto buscaIndividualProduto(@PathVariable String slug) {
		return produtoService.buscaIndividualProduto(slug);
	}

	@GetMapping("/{slug}/relacionados")
	public List<ProdutoResumoDto> buscaListaProdutoRelacionado(@PathVariable String slug) {
		return produtoService.buscaListaProdutoRelacionado(slug);
	}

}
