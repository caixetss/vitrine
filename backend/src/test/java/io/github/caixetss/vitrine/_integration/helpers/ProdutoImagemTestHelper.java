package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.entidade.ProdutoImagem;
import io.github.caixetss.vitrine.repository.ProdutoImagemRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

@RequiredArgsConstructor
public class ProdutoImagemTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final ProdutoImagemRepository produtoImagemRepository;

	public ProdutoImagem criaImagem(Produto produto, int posicaoGaleria) {
		ProdutoImagem imagem = NovaEntidade.instancia(ProdutoImagem.class);
		ReflectionTestUtils.setField(imagem, "produto", produto);
		ReflectionTestUtils.setField(imagem, "url", FAKER.internet().url());
		ReflectionTestUtils.setField(imagem, "textoAlternativo", FAKER.lorem().sentence());
		ReflectionTestUtils.setField(imagem, "posicaoGaleria", posicaoGaleria);
		return produtoImagemRepository.save(imagem);
	}

}
