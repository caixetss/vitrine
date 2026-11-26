package io.github.caixetss.vitrine.service;

import io.github.caixetss.vitrine.dto.AvaliacaoDto;
import io.github.caixetss.vitrine.dto.AvaliacoesDoProdutoDto;
import io.github.caixetss.vitrine.dto.AvaliacoesDoProdutoDto.ResumoAvaliacoesDto;
import io.github.caixetss.vitrine.dto.PaginaDto;
import io.github.caixetss.vitrine.dto.QuantidadePorNotaDto;
import io.github.caixetss.vitrine.entidade.Avaliacao;
import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.repository.AvaliacaoRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

	private final AvaliacaoRepository avaliacaoRepository;
	private final ProdutoService produtoService;

	@Transactional(readOnly = true)
	public AvaliacoesDoProdutoDto buscaListaAvaliacaoDoProduto(String slugProduto, int pagina, int tamanho) {
		Produto produto = produtoService.buscaProdutoAtivoPeloSlug(slugProduto);

		ResumoAvaliacoesDto resumo = new ResumoAvaliacoesDto(
				produto.getAvaliacaoMedia(),
				produto.getTotalAvaliacoes(),
				montaDistribuicaoDasNotas(produto.getId()));

		PaginaDto<AvaliacaoDto> avaliacoes = new PaginaDto<>(avaliacaoRepository
				.buscaListaAvaliacaoMaisRecenteDoProduto(produto.getId(), PageRequest.of(pagina, tamanho))
				.map(AvaliacaoDto::new));

		return new AvaliacoesDoProdutoDto(resumo, avaliacoes);
	}

	private Map<Integer, Long> montaDistribuicaoDasNotas(UUID idProduto) {
		Map<Integer, Long> quantidadePorNota = new LinkedHashMap<>();
		for (int nota = Avaliacao.NOTA_MAXIMA; nota >= Avaliacao.NOTA_MINIMA; nota--) {
			quantidadePorNota.put(nota, 0L);
		}
		List<QuantidadePorNotaDto> contagem = avaliacaoRepository.contaAvaliacoesPorNota(idProduto);
		contagem.forEach(linha -> quantidadePorNota.put(linha.nota(), linha.quantidade()));
		return quantidadePorNota;
	}

}
