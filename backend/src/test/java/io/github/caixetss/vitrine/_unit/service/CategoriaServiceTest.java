package io.github.caixetss.vitrine._unit.service;

import io.github.caixetss.vitrine._unit.helpers.CategoriaTestHelper;
import io.github.caixetss.vitrine.dto.CategoriaDto;
import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.repository.CategoriaRepository;
import io.github.caixetss.vitrine.service.CategoriaService;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Automatizados Unitários | CategoriaService")
class CategoriaServiceTest {

	@Mock
	private CategoriaRepository categoriaRepository;

	private CategoriaService categoriaService;

	@BeforeEach
	void setUp() {
		this.categoriaService = new CategoriaService(this.categoriaRepository);
	}

	@Nested
	@DisplayName("buscaListaCategoriaEmArvore")
	class BuscaListaCategoriaEmArvore {

		@Test
		@DisplayName("Não deve devolver subcategoria cuja categoria principal não veio na lista de ativas")
		void naoDeveDevolverSubcategoriaCujaCategoriaPrincipalNaoVeioNaLista() {
			Categoria principalInativa = CategoriaTestHelper.criaCategoriaPrincipal();
			Categoria subcategoria = CategoriaTestHelper.criaSubcategoria(principalInativa);
			when(categoriaRepository.buscaListaCategoriaAtiva()).thenReturn(List.of(subcategoria));

			assertThat(categoriaService.buscaListaCategoriaEmArvore()).isEmpty();
		}

		@Test
		@DisplayName("Deve devolver categoria principal sem subcategorias com lista vazia")
		void deveDevolverCategoriaPrincipalSemSubcategoriasComListaVazia() {
			Categoria principal = CategoriaTestHelper.criaCategoriaPrincipal();
			when(categoriaRepository.buscaListaCategoriaAtiva()).thenReturn(List.of(principal));

			List<CategoriaDto> resultado = categoriaService.buscaListaCategoriaEmArvore();

			assertThat(resultado).singleElement().satisfies(categoria -> assertThat(categoria.subcategorias()).isEmpty());
		}

		@Test
		@DisplayName("Deve agrupar as subcategorias dentro da principal mantendo a ordem recebida")
		void deveAgruparSubcategoriasDentroDaPrincipalMantendoAOrdemRecebida() {
			Categoria audio = CategoriaTestHelper.criaCategoriaPrincipal();
			Categoria celulares = CategoriaTestHelper.criaCategoriaPrincipal();
			Categoria fones = CategoriaTestHelper.criaSubcategoria(audio);
			Categoria caixas = CategoriaTestHelper.criaSubcategoria(audio);
			when(categoriaRepository.buscaListaCategoriaAtiva()).thenReturn(List.of(audio, celulares, fones, caixas));

			List<CategoriaDto> resultado = categoriaService.buscaListaCategoriaEmArvore();

			assertThat(resultado).extracting(CategoriaDto::id).containsExactly(audio.getId(), celulares.getId());
			assertThat(resultado.get(0).subcategorias()).extracting(CategoriaDto::id).containsExactly(fones.getId(), caixas.getId());
			assertThat(resultado.get(1).subcategorias()).isEmpty();
		}

	}

}
