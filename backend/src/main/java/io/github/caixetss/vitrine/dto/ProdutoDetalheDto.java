package io.github.caixetss.vitrine.dto;

import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.entidade.Marca;
import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.entidade.ProdutoEspecificacao;
import io.github.caixetss.vitrine.entidade.ProdutoImagem;
import io.github.caixetss.vitrine.entidade.ProdutoVariacao;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public record ProdutoDetalheDto(
		UUID id,
		String slug,
		String nome,
		String descricao,
		BigDecimal preco,
		BigDecimal precoOriginal,
		BigDecimal avaliacaoMedia,
		Integer totalAvaliacoes,
		Integer totalVendidos,
		Boolean ehFreteGratis,
		MarcaDoProdutoDto marca,
		CategoriaDoProdutoDto categoria,
		List<ImagemDoProdutoDto> imagens,
		List<VariacaoDoProdutoDto> variacoes,
		List<EspecificacaoDoProdutoDto> especificacoes) {

	public ProdutoDetalheDto(Produto produto, List<ProdutoImagem> imagens, List<ProdutoVariacao> variacoes,
			List<ProdutoEspecificacao> especificacoes) {
		this(produto.getId(), produto.getSlug(), produto.getNome(), produto.getDescricao(), produto.getPreco(),
				produto.getPrecoOriginal(), produto.getAvaliacaoMedia(), produto.getTotalAvaliacoes(),
				produto.getTotalVendidos(), produto.getEhFreteGratis(),
				new MarcaDoProdutoDto(produto.getMarca()),
				new CategoriaDoProdutoDto(produto.getCategoria()),
				imagens.stream().map(ImagemDoProdutoDto::new).toList(),
				variacoes.stream().map(VariacaoDoProdutoDto::new).toList(),
				especificacoes.stream().map(EspecificacaoDoProdutoDto::new).toList());
	}

	public record MarcaDoProdutoDto(String nome, String slug) {

		public MarcaDoProdutoDto(Marca marca) {
			this(marca.getNome(), marca.getSlug());
		}

	}

	public record CategoriaDoProdutoDto(String nome, String slug, CategoriaDoProdutoDto categoriaPai) {

		public CategoriaDoProdutoDto(Categoria categoria) {
			this(categoria.getNome(), categoria.getSlug(),
					categoria.ehPrincipal() ? null : new CategoriaDoProdutoDto(categoria.getCategoriaPai()));
		}

	}

	public record ImagemDoProdutoDto(String url, String textoAlternativo, Integer posicaoGaleria) {

		public ImagemDoProdutoDto(ProdutoImagem imagem) {
			this(imagem.getUrl(), imagem.getTextoAlternativo(), imagem.getPosicaoGaleria());
		}

	}

	public record VariacaoDoProdutoDto(UUID id, String nomeCor, String corHex, String codigoEstoque, Integer estoque) {

		public VariacaoDoProdutoDto(ProdutoVariacao variacao) {
			this(variacao.getId(), variacao.getNomeCor(), variacao.getCorHex(), variacao.getCodigoEstoque(), variacao.getEstoque());
		}

	}

	public record EspecificacaoDoProdutoDto(String nome, String valor) {

		public EspecificacaoDoProdutoDto(ProdutoEspecificacao especificacao) {
			this(especificacao.getNome(), especificacao.getValor());
		}

	}

}
