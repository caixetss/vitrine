package io.github.caixetss.vitrine._integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.empty;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Automatizados de Integração | Rota inexistente")
class RotaInexistenteTest extends AbstractMvcIntegrationTest {

	@Nested
	@DisplayName("GET /api/rota-que-nao-existe")
	class RotaQueNaoExiste {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Deve devolver 404 no formato ErroDto")
			void deveDevolver404NoFormatoErroDto() throws Exception {
				mockMvc.perform(get("/api/rota-que-nao-existe"))
						.andExpect(status().isNotFound())
						.andExpect(jsonPath("$.status").value(404))
						.andExpect(jsonPath("$.erro").value("Não encontrado"))
						.andExpect(jsonPath("$.campos", empty()));
			}

		}

	}

}
