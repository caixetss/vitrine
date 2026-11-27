package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Marca;
import io.github.caixetss.vitrine.repository.MarcaRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

@RequiredArgsConstructor
public class MarcaTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final MarcaRepository marcaRepository;

	public Marca criaMarca() {
		Marca marca = NovaEntidade.instancia(Marca.class);
		ReflectionTestUtils.setField(marca, "nome", FAKER.company().name());
		ReflectionTestUtils.setField(marca, "slug", "marca-" + FAKER.number().digits(12));
		ReflectionTestUtils.setField(marca, "ehInativo", false);
		return marcaRepository.save(marca);
	}

}
