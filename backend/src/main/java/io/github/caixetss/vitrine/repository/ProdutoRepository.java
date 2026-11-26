package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.entidade.Categoria;
import io.github.caixetss.vitrine.entidade.Produto;

import org.springframework.data.domain.Limit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID>, JpaSpecificationExecutor<Produto> {

	@Query("""
			SELECT p FROM Produto p
			JOIN FETCH p.marca
			JOIN FETCH p.categoria c
			LEFT JOIN FETCH c.categoriaPai
			WHERE p.slug = :slug AND p.ehInativo = false
			""")
	Optional<Produto> buscaIndividualProdutoAtivo(String slug);

	@Query("""
			SELECT p FROM Produto p
			WHERE p.categoria = :categoria AND p.id <> :idProdutoAtual AND p.ehInativo = false
			ORDER BY p.totalVendidos DESC, p.nome
			""")
	List<Produto> buscaListaProdutoDaMesmaCategoria(Categoria categoria, UUID idProdutoAtual, Limit limite);

}
