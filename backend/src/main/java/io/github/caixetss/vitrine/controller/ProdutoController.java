package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.dto.FiltrosDisponiveisDto;
import io.github.caixetss.vitrine.dto.OrdemProduto;
import io.github.caixetss.vitrine.dto.PaginaDto;
import io.github.caixetss.vitrine.dto.ProdutoDetalheDto;
import io.github.caixetss.vitrine.dto.ProdutoFiltro;
import io.github.caixetss.vitrine.dto.ProdutoResumoDto;
import io.github.caixetss.vitrine.service.ProdutoService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Tag(name = "Produtos", description = "Listagem com filtros, barra de filtros, página do produto e relacionados")
@RestController
@RequestMapping("/api/produtos")
@RequiredArgsConstructor
public class ProdutoController {

	private final ProdutoService produtoService;

	@Operation(
			summary = "Lista produtos com filtros, ordenação e paginação",
			description = "Usado pela página de categoria, pela busca e pelas vitrines da home. Só devolve produtos ativos; filtros combinam com E e várias marcas combinam com OU.")
	@GetMapping
	public PaginaDto<ProdutoResumoDto> buscaListaProduto(
			@ParameterObject @Valid ProdutoFiltro filtro,
			@Parameter(
					description = "Ordenação da lista",
					schema = @Schema(type = "string", defaultValue = "relevancia",
							allowableValues = {"relevancia", "menor-preco", "maior-preco", "avaliacao", "mais-vendidos", "maior-desconto"}))
			@RequestParam(defaultValue = "relevancia") OrdemProduto ordem,
			@Parameter(description = "Número da página, começando em 0", example = "0")
			@RequestParam(defaultValue = "0") @Min(value = 0, message = "não pode ser negativa") int pagina,
			@Parameter(description = "Itens por página, de 1 a 60", example = "20")
			@RequestParam(defaultValue = "20") @Min(value = 1, message = "deve ser entre 1 e 60") @Max(value = 60, message = "deve ser entre 1 e 60") int tamanho) {
		return produtoService.buscaListaProduto(filtro, ordem, pagina, tamanho);
	}

	@Operation(
			summary = "Conta as opções da barra de filtros",
			description = "Recebe os mesmos filtros da listagem e devolve quantos produtos há por subcategoria e por marca, a faixa de preço e o total com frete grátis. Cada grupo ignora o próprio filtro.")
	@GetMapping("/filtros")
	public FiltrosDisponiveisDto buscaFiltrosDisponiveis(@ParameterObject @Valid ProdutoFiltro filtro) {
		return produtoService.buscaFiltrosDisponiveis(filtro);
	}

	@Operation(
			summary = "Busca a página do produto",
			description = "Devolve marca, categoria com a categoria pai, imagens, variações ativas e especificações.")
	@GetMapping("/{slug}")
	public ProdutoDetalheDto buscaIndividualProduto(
			@Parameter(description = "Identificador do produto na URL", example = "kit-halteres-emborrachados-2-x-5-kg")
			@PathVariable String slug) {
		return produtoService.buscaIndividualProduto(slug);
	}

	@Operation(
			summary = "Lista produtos relacionados",
			description = "Até 5 produtos ativos da mesma categoria, sem o próprio produto, dos mais vendidos para os menos vendidos.")
	@GetMapping("/{slug}/relacionados")
	public List<ProdutoResumoDto> buscaListaProdutoRelacionado(
			@Parameter(description = "Identificador do produto na URL", example = "kit-halteres-emborrachados-2-x-5-kg")
			@PathVariable String slug) {
		return produtoService.buscaListaProdutoRelacionado(slug);
	}

}
