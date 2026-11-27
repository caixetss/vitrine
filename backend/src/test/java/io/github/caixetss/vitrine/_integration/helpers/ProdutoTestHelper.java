package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.entidade.Marca;
import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.repository.ProdutoRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.math.BigDecimal;
import java.util.Locale;

@RequiredArgsConstructor
public class ProdutoTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final ProdutoRepository produtoRepository;

	public NovoProduto novoProduto(Categoria categoria, Marca marca) {
		return new NovoProduto(categoria, marca);
	}

	public class NovoProduto {

		private final Produto produto = NovaEntidade.instancia(Produto.class);

		private NovoProduto(Categoria categoria, Marca marca) {
			String nome = FAKER.commerce().productName() + " " + FAKER.number().digits(8);
			define("nome", nome);
			define("slug", "produto-" + FAKER.number().digits(12));
			define("descricao", FAKER.lorem().sentence());
			define("categoria", categoria);
			define("marca", marca);
			define("preco", new BigDecimal("100.00"));
			define("precoOriginal", null);
			define("ehFreteGratis", false);
			define("avaliacaoMedia", new BigDecimal("4.0"));
			define("totalAvaliacoes", 0);
			define("totalVendidos", 0);
			define("ehInativo", false);
		}

		public NovoProduto comNome(String nome) {
			return define("nome", nome);
		}

		public NovoProduto comPreco(String preco) {
			return define("preco", new BigDecimal(preco));
		}

		public NovoProduto comPrecoOriginal(String precoOriginal) {
			return define("precoOriginal", new BigDecimal(precoOriginal));
		}

		public NovoProduto comFreteGratis() {
			return define("ehFreteGratis", true);
		}

		public NovoProduto comAvaliacao(String avaliacaoMedia, int totalAvaliacoes) {
			define("avaliacaoMedia", new BigDecimal(avaliacaoMedia));
			return define("totalAvaliacoes", totalAvaliacoes);
		}

		public NovoProduto comTotalVendidos(int totalVendidos) {
			return define("totalVendidos", totalVendidos);
		}

		public NovoProduto inativo() {
			return define("ehInativo", true);
		}

		public Produto salva() {
			return produtoRepository.save(produto);
		}

		private NovoProduto define(String campo, Object valor) {
			ReflectionTestUtils.setField(produto, campo, valor);
			return this;
		}

	}

}
