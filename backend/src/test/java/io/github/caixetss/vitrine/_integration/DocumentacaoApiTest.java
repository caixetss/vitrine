package io.github.caixetss.vitrine._integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.TestPropertySource;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@TestPropertySource(properties = "SWAGGER_ATIVO=true")
@DisplayName("Testes Automatizados de Integração | Documentação da API ligada")
class DocumentacaoApiTest extends AbstractMvcIntegrationTest {

	@Nested
	@DisplayName("GET /v3/api-docs")
	class BuscaContratoOpenApi {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Deve listar todos os endpoints da API")
			void deveListarTodosOsEndpointsDaApi() throws Exception {
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
			@DisplayName("Deve agrupar e descrever os endpoints em português")
			void deveAgruparEDescreverOsEndpointsEmPortugues() throws Exception {
				mockMvc.perform(get("/v3/api-docs"))
						.andExpect(jsonPath("$.tags[*].name").value(hasItems("Categorias", "Produtos", "Avaliações")))
						.andExpect(jsonPath("$.paths['/api/produtos'].get.summary").value("Lista produtos com filtros, ordenação e paginação"));
			}

			@Test
			@DisplayName("Deve mostrar os filtros como parâmetros soltos e as ordenações como aparecem na URL")
			void deveMostrarOsFiltrosComoParametrosSoltosEAsOrdenacoesComoAparecemNaUrl() throws Exception {
				mockMvc.perform(get("/v3/api-docs"))
						.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[*].name")
								.value(hasItems("categoria", "busca", "precoMin", "precoMax", "avaliacaoMin", "marcas", "freteGratis", "ordem", "pagina", "tamanho")))
						.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[*].name").value(not(hasItem("faixaDePrecoValida"))))
						.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[?(@.name == 'ordem')].schema.enum[*]").value(hasItem("menor-preco")))
						.andExpect(jsonPath("$.paths['/api/produtos'].get.parameters[?(@.name == 'marcas')].schema.items.example").value(hasItem("sonora")));
			}

			@Test
			@DisplayName("Deve documentar os erros de cada endpoint no formato ErroDto")
			void deveDocumentarOsErrosDeCadaEndpointNoFormatoErroDto() throws Exception {
				mockMvc.perform(get("/v3/api-docs"))
						.andExpect(jsonPath("$.paths['/api/categorias'].get.responses['400']").exists())
						.andExpect(jsonPath("$.paths['/api/categorias'].get.responses['500']").exists())
						.andExpect(jsonPath("$.paths['/api/categorias'].get.responses['404']").doesNotExist())
						.andExpect(jsonPath("$.paths['/api/produtos/{slug}'].get.responses['404']").exists())
						.andExpect(jsonPath("$.components.schemas.ErroDto").exists())
						.andExpect(jsonPath("$.components.schemas.CampoErroDto").exists());
			}

		}

	}

	@Nested
	@DisplayName("GET /swagger-ui/index.html")
	class BuscaTelaDoSwagger {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Deve abrir a tela do Swagger")
			void deveAbrirATelaDoSwagger() throws Exception {
				mockMvc.perform(get("/swagger-ui/index.html"))
						.andExpect(status().isOk());
			}

		}

	}

}
