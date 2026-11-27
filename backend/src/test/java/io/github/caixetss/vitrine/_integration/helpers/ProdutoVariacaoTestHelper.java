package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.entidade.ProdutoVariacao;
import io.github.caixetss.vitrine.repository.ProdutoVariacaoRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

@RequiredArgsConstructor
public class ProdutoVariacaoTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final ProdutoVariacaoRepository produtoVariacaoRepository;

	public ProdutoVariacao criaVariacao(Produto produto, String nomeCor) {
		return salva(produto, nomeCor, false);
	}

	public ProdutoVariacao criaVariacaoInativa(Produto produto, String nomeCor) {
		return salva(produto, nomeCor, true);
	}

	private ProdutoVariacao salva(Produto produto, String nomeCor, boolean ehInativo) {
		ProdutoVariacao variacao = NovaEntidade.instancia(ProdutoVariacao.class);
		ReflectionTestUtils.setField(variacao, "produto", produto);
		ReflectionTestUtils.setField(variacao, "nomeCor", nomeCor);
		ReflectionTestUtils.setField(variacao, "corHex", "#111827");
		ReflectionTestUtils.setField(variacao, "codigoEstoque", "TESTE-" + FAKER.number().digits(12));
		ReflectionTestUtils.setField(variacao, "estoque", FAKER.number().numberBetween(1, 50));
		ReflectionTestUtils.setField(variacao, "ehInativo", ehInativo);
		return produtoVariacaoRepository.save(variacao);
	}

}
