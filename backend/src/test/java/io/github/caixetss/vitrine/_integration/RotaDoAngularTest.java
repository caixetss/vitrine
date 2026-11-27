package io.github.caixetss.vitrine._integration;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.hamcrest.Matchers.containsString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Automatizados de Integração | Rotas do Angular e health check")
class RotaDoAngularTest extends AbstractMvcIntegrationTest {

	@Nested
	@DisplayName("GET /categoria/audio")
	class RotaDoAngular {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Deve devolver o index.html para a rota do Angular abrir a página certa")
			void deveDevolverIndexHtml() throws Exception {
				mockMvc.perform(get("/categoria/audio"))
						.andExpect(status().isOk())
						.andExpect(content().string(containsString("Vitrine de teste")));
			}

		}

	}

	@Nested
	@DisplayName("GET /actuator/health")
	class HealthCheck {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Deve responder UP com o banco no ar")
			void deveResponderUp() throws Exception {
				mockMvc.perform(get("/actuator/health"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$.status").value("UP"));
			}

		}

	}

}
