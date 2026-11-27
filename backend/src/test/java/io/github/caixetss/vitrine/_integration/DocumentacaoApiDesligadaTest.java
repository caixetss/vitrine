package io.github.caixetss.vitrine._integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Automatizados de Integração | Documentação da API sem configuração")
class DocumentacaoApiDesligadaTest extends AbstractMvcIntegrationTest {

	@Nested
	@DisplayName("GET /v3/api-docs")
	class BuscaContratoOpenApi {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Não deve expor o contrato quando o Swagger não foi ligado")
			void naoDeveExporOContratoQuandoOSwaggerNaoFoiLigado() throws Exception {
				mockMvc.perform(get("/v3/api-docs"))
						.andExpect(status().isNotFound());
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
			@DisplayName("Não deve abrir a tela do Swagger quando ele não foi ligado")
			void naoDeveAbrirATelaDoSwaggerQuandoEleNaoFoiLigado() throws Exception {
				mockMvc.perform(get("/swagger-ui/index.html"))
						.andExpect(status().isNotFound());
			}

		}

	}

}
