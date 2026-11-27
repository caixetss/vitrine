package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Avaliacao;
import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.repository.AvaliacaoRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Instant;
import java.util.Locale;

@RequiredArgsConstructor
public class AvaliacaoTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final AvaliacaoRepository avaliacaoRepository;

	public Avaliacao criaAvaliacao(Produto produto, int nota, Instant criadoEm) {
		Avaliacao avaliacao = NovaEntidade.instancia(Avaliacao.class);
		ReflectionTestUtils.setField(avaliacao, "produto", produto);
		ReflectionTestUtils.setField(avaliacao, "nomeAutor", FAKER.name().firstName());
		ReflectionTestUtils.setField(avaliacao, "nota", nota);
		ReflectionTestUtils.setField(avaliacao, "titulo", FAKER.lorem().sentence(3));
		ReflectionTestUtils.setField(avaliacao, "comentario", FAKER.lorem().paragraph());
		ReflectionTestUtils.setField(avaliacao, "ehCompraVerificada", true);
		ReflectionTestUtils.setField(avaliacao, "criadoEm", criadoEm);
		return avaliacaoRepository.save(avaliacao);
	}

}
