package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.ProdutoEspecificacao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface ProdutoEspecificacaoRepository extends JpaRepository<ProdutoEspecificacao, UUID> {

	@Query("SELECT e FROM ProdutoEspecificacao e WHERE e.produto.id = :idProduto ORDER BY e.ordemExibicao")
	List<ProdutoEspecificacao> buscaListaEspecificacaoDoProduto(UUID idProduto);

}
