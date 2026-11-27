package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.repository.CategoriaRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

@RequiredArgsConstructor
public class CategoriaTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final CategoriaRepository categoriaRepository;

	public Categoria criaCategoriaPrincipal() {
		return salva(null, 0, false);
	}

	public Categoria criaCategoriaPrincipalInativa() {
		return salva(null, 0, true);
	}

	public Categoria criaSubcategoria(Categoria categoriaPai, int ordemExibicao) {
		return salva(categoriaPai, ordemExibicao, false);
	}

	private Categoria salva(Categoria categoriaPai, int ordemExibicao, boolean ehInativo) {
		Categoria categoria = NovaEntidade.instancia(Categoria.class);
		String nome = FAKER.commerce().department() + " " + FAKER.number().digits(8);
		ReflectionTestUtils.setField(categoria, "nome", nome);
		ReflectionTestUtils.setField(categoria, "slug", "categoria-" + FAKER.number().digits(12));
		ReflectionTestUtils.setField(categoria, "icone", "box");
		ReflectionTestUtils.setField(categoria, "categoriaPai", categoriaPai);
		ReflectionTestUtils.setField(categoria, "ordemExibicao", ordemExibicao);
		ReflectionTestUtils.setField(categoria, "ehInativo", ehInativo);
		return categoriaRepository.save(categoria);
	}

}
