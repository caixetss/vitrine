package io.github.caixetss.vitrine._integration.helpers;

import io.github.caixetss.vitrine.repository.AvaliacaoRepository;
import io.github.caixetss.vitrine.repository.CategoriaRepository;
import io.github.caixetss.vitrine.repository.MarcaRepository;
import io.github.caixetss.vitrine.repository.ProdutoEspecificacaoRepository;
import io.github.caixetss.vitrine.repository.ProdutoImagemRepository;
import io.github.caixetss.vitrine.repository.ProdutoRepository;
import io.github.caixetss.vitrine.repository.ProdutoVariacaoRepository;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration(proxyBeanMethods = false)
public class HelpersConfiguration {

	@Bean
	CategoriaTestHelper categoriaTestHelper(CategoriaRepository categoriaRepository) {
		return new CategoriaTestHelper(categoriaRepository);
	}

	@Bean
	MarcaTestHelper marcaTestHelper(MarcaRepository marcaRepository) {
		return new MarcaTestHelper(marcaRepository);
	}

	@Bean
	ProdutoTestHelper produtoTestHelper(ProdutoRepository produtoRepository) {
		return new ProdutoTestHelper(produtoRepository);
	}

	@Bean
	ProdutoImagemTestHelper produtoImagemTestHelper(ProdutoImagemRepository produtoImagemRepository) {
		return new ProdutoImagemTestHelper(produtoImagemRepository);
	}

	@Bean
	ProdutoVariacaoTestHelper produtoVariacaoTestHelper(ProdutoVariacaoRepository produtoVariacaoRepository) {
		return new ProdutoVariacaoTestHelper(produtoVariacaoRepository);
	}

	@Bean
	ProdutoEspecificacaoTestHelper produtoEspecificacaoTestHelper(ProdutoEspecificacaoRepository produtoEspecificacaoRepository) {
		return new ProdutoEspecificacaoTestHelper(produtoEspecificacaoRepository);
	}

	@Bean
	AvaliacaoTestHelper avaliacaoTestHelper(AvaliacaoRepository avaliacaoRepository) {
		return new AvaliacaoTestHelper(avaliacaoRepository);
	}

}
