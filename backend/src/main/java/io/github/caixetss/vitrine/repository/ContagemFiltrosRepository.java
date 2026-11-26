package io.github.caixetss.vitrine.repository;

import io.github.caixetss.vitrine.dto.OpcaoFiltroDto;
import io.github.caixetss.vitrine.entidade.Produto;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Root;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
@RequiredArgsConstructor
public class ContagemFiltrosRepository {

	private final EntityManager entityManager;

	public List<OpcaoFiltroDto> contaProdutosPorSubcategoria(Specification<Produto> condicoes) {
		return contaProdutosAgrupadosPor("categoria", "ordemExibicao", condicoes);
	}

	public List<OpcaoFiltroDto> contaProdutosPorMarca(Specification<Produto> condicoes) {
		return contaProdutosAgrupadosPor("marca", "nome", condicoes);
	}

	public FaixaDePreco buscaMenorEMaiorPreco(Specification<Produto> condicoes) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<FaixaDePreco> consulta = cb.createQuery(FaixaDePreco.class);
		Root<Produto> produto = consulta.from(Produto.class);
		consulta.select(cb.construct(FaixaDePreco.class, cb.min(produto.get("preco")), cb.max(produto.get("preco"))))
				.where(condicoes.toPredicate(produto, consulta, cb));
		return entityManager.createQuery(consulta).getSingleResult();
	}

	public long contaProdutosComFreteGratis(Specification<Produto> condicoes) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Long> consulta = cb.createQuery(Long.class);
		Root<Produto> produto = consulta.from(Produto.class);
		consulta.select(cb.count(produto))
				.where(condicoes.and(ProdutoSpecifications.filtraPorFreteGratis()).toPredicate(produto, consulta, cb));
		return entityManager.createQuery(consulta).getSingleResult();
	}

	private List<OpcaoFiltroDto> contaProdutosAgrupadosPor(String relacionamento, String campoDeOrdenacao, Specification<Produto> condicoes) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<OpcaoFiltroDto> consulta = cb.createQuery(OpcaoFiltroDto.class);
		Root<Produto> produto = consulta.from(Produto.class);
		Join<Produto, ?> grupo = produto.join(relacionamento);
		consulta.select(cb.construct(OpcaoFiltroDto.class, grupo.get("slug"), grupo.get("nome"), cb.count(produto)))
				.where(condicoes.toPredicate(produto, consulta, cb))
				.groupBy(grupo.get("id"), grupo.get("slug"), grupo.get("nome"), grupo.get(campoDeOrdenacao))
				.orderBy(cb.asc(grupo.get(campoDeOrdenacao)));
		return entityManager.createQuery(consulta).getResultList();
	}

	public record FaixaDePreco(BigDecimal minimo, BigDecimal maximo) {
	}

}
