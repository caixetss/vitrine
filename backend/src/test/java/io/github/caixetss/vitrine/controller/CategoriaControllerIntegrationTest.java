package io.github.caixetss.vitrine.controller;

import io.github.caixetss.vitrine.TestcontainersConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class CategoriaControllerIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void buscaListaCategoria_devolveArvoreDosDadosDeExemplo() throws Exception {
		mockMvc.perform(get("/api/categorias"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(10)))
				.andExpect(jsonPath("$[0].nome").value("Celulares"))
				.andExpect(jsonPath("$[3].slug").value("audio"))
				.andExpect(jsonPath("$[3].icone").value("headphones"))
				.andExpect(jsonPath("$[3].subcategorias[*].nome")
						.value(contains("Fones de ouvido", "Caixas de som", "Headsets gamer", "Soundbars")));
	}

}
