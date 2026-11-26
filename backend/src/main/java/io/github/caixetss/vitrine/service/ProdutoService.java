package io.github.caixetss.vitrine.service;

import io.github.caixetss.vitrine.dto.FiltrosDisponiveisDto;
import io.github.caixetss.vitrine.dto.OrdemProduto;
import io.github.caixetss.vitrine.dto.PaginaDto;
import io.github.caixetss.vitrine.dto.ProdutoDetalheDto;
import io.github.caixetss.vitrine.dto.ProdutoFiltro;
import io.github.caixetss.vitrine.dto.ProdutoResumoDto;
import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.entidade.ProdutoImagem;
import io.github.caixetss.vitrine.repository.ContagemFiltrosRepository.FaixaDePreco;
import io.github.caixetss.vitrine.repository.ContagemFiltrosRepository;
import io.github.caixetss.vitrine.excecao.RecursoNaoEncontradoException;
import io.github.caixetss.vitrine.repository.ProdutoEspecificacaoRepository;
import io.github.caixetss.vitrine.repository.ProdutoImagemRepository;
import io.github.caixetss.vitrine.repository.ProdutoRepository;
import io.github.caixetss.vitrine.repository.ProdutoSpecifications;
import io.github.caixetss.vitrine.repository.ProdutoVariacaoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Limit;
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

	private static final int LIMITE_DE_PRODUTOS_RELACIONADOS = 5;

	private final ProdutoRepository produtoRepository;
	private final ProdutoImagemRepository produtoImagemRepository;
	private final ProdutoVariacaoRepository produtoVariacaoRepository;
	private final ProdutoEspecificacaoRepository produtoEspecificacaoRepository;
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

	@Transactional(readOnly = true)
	public ProdutoDetalheDto buscaIndividualProduto(String slug) {
		Produto produto = buscaProdutoAtivoPeloSlug(slug);
		return new ProdutoDetalheDto(
				produto,
				produtoImagemRepository.buscaListaImagemDoProduto(produto.getId()),
				produtoVariacaoRepository.buscaListaVariacaoAtivaDoProduto(produto.getId()),
				produtoEspecificacaoRepository.buscaListaEspecificacaoDoProduto(produto.getId()));
	}

	@Transactional(readOnly = true)
	public List<ProdutoResumoDto> buscaListaProdutoRelacionado(String slug) {
		Produto produtoAtual = buscaProdutoAtivoPeloSlug(slug);
		List<Produto> relacionados = produtoRepository.buscaListaProdutoDaMesmaCategoria(
				produtoAtual.getCategoria(), produtoAtual.getId(), Limit.of(LIMITE_DE_PRODUTOS_RELACIONADOS));

		Map<UUID, String> urlDaCapaDeCadaProduto = buscaUrlDaCapaDeCadaProduto(relacionados);

		return relacionados.stream()
				.map(produto -> new ProdutoResumoDto(produto, urlDaCapaDeCadaProduto.get(produto.getId())))
				.toList();
	}

	public Produto buscaProdutoAtivoPeloSlug(String slug) {
		return produtoRepository.buscaIndividualProdutoAtivo(slug)
				.orElseThrow(() -> new RecursoNaoEncontradoException("Produto não encontrado: " + slug));
	}

	private Map<UUID, String> buscaUrlDaCapaDeCadaProduto(List<Produto> produtos) {
		List<UUID> idsProduto = produtos.stream().map(Produto::getId).toList();
		return produtoImagemRepository.buscaListaCapaDosProdutos(idsProduto).stream()
				.collect(Collectors.toMap(capa -> capa.getProduto().getId(), ProdutoImagem::getUrl));
	}

}
