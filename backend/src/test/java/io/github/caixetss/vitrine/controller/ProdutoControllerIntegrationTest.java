package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.TestcontainersConfiguration;

import jakarta.persistence.EntityManagerFactory;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.everyItem;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.Matchers.startsWith;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "spring.jpa.properties.hibernate.generate_statistics=true")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class ProdutoControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private EntityManagerFactory entityManagerFactory;

	@Test
	void semFiltro_devolvePrimeiraPaginaDeTodosOsProdutosAtivos() throws Exception {
		mockMvc.perform(get("/api/produtos"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.totalItens").value(41))
				.andExpect(jsonPath("$.pagina").value(0))
				.andExpect(jsonPath("$.tamanho").value(20))
				.andExpect(jsonPath("$.totalPaginas").value(3))
				.andExpect(jsonPath("$.itens", hasSize(20)))
				.andExpect(jsonPath("$.itens[*].imagemCapa", everyItem(startsWith("https://placehold.co/"))));
	}

	@Test
	void filtroDeCategoriaPrincipal_incluiProdutosDasSubcategorias() throws Exception {
		mockMvc.perform(get("/api/produtos").param("categoria", "audio"))
				.andExpect(jsonPath("$.totalItens").value(13));
	}

	@Test
	void filtroDeSubcategoria_trazSoOsProdutosDela() throws Exception {
		mockMvc.perform(get("/api/produtos").param("categoria", "fones-de-ouvido"))
				.andExpect(jsonPath("$.totalItens").value(8));
	}

	@Test
	void busca_ignoraAcentoEMaiusculas() throws Exception {
		mockMvc.perform(get("/api/produtos").param("busca", "CAMERA"))
				.andExpect(jsonPath("$.totalItens").value(1))
				.andExpect(jsonPath("$.itens[0].nome").value("Smartphone Orion X Pro 256GB, 12GB RAM, Câmera 108MP"));
	}

	@Test
	void busca_trataPercentualComoTextoENaoComoCuringa() throws Exception {
		mockMvc.perform(get("/api/produtos").param("busca", "%%"))
				.andExpect(jsonPath("$.totalItens").value(0));
	}

	@Test
	void filtroDePreco_respeitaOLimiteMaximo() throws Exception {
		mockMvc.perform(get("/api/produtos").param("categoria", "audio").param("precoMax", "500"))
				.andExpect(jsonPath("$.totalItens").value(12));
	}

	@Test
	void filtroDeAvaliacao_trazSoProdutosComMediaIgualOuAcima() throws Exception {
		mockMvc.perform(get("/api/produtos").param("avaliacaoMin", "5"))
				.andExpect(jsonPath("$.totalItens").value(0));
	}

	@Test
	void filtroDeMarcas_combinaAsMarcasComOu() throws Exception {
		mockMvc.perform(get("/api/produtos").param("marcas", "sonora,vibe"))
				.andExpect(jsonPath("$.totalItens").value(9));
	}

	@Test
	void filtroDeFreteGratis_tiraProdutosSemFreteGratis() throws Exception {
		mockMvc.perform(get("/api/produtos").param("freteGratis", "true"))
				.andExpect(jsonPath("$.totalItens").value(36));
	}

	@Test
	void filtrosCombinados_aplicamTodosAoMesmoTempo() throws Exception {
		mockMvc.perform(get("/api/produtos")
						.param("categoria", "audio")
						.param("marcas", "sonora")
						.param("precoMax", "300")
						.param("ordem", "menor-preco"))
				.andExpect(jsonPath("$.itens[*].nome").value(contains(
						"Fone com Fio Sonora Basic P2 com Microfone",
						"Fone sem Fio TWS Sonora Buds Lite com Estojo de Carga",
						"Fone de Ouvido Bluetooth Sonora Pulse ANC com Cancelamento de Ruído")));
	}

	@Test
	void ordemMenorPreco_comecaPeloMaisBarato() throws Exception {
		mockMvc.perform(get("/api/produtos").param("ordem", "menor-preco"))
				.andExpect(jsonPath("$.itens[0].nome").value("Fone com Fio Sonora Basic P2 com Microfone"));
	}

	@Test
	void ordemMaiorPreco_comecaPeloMaisCaro() throws Exception {
		mockMvc.perform(get("/api/produtos").param("ordem", "maior-preco"))
				.andExpect(jsonPath("$.itens[0].nome").value("Notebook Orion 15 Ryzen 5, 16GB RAM, SSD 512GB"));
	}

	@Test
	void ordemAvaliacao_desempataPeloTotalDeAvaliacoes() throws Exception {
		mockMvc.perform(get("/api/produtos").param("ordem", "avaliacao"))
				.andExpect(jsonPath("$.itens[0].nome").value("Camiseta Básica Algodão Pima Unissex"))
				.andExpect(jsonPath("$.itens[1].nome").value("Garrafa Térmica Trek 750ml Inox"));
	}

	@Test
	void ordemMaisVendidos_comecaPeloMaiorTotalDeVendas() throws Exception {
		mockMvc.perform(get("/api/produtos").param("ordem", "mais-vendidos"))
				.andExpect(jsonPath("$.itens[0].nome").value("Camiseta Básica Algodão Pima Unissex"));
	}

	@Test
	void ordemMaiorDesconto_deixaProdutosSemDescontoNoFim() throws Exception {
		mockMvc.perform(get("/api/produtos").param("ordem", "maior-desconto").param("tamanho", "60"))
				.andExpect(jsonPath("$.itens[0].nome").value("Fone de Ouvido Bluetooth Sonora Pulse ANC com Cancelamento de Ruído"))
				.andExpect(jsonPath("$.itens[40].precoOriginal").value(nullValue()));
	}

	@ParameterizedTest
	@ValueSource(strings = {
			"precoMin=500&precoMax=100",
			"precoMin=-1",
			"avaliacaoMin=7",
			"busca=a",
			"ordem=qualquer",
			"tamanho=61",
			"pagina=-1"})
	void parametroInvalido_devolve400(String parametros) throws Exception {
		mockMvc.perform(get("/api/produtos?" + parametros))
				.andExpect(status().isBadRequest());
	}

	@Test
	void listagem_naoFazUmaConsultaPorProduto() throws Exception {
		Statistics estatisticas = entityManagerFactory.unwrap(SessionFactory.class).getStatistics();
		estatisticas.clear();

		mockMvc.perform(get("/api/produtos")).andExpect(status().isOk());

		assertThat(estatisticas.getPrepareStatementCount()).isEqualTo(3);
	}

	@Test
	void filtrosDisponiveis_contaPorSubcategoriaMarcaPrecoEFrete() throws Exception {
		mockMvc.perform(get("/api/produtos/filtros").param("categoria", "audio"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.subcategorias[*].slug").value(contains("fones-de-ouvido", "caixas-de-som", "headsets-gamer", "soundbars")))
				.andExpect(jsonPath("$.subcategorias[0].total").value(8))
				.andExpect(jsonPath("$.marcas[?(@.slug == 'sonora')].total").value(contains(6)))
				.andExpect(jsonPath("$.precoMinimo").value(39.90))
				.andExpect(jsonPath("$.precoMaximo").value(899.00))
				.andExpect(jsonPath("$.totalFreteGratis").value(12));
	}

	@Test
	void filtrosDisponiveis_contagemDeMarcasIgnoraAsMarcasJaMarcadas() throws Exception {
		mockMvc.perform(get("/api/produtos/filtros").param("categoria", "audio").param("marcas", "sonora"))
				.andExpect(jsonPath("$.marcas[?(@.slug == 'vibe')].total").value(contains(3)))
				.andExpect(jsonPath("$.subcategorias[*].slug").value(contains("fones-de-ouvido")))
				.andExpect(jsonPath("$.subcategorias[0].total").value(6));
	}

}
