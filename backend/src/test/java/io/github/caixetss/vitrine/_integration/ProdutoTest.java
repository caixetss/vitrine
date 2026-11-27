package io.github.caixetss.vitrine._integration;

import io.github.caixetss.vitrine._integration.helpers.CategoriaTestHelper;
import io.github.caixetss.vitrine._integration.helpers.MarcaTestHelper;
import io.github.caixetss.vitrine._integration.helpers.ProdutoEspecificacaoTestHelper;
import io.github.caixetss.vitrine._integration.helpers.ProdutoImagemTestHelper;
import io.github.caixetss.vitrine._integration.helpers.ProdutoTestHelper;
import io.github.caixetss.vitrine._integration.helpers.ProdutoVariacaoTestHelper;
import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.entidade.Marca;
import io.github.caixetss.vitrine.entidade.Produto;

import net.datafaker.Faker;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Locale;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Automatizados de Integração | Produto")
class ProdutoTest extends AbstractMvcIntegrationTest {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	@Autowired
	private CategoriaTestHelper categoriaTestHelper;

	@Autowired
	private MarcaTestHelper marcaTestHelper;

	@Autowired
	private ProdutoTestHelper produtoTestHelper;

	@Autowired
	private ProdutoImagemTestHelper produtoImagemTestHelper;

	@Autowired
	private ProdutoVariacaoTestHelper produtoVariacaoTestHelper;

	@Autowired
	private ProdutoEspecificacaoTestHelper produtoEspecificacaoTestHelper;

	@Nested
	@DisplayName("GET /api/produtos")
	class BuscaListaProduto {

		@Nested
		@DisplayName("Payload")
		class Payload {

