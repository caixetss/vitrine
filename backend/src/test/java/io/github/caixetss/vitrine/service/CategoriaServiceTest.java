package io.github.caixetss.vitrine.service;

import io.github.caixetss.vitrine.dto.CategoriaDto;
import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.repository.CategoriaRepository;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoriaServiceTest {

	@Mock
	private CategoriaRepository categoriaRepository;

	@InjectMocks
	private CategoriaService categoriaService;

	@Test
	void buscaListaCategoria_agrupaSubcategoriasDentroDaPrincipal() {
		Categoria audio = novaCategoria("Áudio", null);
		Categoria celulares = novaCategoria("Celulares", null);
		Categoria fones = novaCategoria("Fones de ouvido", audio);
		Categoria caixas = novaCategoria("Caixas de som", audio);
		when(categoriaRepository.buscaListaCategoriaAtiva())
				.thenReturn(List.of(audio, celulares, fones, caixas));

		List<CategoriaDto> resultado = categoriaService.buscaListaCategoriaEmArvore();

		assertThat(resultado).extracting(CategoriaDto::nome).containsExactly("Áudio", "Celulares");
		assertThat(resultado.get(0).subcategorias()).extracting(CategoriaDto::nome)
				.containsExactly("Fones de ouvido", "Caixas de som");
		assertThat(resultado.get(1).subcategorias()).isEmpty();
	}

	@Test
	void buscaListaCategoria_ignoraSubcategoriaCujaPrincipalNaoVeioNaLista() {
		Categoria principalInativa = novaCategoria("Livros", null);
		Categoria subcategoria = novaCategoria("Ficção", principalInativa);
		when(categoriaRepository.buscaListaCategoriaAtiva())
				.thenReturn(List.of(subcategoria));

		assertThat(categoriaService.buscaListaCategoriaEmArvore()).isEmpty();
	}

	private Categoria novaCategoria(String nome, Categoria categoriaPai) {
		Categoria categoria = BeanUtils.instantiateClass(Categoria.class);
		ReflectionTestUtils.setField(categoria, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(categoria, "nome", nome);
		ReflectionTestUtils.setField(categoria, "categoriaPai", categoriaPai);
		return categoria;
	}

}
