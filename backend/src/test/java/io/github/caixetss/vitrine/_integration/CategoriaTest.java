package io.github.caixetss.vitrine._integration;

import io.github.caixetss.vitrine._integration.helpers.CategoriaTestHelper;
import io.github.caixetss.vitrine.entidade.Categoria;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Testes Automatizados de Integração | Categoria")
class CategoriaTest extends AbstractMvcIntegrationTest {

	@Autowired
	private CategoriaTestHelper categoriaTestHelper;

	@Nested
	@DisplayName("GET /api/categorias")
	class BuscaListaCategoriaEmArvore {

		@Nested
		@DisplayName("Service")
		class Service {

			@Test
			@DisplayName("Não deve devolver categoria principal inativa nem as subcategorias dela")
			void naoDeveDevolverCategoriaPrincipalInativaNemAsSubcategoriasDela() throws Exception {
				Categoria principalInativa = categoriaTestHelper.criaCategoriaPrincipalInativa();
				Categoria subcategoriaDaInativa = categoriaTestHelper.criaSubcategoria(principalInativa, 1);

				mockMvc.perform(get("/api/categorias"))
						.andExpect(jsonPath("$[*].slug").value(not(hasItem(principalInativa.getSlug()))))
						.andExpect(jsonPath("$[*].subcategorias[*].slug").value(not(hasItem(subcategoriaDaInativa.getSlug()))));
			}

			@Test
			@DisplayName("Deve devolver as subcategorias dentro da principal na ordem de exibição")
			void deveDevolverAsSubcategoriasDentroDaPrincipalNaOrdemDeExibicao() throws Exception {
				Categoria principal = categoriaTestHelper.criaCategoriaPrincipal();
				Categoria segunda = categoriaTestHelper.criaSubcategoria(principal, 2);
				Categoria primeira = categoriaTestHelper.criaSubcategoria(principal, 1);
				Categoria principalSemSubcategorias = categoriaTestHelper.criaCategoriaPrincipal();

				mockMvc.perform(get("/api/categorias"))
						.andExpect(status().isOk())
						.andExpect(jsonPath("$[?(@.slug == '" + principal.getSlug() + "')].nome").value(contains(principal.getNome())))
						.andExpect(jsonPath("$[?(@.slug == '" + principal.getSlug() + "')].subcategorias[*].slug")
								.value(contains(primeira.getSlug(), segunda.getSlug())))
						.andExpect(jsonPath("$[?(@.slug == '" + principalSemSubcategorias.getSlug() + "')].subcategorias[*]", empty()))
						.andExpect(jsonPath("$[*].slug").value(not(hasItem(primeira.getSlug()))));
			}

		}

	}

}
