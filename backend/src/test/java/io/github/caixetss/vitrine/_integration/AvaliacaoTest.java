package io.github.caixetss.vitrine._integration;

import io.github.caixetss.vitrine._integration.helpers.AvaliacaoTestHelper;
import io.github.caixetss.vitrine._integration.helpers.CategoriaTestHelper;
import io.github.caixetss.vitrine._integration.helpers.MarcaTestHelper;
import io.github.caixetss.vitrine._integration.helpers.ProdutoTestHelper;
import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.entidade.Produto;

import net.datafaker.Faker;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Automatizados de Integração | Avaliação")
class AvaliacaoTest extends AbstractMvcIntegrationTest {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	@Autowired
	private CategoriaTestHelper categoriaTestHelper;

	@Autowired
	private MarcaTestHelper marcaTestHelper;

	@Autowired
	private ProdutoTestHelper produtoTestHelper;

	@Autowired
	private AvaliacaoTestHelper avaliacaoTestHelper;

	@Nested
	@DisplayName("GET /api/produtos/{slug}/avaliacoes")
	class BuscaListaAvaliacaoDoProduto {

		@Nested
		@DisplayName("Payload")
		class Payload {

			@Test
			@DisplayName("Não deve aceitar tamanho de página acima de 60")
			void naoDeveAceitarTamanhoDePaginaAcimaDeSessenta() throws Exception {
				mockMvc.perform(get("/api/produtos/qualquer-produto/avaliacoes").param("tamanho", "61"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("tamanho"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("deve ser entre 1 e 60"));
			}

		}

		@Nested
		@DisplayName("Service")
		class Service {

			private Produto produto;

			@BeforeEach
			void setUp() {
				Categoria subcategoria = categoriaTestHelper.criaSubcategoria(categoriaTestHelper.criaCategoriaPrincipal(), 1);
				produto = produtoTestHelper.novoProduto(subcategoria, marcaTestHelper.criaMarca()).comAvaliacao("4.0", 3).salva();
			}

			@Test
			@DisplayName("Não deve encontrar avaliações de produto inexistente")
			void naoDeveEncontrarAvaliacoesDeProdutoInexistente() throws Exception {
				mockMvc.perform(get("/api/produtos/produto-inexistente-" + FAKER.number().digits(10) + "/avaliacoes"))
						.andExpect(status().isNotFound())
						.andExpect(jsonPath("$.status").value(404));
			}

			@Test
			@DisplayName("Deve trazer as cinco notas na distribuição, com zero nas que não têm avaliação")
			void deveTrazerAsCincoNotasNaDistribuicaoComZeroNasQueNaoTemAvaliacao() throws Exception {
				Instant agora = Instant.now();
				avaliacaoTestHelper.criaAvaliacao(produto, 5, agora);
				avaliacaoTestHelper.criaAvaliacao(produto, 5, agora);
				avaliacaoTestHelper.criaAvaliacao(produto, 2, agora);

				mockMvc.perform(get("/api/produtos/" + produto.getSlug() + "/avaliacoes"))
						.andExpect(jsonPath("$.resumo.distribuicao.5").value(2))
						.andExpect(jsonPath("$.resumo.distribuicao.4").value(0))
						.andExpect(jsonPath("$.resumo.distribuicao.3").value(0))
						.andExpect(jsonPath("$.resumo.distribuicao.2").value(1))
						.andExpect(jsonPath("$.resumo.distribuicao.1").value(0));
			}

			@Test
			@DisplayName("Deve paginar as avaliações das mais recentes para as mais antigas")
			void devePaginarAsAvaliacoesDasMaisRecentesParaAsMaisAntigas() throws Exception {
				Instant agora = Instant.now().truncatedTo(ChronoUnit.SECONDS);
				avaliacaoTestHelper.criaAvaliacao(produto, 3, agora.minus(2, ChronoUnit.DAYS));
				avaliacaoTestHelper.criaAvaliacao(produto, 5, agora);
				avaliacaoTestHelper.criaAvaliacao(produto, 4, agora.minus(1, ChronoUnit.DAYS));

				mockMvc.perform(get("/api/produtos/" + produto.getSlug() + "/avaliacoes").param("tamanho", "2"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.avaliacoes.itens", hasSize(2)))
						.andExpect(jsonPath("$.avaliacoes.itens[*].nota").value(contains(5, 4)))
						.andExpect(jsonPath("$.avaliacoes.totalItens").value(3))
						.andExpect(jsonPath("$.avaliacoes.totalPaginas").value(2));
			}

			@Test
			@DisplayName("Deve devolver o resumo com média e total do produto")
			void deveDevolverOResumoComMediaETotalDoProduto() throws Exception {
				mockMvc.perform(get("/api/produtos/" + produto.getSlug() + "/avaliacoes"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.resumo.media").value(4.0))
						.andExpect(jsonPath("$.resumo.total").value(3))
						.andExpect(jsonPath("$.avaliacoes.tamanho").value(5));
			}

		}

	}

}
