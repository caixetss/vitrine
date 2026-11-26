package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.TestcontainersConfiguration;

import com.jayway.jsonpath.JsonPath;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class AvaliacaoControllerIntegrationTest {

	private static final String URL_AVALIACOES_FONE_PULSE =
			"/api/produtos/fone-de-ouvido-bluetooth-sonora-pulse-anc-com-cancelamento-de-ruido/avaliacoes";

	@Autowired
	private MockMvc mockMvc;

	@Test
	void avaliacoes_devolvemCincoMaisRecentesPorPadrao() throws Exception {
		String resposta = mockMvc.perform(get(URL_AVALIACOES_FONE_PULSE))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.avaliacoes.tamanho").value(5))
				.andExpect(jsonPath("$.avaliacoes.itens", hasSize(5)))
				.andReturn().getResponse().getContentAsString();

		List<String> datas = JsonPath.read(resposta, "$.avaliacoes.itens[*].criadoEm");
		assertThat(datas).isSortedAccordingTo(Comparator.reverseOrder());
	}

	@Test
	void distribuicao_trazAsCincoNotasESomaOTotal() throws Exception {
		String resposta = mockMvc.perform(get(URL_AVALIACOES_FONE_PULSE))
				.andExpect(jsonPath("$.resumo.distribuicao.*", hasSize(5)))
				.andReturn().getResponse().getContentAsString();

		Map<String, Integer> distribuicao = JsonPath.read(resposta, "$.resumo.distribuicao");
		int total = JsonPath.read(resposta, "$.resumo.total");
		int totalDaPagina = JsonPath.read(resposta, "$.avaliacoes.totalItens");

		assertThat(distribuicao.keySet()).containsExactly("5", "4", "3", "2", "1");
		assertThat(distribuicao.values().stream().mapToInt(Integer::intValue).sum()).isEqualTo(total).isEqualTo(totalDaPagina);
	}

	@Test
	void avaliacoes_produtoInexistenteDevolve404() throws Exception {
		mockMvc.perform(get("/api/produtos/produto-que-nao-existe/avaliacoes"))
				.andExpect(status().isNotFound());
	}

	@Test
	void avaliacoes_tamanhoInvalidoDevolve400() throws Exception {
		mockMvc.perform(get(URL_AVALIACOES_FONE_PULSE).param("tamanho", "61"))
				.andExpect(status().isBadRequest());
	}

}
