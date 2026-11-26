package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.dto.QuantidadePorNotaDto;
import io.github.caixetss.vitrine.entidade.Avaliacao;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.UUID;

public interface AvaliacaoRepository extends JpaRepository<Avaliacao, UUID> {

	@Query("SELECT a FROM Avaliacao a WHERE a.produto.id = :idProduto ORDER BY a.criadoEm DESC, a.id")
	Page<Avaliacao> buscaListaAvaliacaoMaisRecenteDoProduto(UUID idProduto, Pageable paginacao);

	@Query("""
			SELECT new io.github.caixetss.vitrine.dto.QuantidadePorNotaDto(a.nota, COUNT(a))
			FROM Avaliacao a
			WHERE a.produto.id = :idProduto
			GROUP BY a.nota
			""")
	List<QuantidadePorNotaDto> contaAvaliacoesPorNota(UUID idProduto);

}
