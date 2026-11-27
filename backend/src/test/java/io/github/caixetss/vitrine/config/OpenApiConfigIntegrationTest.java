package io.github.caixetss.vitrine.config;

import io.github.caixetss.vitrine.TestcontainersConfiguration;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "SWAGGER_ATIVO=true")
@AutoConfigureMockMvc
@Import(TestcontainersConfiguration.class)
class OpenApiConfigIntegrationTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void documentacao_listaTodosOsEndpointsDaApi() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.info.title").value("Vitrine API"))
				.andExpect(jsonPath("$.paths['/api/categorias']").exists())
				.andExpect(jsonPath("$.paths['/api/produtos']").exists())
				.andExpect(jsonPath("$.paths['/api/produtos/filtros']").exists())
				.andExpect(jsonPath("$.paths['/api/produtos/{slug}']").exists())
				.andExpect(jsonPath("$.paths['/api/produtos/{slug}/relacionados']").exists())
				.andExpect(jsonPath("$.paths['/api/produtos/{slug}/avaliacoes']").exists());
	}

	@Test
	void documentacao_agrupaEDescreveOsEndpointsEmPortugues() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(jsonPath("$.tags[*].name").value(hasItems("Categorias", "Produtos", "Avaliações")))
				.andExpect(jsonPath("$.paths['/api/produtos'].get.summary").value("Lista produtos com filtros, ordenação e paginação"));
	}

	@Test
	void documentacao_mostraOsFiltrosComoParametrosSoltosEAsOrdenacoesDaUrl() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[*].name")
						.value(hasItems("categoria", "busca", "precoMin", "precoMax", "avaliacaoMin", "marcas", "freteGratis", "ordem", "pagina", "tamanho")))
				.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[*].name").value(not(hasItem("faixaDePrecoValida"))))
				.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[?(@.name == 'ordem')].schema.enum[*]").value(hasItem("menor-preco")))
				.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[?(@.name == 'marcas')].schema.items.example").value(hasItem("sonora")));
	}

	@Test
	void documentacao_listaOsErrosDeCadaEndpoint() throws Exception {
		mockMvc.perform(get("/v3/api-docs"))
				.andExpect(jsonPath("$.paths['/api/categorias'].get.responses['400']").exists())
				.andExpect(jsonPath("$.paths['/api/categorias'].get.responses['500']").exists())
				.andExpect(jsonPath("$.paths['/api/categorias'].get.responses['404']").doesNotExist())
				.andExpect(jsonPath("$.paths['/api/produtos/{slug}'].get.responses['404']").exists())
				.andExpect(jsonPath("$.components.schemas.ErroDto").exists())
				.andExpect(jsonPath("$.components.schemas.CampoErroDto").exists());
	}

	@Test
	void telaDoSwagger_estaDisponivel() throws Exception {
		mockMvc.perform(get("/swagger-ui/index.html"))
				.andExpect(status().isOk());
	}

}
