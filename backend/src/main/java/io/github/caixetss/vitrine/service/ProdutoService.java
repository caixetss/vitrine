package io.github.caixetss.vitrine.service;

import io.github.caixetss.vitrine.dto.FiltrosDisponiveisDto;
import io.github.caixetss.vitrine.dto.OrdemProduto;
import io.github.caixetss.vitrine.dto.PaginaDto;
import io.github.caixetss.vitrine.dto.ProdutoFiltro;
import io.github.caixetss.vitrine.dto.ProdutoResumoDto;
import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.entidade.ProdutoImagem;
import io.github.caixetss.vitrine.repository.ContagemFiltrosRepository.FaixaDePreco;
import io.github.caixetss.vitrine.repository.ContagemFiltrosRepository;
import io.github.caixetss.vitrine.repository.ProdutoImagemRepository;
import io.github.caixetss.vitrine.repository.ProdutoRepository;
import io.github.caixetss.vitrine.repository.ProdutoSpecifications;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProdutoService {

	private final ProdutoRepository produtoRepository;
	private final ProdutoImagemRepository produtoImagemRepository;
	private final ContagemFiltrosRepository contagemFiltrosRepository;

	@Transactional(readOnly = true)
	public PaginaDto<ProdutoResumoDto> buscaListaProduto(ProdutoFiltro filtro, OrdemProduto ordem, int pagina, int tamanho) {
		Page<Produto> produtos = produtoRepository.findAll(
				ProdutoSpecifications.combinaFiltrosEscolhidos(filtro).and(ProdutoSpecifications.ordenaPor(ordem)),
				PageRequest.of(pagina, tamanho));

		Map<UUID, String> urlDaCapaDeCadaProduto = buscaUrlDaCapaDeCadaProduto(produtos.getContent());

		return new PaginaDto<>(produtos.map(produto -> new ProdutoResumoDto(produto, urlDaCapaDeCadaProduto.get(produto.getId()))));
	}

	@Transactional(readOnly = true)
	public FiltrosDisponiveisDto buscaFiltrosDisponiveis(ProdutoFiltro filtro) {
		FaixaDePreco faixaDePreco = contagemFiltrosRepository.buscaMenorEMaiorPreco(ProdutoSpecifications.combinaFiltrosEscolhidos(filtro.semFaixaDePreco()));
		long totalFreteGratis = contagemFiltrosRepository.contaProdutosComFreteGratis(
				ProdutoSpecifications.combinaFiltrosEscolhidos(filtro.semFreteGratis()));

		return new FiltrosDisponiveisDto(
				contagemFiltrosRepository.contaProdutosPorSubcategoria(ProdutoSpecifications.combinaFiltrosEscolhidos(filtro)),
				contagemFiltrosRepository.contaProdutosPorMarca(ProdutoSpecifications.combinaFiltrosEscolhidos(filtro.semMarcas())),
				faixaDePreco.minimo(),
				faixaDePreco.maximo(),
				totalFreteGratis);
	}

	private Map<UUID, String> buscaUrlDaCapaDeCadaProduto(List<Produto> produtos) {
		List<UUID> idsProduto = produtos.stream().map(Produto::getId).toList();
		return produtoImagemRepository.buscaListaCapaDosProdutos(idsProduto).stream()
				.collect(Collectors.toMap(capa -> capa.getProduto().getId(), ProdutoImagem::getUrl));
	}

}
