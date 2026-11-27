package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.entidade.Produto;
import io.github.caixetss.vitrine.entidade.ProdutoEspecificacao;
import io.github.caixetss.vitrine.repository.ProdutoEspecificacaoRepository;

import lombok.RequiredArgsConstructor;
import net.datafaker.Faker;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Locale;

@RequiredArgsConstructor
public class ProdutoEspecificacaoTestHelper {

	private static final Faker FAKER = new Faker(Locale.of("pt", "BR"));

	private final ProdutoEspecificacaoRepository produtoEspecificacaoRepository;

	public ProdutoEspecificacao criaEspecificacao(Produto produto, String nome, int ordemExibicao) {
		ProdutoEspecificacao especificacao = NovaEntidade.instancia(ProdutoEspecificacao.class);
		ReflectionTestUtils.setField(especificacao, "produto", produto);
		ReflectionTestUtils.setField(especificacao, "nome", nome);
		ReflectionTestUtils.setField(especificacao, "valor", FAKER.lorem().word());
		ReflectionTestUtils.setField(especificacao, "ordemExibicao", ordemExibicao);
		return produtoEspecificacaoRepository.save(especificacao);
	}

}