			@Test
			@DisplayName("Não deve aceitar preço mínimo maior que o preço máximo")
			void naoDeveAceitarPrecoMinimoMaiorQueOPrecoMaximo() throws Exception {
				mockMvc.perform(get("/api/produtos").param("precoMin", "500").param("precoMax", "100"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.status").value(400))
						.andExpect(jsonPath("$.erro").value("Requisição inválida"))
						.andExpect(jsonPath("$.campos[0].campo").value("faixaDePrecoValida"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("precoMin deve ser menor ou igual a precoMax"));
			}

			@Test
			@DisplayName("Não deve aceitar texto no lugar de número no filtro de preço")
			void naoDeveAceitarTextoNoLugarDeNumeroNoFiltroDePreco() throws Exception {
				mockMvc.perform(get("/api/produtos").param("precoMin", "abc"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("precoMin"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("valor inválido"));
			}

			@Test
			@DisplayName("Não deve aceitar preço negativo")
			void naoDeveAceitarPrecoNegativo() throws Exception {
				mockMvc.perform(get("/api/produtos").param("precoMin", "-1"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("precoMin"));
			}

			@Test
			@DisplayName("Não deve aceitar avaliação mínima fora de 1 a 5")
			void naoDeveAceitarAvaliacaoMinimaForaDeUmACinco() throws Exception {
				mockMvc.perform(get("/api/produtos").param("avaliacaoMin", "7"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("avaliacaoMin"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("deve ser entre 1 e 5"));
			}

			@Test
			@DisplayName("Não deve aceitar busca com menos de 2 caracteres")
			void naoDeveAceitarBuscaComMenosDeDoisCaracteres() throws Exception {
				mockMvc.perform(get("/api/produtos").param("busca", "a"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("busca"));
			}

			@Test
			@DisplayName("Não deve aceitar ordenação desconhecida")
			void naoDeveAceitarOrdenacaoDesconhecida() throws Exception {
				mockMvc.perform(get("/api/produtos").param("ordem", "qualquer"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("ordem"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("valor inválido"));
			}

			@Test
			@DisplayName("Não deve aceitar página negativa")
			void naoDeveAceitarPaginaNegativa() throws Exception {
				mockMvc.perform(get("/api/produtos").param("pagina", "-1"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("pagina"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("não pode ser negativa"));
			}

			@Test
			@DisplayName("Não deve aceitar tamanho de página acima de 60")
			void naoDeveAceitarTamanhoDePaginaAcimaDeSessenta() throws Exception {
				mockMvc.perform(get("/api/produtos").param("tamanho", "61"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("tamanho"))
						.andExpect(jsonPath("$.campos[0].mensagem").value("deve ser entre 1 e 60"));
			}

		}

		@Nested
		@DisplayName("Service")
		class Service {

			private Categoria categoriaPrincipal;
			private Categoria subcategoria;
			private Marca marca;

			@BeforeEach
			void setUp() {
				categoriaPrincipal = categoriaTestHelper.criaCategoriaPrincipal();
				subcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 1);
				marca = marcaTestHelper.criaMarca();
			}

			@Test
			@DisplayName("Não deve devolver produto inativo")
			void naoDeveDevolverProdutoInativo() throws Exception {
				produtoTestHelper.novoProduto(subcategoria, marca).inativo().salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()))
						.andExpect(jsonPath("$.totalItens").value(0))
						.andExpect(jsonPath("$.itens", empty()));
			}

			@Test
			@DisplayName("Deve tratar porcentagem na busca como texto e não como curinga")
			void deveTratarPorcentagemNaBuscaComoTextoENaoComoCuringa() throws Exception {
				produtoTestHelper.novoProduto(subcategoria, marca).salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("busca", "%%"))
						.andExpect(jsonPath("$.totalItens").value(0));
			}

			@Test
			@DisplayName("Deve buscar pelo nome ignorando acentos e maiúsculas")
			void deveBuscarPeloNomeIgnorandoAcentosEMaiusculas() throws Exception {
				String codigo = FAKER.number().digits(10);
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).comNome("Câmera Ação " + codigo).salva();

				mockMvc.perform(get("/api/produtos").param("busca", "CAMERA ACAO " + codigo))
						.andExpect(jsonPath("$.totalItens").value(1))
						.andExpect(jsonPath("$.itens[0].slug").value(produto.getSlug()));
			}

			@Test
			@DisplayName("Deve incluir os produtos das subcategorias ao filtrar pela categoria principal")
			void deveIncluirOsProdutosDasSubcategoriasAoFiltrarPelaCategoriaPrincipal() throws Exception {
				Categoria outraSubcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 2);
				produtoTestHelper.novoProduto(subcategoria, marca).salva();
				produtoTestHelper.novoProduto(outraSubcategoria, marca).salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()))
						.andExpect(jsonPath("$.totalItens").value(2));
			}

			@Test
			@DisplayName("Deve trazer só os produtos da subcategoria filtrada")
			void deveTrazerSoOsProdutosDaSubcategoriaFiltrada() throws Exception {
				Categoria outraSubcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 2);
				Produto produtoDaSubcategoria = produtoTestHelper.novoProduto(subcategoria, marca).salva();
				produtoTestHelper.novoProduto(outraSubcategoria, marca).salva();

				mockMvc.perform(get("/api/produtos").param("categoria", subcategoria.getSlug()))
						.andExpect(jsonPath("$.totalItens").value(1))
						.andExpect(jsonPath("$.itens[0].slug").value(produtoDaSubcategoria.getSlug()));
			}

			@Test
			@DisplayName("Deve filtrar pela faixa de preço incluindo os limites")
			void deveFiltrarPelaFaixaDePrecoIncluindoOsLimites() throws Exception {
				produtoTestHelper.novoProduto(subcategoria, marca).comPreco("99.99").salva();
				Produto noLimiteMinimo = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("100.00").salva();
				Produto noLimiteMaximo = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("200.00").salva();
				produtoTestHelper.novoProduto(subcategoria, marca).comPreco("200.01").salva();

				mockMvc.perform(get("/api/produtos")
								.param("categoria", categoriaPrincipal.getSlug())
								.param("precoMin", "100")
								.param("precoMax", "200"))
						.andExpect(jsonPath("$.itens[*].slug").value(containsInAnyOrder(noLimiteMinimo.getSlug(), noLimiteMaximo.getSlug())));
			}

			@Test
			@DisplayName("Deve trazer só produtos com média igual ou acima da avaliação mínima")
			void deveTrazerSoProdutosComMediaIgualOuAcimaDaAvaliacaoMinima() throws Exception {
				produtoTestHelper.novoProduto(subcategoria, marca).comAvaliacao("3.9", 10).salva();
				Produto bemAvaliado = produtoTestHelper.novoProduto(subcategoria, marca).comAvaliacao("4.0", 10).salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("avaliacaoMin", "4"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(bemAvaliado.getSlug())));
			}

			@Test
			@DisplayName("Deve combinar várias marcas com OU")
			void deveCombinarVariasMarcasComOu() throws Exception {
				Marca segundaMarca = marcaTestHelper.criaMarca();
				Marca terceiraMarca = marcaTestHelper.criaMarca();
				Produto daPrimeira = produtoTestHelper.novoProduto(subcategoria, marca).salva();
				Produto daSegunda = produtoTestHelper.novoProduto(subcategoria, segundaMarca).salva();
				produtoTestHelper.novoProduto(subcategoria, terceiraMarca).salva();

				mockMvc.perform(get("/api/produtos")
								.param("categoria", categoriaPrincipal.getSlug())
								.param("marcas", marca.getSlug() + "," + segundaMarca.getSlug()))
						.andExpect(jsonPath("$.itens[*].slug").value(containsInAnyOrder(daPrimeira.getSlug(), daSegunda.getSlug())));
			}

			@Test
			@DisplayName("Deve trazer só produtos com frete grátis quando o filtro é marcado")
			void deveTrazerSoProdutosComFreteGratisQuandoOFiltroEMarcado() throws Exception {
				produtoTestHelper.novoProduto(subcategoria, marca).salva();
				Produto comFreteGratis = produtoTestHelper.novoProduto(subcategoria, marca).comFreteGratis().salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("freteGratis", "true"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(comFreteGratis.getSlug())));
			}

			@Test
			@DisplayName("Deve aplicar todos os filtros ao mesmo tempo")
			void deveAplicarTodosOsFiltrosAoMesmoTempo() throws Exception {
				Marca outraMarca = marcaTestHelper.criaMarca();
				Produto atendeTudo = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("150.00").comFreteGratis().salva();
				produtoTestHelper.novoProduto(subcategoria, marca).comPreco("150.00").salva();
				produtoTestHelper.novoProduto(subcategoria, marca).comPreco("350.00").comFreteGratis().salva();
				produtoTestHelper.novoProduto(subcategoria, outraMarca).comPreco("150.00").comFreteGratis().salva();

				mockMvc.perform(get("/api/produtos")
								.param("categoria", categoriaPrincipal.getSlug())
								.param("marcas", marca.getSlug())
								.param("precoMax", "300")
								.param("freteGratis", "true"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(atendeTudo.getSlug())));
			}

			@Test
			@DisplayName("Deve ordenar por menor preço")
			void deveOrdenarPorMenorPreco() throws Exception {
				Produto caro = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("300.00").salva();
				Produto barato = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("10.00").salva();
				Produto medio = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("50.00").salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("ordem", "menor-preco"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(barato.getSlug(), medio.getSlug(), caro.getSlug())));
			}

			@Test
			@DisplayName("Deve ordenar por maior preço")
			void deveOrdenarPorMaiorPreco() throws Exception {
				Produto barato = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("10.00").salva();
				Produto caro = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("300.00").salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("ordem", "maior-preco"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(caro.getSlug(), barato.getSlug())));
			}

			@Test
			@DisplayName("Deve ordenar por avaliação desempatando pelo total de avaliações")
			void deveOrdenarPorAvaliacaoDesempatandoPeloTotalDeAvaliacoes() throws Exception {
				Produto notaMenor = produtoTestHelper.novoProduto(subcategoria, marca).comAvaliacao("4.2", 900).salva();
				Produto notaMaiorPoucasAvaliacoes = produtoTestHelper.novoProduto(subcategoria, marca).comAvaliacao("4.8", 10).salva();
				Produto notaMaiorMuitasAvaliacoes = produtoTestHelper.novoProduto(subcategoria, marca).comAvaliacao("4.8", 500).salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("ordem", "avaliacao"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(
								notaMaiorMuitasAvaliacoes.getSlug(), notaMaiorPoucasAvaliacoes.getSlug(), notaMenor.getSlug())));
			}

