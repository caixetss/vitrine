package io.github.caixetss.vitrine._unit.helpers;

import io.github.caixetss.vitrine.entidade.Categoria;

import net.datafaker.Faker;
import org.springframework.beans.BeanUtils;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;
import java.util.UUID;

public final class CategoriaTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private CategoriaTestHelper() {
	}

	public static Categoria criaCategoriaPrincipal() {
		return criaCategoria(null);
	}

	public static Categoria criaSubcategoria(Categoria categoriaPai) {
		return criaCategoria(categoriaPai);
	}

	private static Categoria criaCategoria(Categoria categoriaPai) {
		Categoria categoria = BeanUtils.instantiateClass(Categoria.class);
		ReflectionTestUtils.setField(categoria, "id", UUID.randomUUID());
		ReflectionTestUtils.setField(categoria, "nome", FAKER.commerce().department() + " " + FAKER.number().digits(6));
		ReflectionTestUtils.setField(categoria, "slug", "categoria-" + FAKER.number().digits(12));
		ReflectionTestUtils.setField(categoria, "categoriaPai", categoriaPai);
		return categoria;
	}

}
