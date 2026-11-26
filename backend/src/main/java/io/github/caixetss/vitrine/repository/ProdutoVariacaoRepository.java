package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.ProdutoVariacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProdutoVariacaoRepository extends JpaRepository<ProdutoVariacao, UUID> {

	@Query("SELECT v FROM ProdutoVariacao v WHERE v.produto.id = :idProduto AND v.ehInativo = false ORDER BY v.nomeCor")
	List<ProdutoVariacao> buscaListaVariacaoAtivaDoProduto(UUID idProduto);

}