			@Test
			@DisplayName("Deve ordenar por mais vendidos")
			void deveOrdenarPorMaisVendidos() throws Exception {
				Produto poucoVendido = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(5).salva();
				Produto maisVendido = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(500).salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("ordem", "mais-vendidos"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(maisVendido.getSlug(), poucoVendido.getSlug())));
			}

			@Test
			@DisplayName("Deve ordenar por maior desconto deixando produtos sem desconto no fim")
			void deveOrdenarPorMaiorDescontoDeixandoProdutosSemDescontoNoFim() throws Exception {
				Produto semDesconto = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("100.00").salva();
				Produto descontoDeDezPorCento = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("90.00").comPrecoOriginal("100.00").salva();
				Produto descontoDeMetade = produtoTestHelper.novoProduto(subcategoria, marca).comPreco("50.00").comPrecoOriginal("100.00").salva();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("ordem", "maior-desconto"))
						.andExpect(jsonPath("$.itens[*].slug").value(contains(
								descontoDeMetade.getSlug(), descontoDeDezPorCento.getSlug(), semDesconto.getSlug())));
			}

			@Test
			@DisplayName("Não deve fazer uma consulta ao banco por produto da página")
			void naoDeveFazerUmaConsultaAoBancoPorProdutoDaPagina() throws Exception {
				for (int i = 0; i < 5; i++) {
					Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).salva();
					produtoImagemTestHelper.criaImagem(produto, 0);
				}
				Statistics estatisticas = zeraContagemDeConsultas();

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug())).andExpect(status().isOk());

				assertThat(estatisticas.getPrepareStatementCount()).isLessThanOrEqualTo(3);
			}

			@Test
			@DisplayName("Deve devolver a página com a capa e os dados do card")
			void deveDevolverAPaginaComACapaEOsDadosDoCard() throws Exception {
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca)
						.comPreco("279.90").comPrecoOriginal("449.90").comFreteGratis().comAvaliacao("4.7", 2384).salva();
				String urlDaCapa = produtoImagemTestHelper.criaImagem(produto, 0).getUrl();
				produtoImagemTestHelper.criaImagem(produto, 1);

				mockMvc.perform(get("/api/produtos").param("categoria", categoriaPrincipal.getSlug()).param("tamanho", "10"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.pagina").value(0))
						.andExpect(jsonPath("$.tamanho").value(10))
						.andExpect(jsonPath("$.totalItens").value(1))
						.andExpect(jsonPath("$.totalPaginas").value(1))
						.andExpect(jsonPath("$.itens[0].slug").value(produto.getSlug()))
						.andExpect(jsonPath("$.itens[0].imagemCapa").value(urlDaCapa))
						.andExpect(jsonPath("$.itens[0].preco").value(279.90))
						.andExpect(jsonPath("$.itens[0].precoOriginal").value(449.90))
						.andExpect(jsonPath("$.itens[0].avaliacaoMedia").value(4.7))
						.andExpect(jsonPath("$.itens[0].totalAvaliacoes").value(2384))
						.andExpect(jsonPath("$.itens[0].ehFreteGratis").value(true));
			}

		}

	}

	@Nested
	@DisplayName("GET /api/produtos/filtros")
	class BuscaFiltrosDisponiveis {

		@Nested
		@DisplayName("Payload")
		class Payload {

			@Test
			@DisplayName("Não deve aceitar preço mínimo maior que o preço máximo")
			void naoDeveAceitarPrecoMinimoMaiorQueOPrecoMaximo() throws Exception {
				mockMvc.perform(get("/api/produtos/filtros").param("precoMin", "500").param("precoMax", "100"))
						.andExpect(status().isBadRequest())
						.andExpect(jsonPath("$.campos[0].campo").value("faixaDePrecoValida"));
			}

		}

		@Nested
		@DisplayName("Service")
		class Service {

			private Categoria categoriaPrincipal;
			private Categoria primeiraSubcategoria;
			private Categoria segundaSubcategoria;
			private Marca marca;
			private Marca outraMarca;

			@BeforeEach
			void setUp() {
				categoriaPrincipal = categoriaTestHelper.criaCategoriaPrincipal();
				primeiraSubcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 1);
				segundaSubcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 2);
				marca = marcaTestHelper.criaMarca();
				outraMarca = marcaTestHelper.criaMarca();
				produtoTestHelper.novoProduto(primeiraSubcategoria, marca).comPreco("40.00").comFreteGratis().salva();
				produtoTestHelper.novoProduto(primeiraSubcategoria, marca).comPreco("90.00").salva();
				produtoTestHelper.novoProduto(segundaSubcategoria, outraMarca).comPreco("900.00").comFreteGratis().salva();
			}

			@Test
			@DisplayName("Deve contar as marcas ignorando o próprio filtro de marcas")
			void deveContarAsMarcasIgnorandoOProprioFiltroDeMarcas() throws Exception {
				mockMvc.perform(get("/api/produtos/filtros")
								.param("categoria", categoriaPrincipal.getSlug())
								.param("marcas", marca.getSlug()))
						.andExpect(jsonPath("$.marcas[?(@.slug == '" + outraMarca.getSlug() + "')].total").value(contains(1)))
						.andExpect(jsonPath("$.subcategorias[*].slug").value(contains(primeiraSubcategoria.getSlug())))
						.andExpect(jsonPath("$.subcategorias[0].total").value(2));
			}

			@Test
			@DisplayName("Deve devolver as contagens por subcategoria e marca, a faixa de preço e o total com frete grátis")
			void deveDevolverAsContagensAFaixaDePrecoEOTotalComFreteGratis() throws Exception {
				mockMvc.perform(get("/api/produtos/filtros").param("categoria", categoriaPrincipal.getSlug()))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.subcategorias[*].slug").value(contains(primeiraSubcategoria.getSlug(), segundaSubcategoria.getSlug())))
						.andExpect(jsonPath("$.subcategorias[*].total").value(contains(2, 1)))
						.andExpect(jsonPath("$.marcas[?(@.slug == '" + marca.getSlug() + "')].total").value(contains(2)))
						.andExpect(jsonPath("$.marcas[?(@.slug == '" + outraMarca.getSlug() + "')].total").value(contains(1)))
						.andExpect(jsonPath("$.precoMinimo").value(40.00))
						.andExpect(jsonPath("$.precoMaximo").value(900.00))
						.andExpect(jsonPath("$.totalFreteGratis").value(2));
			}

		}

	}

	@Nested
	@DisplayName("GET /api/produtos/{slug}")
	class BuscaIndividualProduto {

		@Nested
		@DisplayName("Service")
		class Service {

			private Categoria categoriaPrincipal;
			private Categoria subcategoria;
			private Marca marca;

			@BeforeEach
			void setUp() {
				categoriaPrincipal = categoriaTestHelper.criaCategoriaPrincipal();
				subcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 1);
				marca = marcaTestHelper.criaMarca();
			}

			@Test
			@DisplayName("Não deve encontrar produto com slug inexistente")
			void naoDeveEncontrarProdutoComSlugInexistente() throws Exception {
				String slugInexistente = "produto-inexistente-" + FAKER.number().digits(10);

				mockMvc.perform(get("/api/produtos/" + slugInexistente))
						.andExpect(status().isNotFound())
						.andExpect(jsonPath("$.status").value(404))
						.andExpect(jsonPath("$.erro").value("Não encontrado"))
						.andExpect(jsonPath("$.mensagem").value("Produto não encontrado: " + slugInexistente))
						.andExpect(jsonPath("$.campos", empty()));
			}

			@Test
			@DisplayName("Não deve encontrar produto inativo")
			void naoDeveEncontrarProdutoInativo() throws Exception {
				Produto produtoInativo = produtoTestHelper.novoProduto(subcategoria, marca).inativo().salva();

				mockMvc.perform(get("/api/produtos/" + produtoInativo.getSlug()))
						.andExpect(status().isNotFound());
			}

			@Test
			@DisplayName("Não deve mostrar variação inativa")
			void naoDeveMostrarVariacaoInativa() throws Exception {
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).salva();
				produtoVariacaoTestHelper.criaVariacao(produto, "Preto");
				produtoVariacaoTestHelper.criaVariacaoInativa(produto, "Branco");

				mockMvc.perform(get("/api/produtos/" + produto.getSlug()))
						.andExpect(jsonPath("$.variacoes[*].nomeCor").value(contains("Preto")))
						.andExpect(jsonPath("$.variacoes[*].nomeCor").value(not(hasItem("Branco"))));
			}

			@Test
			@DisplayName("Não deve fazer mais de uma consulta por coleção do produto")
			void naoDeveFazerMaisDeUmaConsultaPorColecaoDoProduto() throws Exception {
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).salva();
				produtoImagemTestHelper.criaImagem(produto, 0);
				produtoImagemTestHelper.criaImagem(produto, 1);
				produtoVariacaoTestHelper.criaVariacao(produto, "Preto");
				produtoVariacaoTestHelper.criaVariacao(produto, "Azul");
				produtoEspecificacaoTestHelper.criaEspecificacao(produto, "Bateria", 1);
				produtoEspecificacaoTestHelper.criaEspecificacao(produto, "Peso", 2);
				Statistics estatisticas = zeraContagemDeConsultas();

				mockMvc.perform(get("/api/produtos/" + produto.getSlug())).andExpect(status().isOk());

				assertThat(estatisticas.getPrepareStatementCount()).isEqualTo(4);
			}

			@Test
			@DisplayName("Deve devolver o produto com marca, categoria com pai e coleções ordenadas")
			void deveDevolverOProdutoComMarcaCategoriaComPaiEColecoesOrdenadas() throws Exception {
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).salva();
				produtoImagemTestHelper.criaImagem(produto, 1);
				produtoImagemTestHelper.criaImagem(produto, 0);
				produtoVariacaoTestHelper.criaVariacao(produto, "Preto");
				produtoEspecificacaoTestHelper.criaEspecificacao(produto, "Peso", 2);
				produtoEspecificacaoTestHelper.criaEspecificacao(produto, "Bateria", 1);

				mockMvc.perform(get("/api/produtos/" + produto.getSlug()))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.slug").value(produto.getSlug()))
						.andExpect(jsonPath("$.nome").value(produto.getNome()))
						.andExpect(jsonPath("$.marca.slug").value(marca.getSlug()))
						.andExpect(jsonPath("$.categoria.slug").value(subcategoria.getSlug()))
						.andExpect(jsonPath("$.categoria.categoriaPai.slug").value(categoriaPrincipal.getSlug()))
						.andExpect(jsonPath("$.categoria.categoriaPai.categoriaPai").value(nullValue()))
						.andExpect(jsonPath("$.imagens[*].posicaoGaleria").value(contains(0, 1)))
						.andExpect(jsonPath("$.variacoes", hasSize(1)))
						.andExpect(jsonPath("$.especificacoes[*].nome").value(contains("Bateria", "Peso")));
			}

		}

	}

	@Nested
	@DisplayName("GET /api/produtos/{slug}/relacionados")
	class BuscaListaProdutoRelacionado {

		@Nested
		@DisplayName("Service")
		class Service {

			private Categoria subcategoria;
			private Marca marca;

			@BeforeEach
			void setUp() {
				Categoria categoriaPrincipal = categoriaTestHelper.criaCategoriaPrincipal();
				subcategoria = categoriaTestHelper.criaSubcategoria(categoriaPrincipal, 1);
				marca = marcaTestHelper.criaMarca();
			}

			@Test
			@DisplayName("Não deve encontrar relacionados de produto inexistente")
			void naoDeveEncontrarRelacionadosDeProdutoInexistente() throws Exception {
				mockMvc.perform(get("/api/produtos/produto-inexistente-" + FAKER.number().digits(10) + "/relacionados"))
						.andExpect(status().isNotFound());
			}

			@Test
			@DisplayName("Deve devolver lista vazia quando não há outros produtos na categoria")
			void deveDevolverListaVaziaQuandoNaoHaOutrosProdutosNaCategoria() throws Exception {
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).salva();

				mockMvc.perform(get("/api/produtos/" + produto.getSlug() + "/relacionados"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$", empty()));
			}

			@Test
			@DisplayName("Deve devolver até cinco produtos da mesma categoria, mais vendidos primeiro, sem o próprio produto")
			void deveDevolverAteCincoProdutosDaMesmaCategoriaMaisVendidosPrimeiroSemOProprioProduto() throws Exception {
				Produto produto = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(1000).salva();
				Produto vendeuDez = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(10).salva();
				Produto vendeuVinte = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(20).salva();
				Produto vendeuTrinta = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(30).salva();
				Produto vendeuQuarenta = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(40).salva();
				Produto vendeuCinquenta = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(50).salva();
				Produto vendeuSessenta = produtoTestHelper.novoProduto(subcategoria, marca).comTotalVendidos(60).salva();
				Categoria outraCategoria = categoriaTestHelper.criaSubcategoria(categoriaTestHelper.criaCategoriaPrincipal(), 1);
				produtoTestHelper.novoProduto(outraCategoria, marca).comTotalVendidos(5000).salva();

				mockMvc.perform(get("/api/produtos/" + produto.getSlug() + "/relacionados"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$[*].slug").value(contains(
								vendeuSessenta.getSlug(), vendeuCinquenta.getSlug(), vendeuQuarenta.getSlug(),
								vendeuTrinta.getSlug(), vendeuVinte.getSlug())))
						.andExpect(jsonPath("$[*].slug").value(not(hasItem(vendeuDez.getSlug()))));
			}

		}

	}

}
